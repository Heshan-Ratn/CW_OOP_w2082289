import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class TicketPool {
    private static TicketPool instance;  // Singleton instance
    private final ConcurrentLinkedQueue<Ticket> tickets = new ConcurrentLinkedQueue<>();
    private long maxCapacity;
    private static final String TICKETS_FILE = "Tickets.json";
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    // Locks and Conditions for both vendors and consumers
    private final Lock ticketLock = new ReentrantLock();
    private final Condition vendorCondition = ticketLock.newCondition();  // Condition for vendors to wait
    private final Map<String, Condition> consumerConditions = new HashMap<>(); // Map of Conditions for each event
    private final Map<String, Integer> eventUsageCount = new HashMap<>();


    private static final Logger logger = LogManager.getLogger(TicketPool.class);
    private static final Logger loggerAdd = LogManager.getLogger("TicketPoolAdd");
    private static final Logger loggerSave = LogManager.getLogger("TicketPoolSave");
    private static final Logger loggerRemove = LogManager.getLogger("TicketPoolRemove");

    private TicketPool(long maxCapacity) {
        this.maxCapacity = maxCapacity;
        loadTickets();  // Load tickets from JSON file
    }

    // Singleton instance retrieval
    public static TicketPool getInstance(long maxCapacity) {
        if (instance == null) {
            instance = new TicketPool(maxCapacity);
        }
        return instance;
    }

    public Set<String> getEventDetails() {
        ticketLock.lock();  // Locking to ensure thread-safe reading
        try {
            return tickets.stream()
                    .map(Ticket::getEventName)
                    .collect(Collectors.toSet());
        } finally {
            ticketLock.unlock();  // Unlock after reading
        }
    }

    // Load tickets from the JSON file
    private void loadTickets() {
        ticketLock.lock();  // Locking to ensure thread-safe modification of the tickets list
        try {
            try (FileReader reader = new FileReader(TICKETS_FILE)) {
                Type ticketListType = new TypeToken<List<Ticket>>() {}.getType();
                List<Ticket> loadedTickets = gson.fromJson(reader, ticketListType);
                if (loadedTickets != null) {
                    tickets.addAll(loadedTickets);
                }
                System.out.println("Loaded tickets from file.");
            } catch (IOException | com.google.gson.JsonSyntaxException e) {
                System.out.println("Could not load tickets: " + e.getMessage());
            }
        } finally {
            ticketLock.unlock();  // Unlock after loading
        }
    }

    // Count available tickets
    public int countAvailableTickets() {
        ticketLock.lock();  // Locking before reading
        try {
            return (int) tickets.stream().filter(ticket -> "Available".equals(ticket.getTicketStatus())).count();
        } finally {
            ticketLock.unlock();  // Always unlock after reading
        }
    }

    // Add ticket to the pool (Vendor method)
    public boolean addTicket(Ticket ticket) {
        ticketLock.lock();
        try {
            // If the pool is full, vendor will wait until a customer removes a ticket
            while (countAvailableTickets() >= maxCapacity) {
                loggerAdd.info("Ticket pool is full. Vendor: "+ ticket.getVendorId()+ " is waiting...");
                vendorCondition.await();  // Vendor waits
            }

            tickets.add(ticket);
            loggerAdd.info("Ticket added to pool: " + ticket.getTicketId() + " " + ticket.getEventName());
            saveTickets();  // Save tickets after addition

            // Notify all consumers waiting for a specific event
            notifyConsumersForEvent(ticket.getEventName());  // Notify only consumers looking for this event

            // Cleanup unused conditions
            cleanupUnusedConditions();
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } finally {
            ticketLock.unlock();
        }
    }

    // Notify consumers waiting for a specific event type
    private void notifyConsumersForEvent(String eventName) {
        ticketLock.lock();  // Locking before modifying the consumerConditions map
        try {
            // Notify consumers waiting for tickets for the event
            Condition eventCondition = consumerConditions.get(eventName);
            if (eventCondition != null) {
                eventCondition.signalAll();
            }

            // Cleanup the event condition if there are no more waiting consumers
            Integer usageCount = eventUsageCount.get(eventName);
            if (usageCount != null && usageCount <= 0) {
                // No more consumers are waiting for this event, clean up the resources
                consumerConditions.remove(eventName);
                eventUsageCount.remove(eventName);
            }
        } finally {
            ticketLock.unlock();  // Unlock after notifying
        }
    }

    // Check if a ticket is available for a specific event
    public boolean isTicketAvailable(String eventName) {
        ticketLock.lock();  // Locking to ensure thread-safe reading
        try {
            return tickets.stream().anyMatch(t -> "Available".equals(t.getTicketStatus()) && eventName.equals(t.getEventName()));
        } finally {
            ticketLock.unlock();  // Unlock after reading
        }
    }

    // Find and remove a ticket from the pool (Consumer method)
    public boolean removeTicket(String eventName, String customerId) {
        ticketLock.lock();
        try {
            // Consumer waits if no ticket is available for the specific event
            while (!isTicketAvailable(eventName)) {
                System.out.println("No tickets available for event: " + eventName + ". Consumer waiting...");
                waitForSpecificEventTicket(eventName);  // Consumer waits for a ticket for this specific event
            }

            Ticket ticket = findAvailableTicket(eventName);
            if (ticket != null) {
                ticket.setTicketStatus("Booked");
                ticket.setCustomerId(customerId);
                loggerRemove.info("Ticket booked: " + ticket.getTicketId() + " for customer: " + customerId + " (Event: " + eventName + ")");
                saveTickets();

                // Notify vendors that space is now available (i.e., a ticket has been booked)
                vendorCondition.signalAll();  // Notify vendors to potentially add new tickets

                // Cleanup unused conditions
                cleanupUnusedConditions();
                return true;
            }
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } finally {
            ticketLock.unlock();
        }
    }

    // Wait for a specific event ticket to become available (Consumer-specific logic)
    private void waitForSpecificEventTicket(String eventName) throws InterruptedException {
        ticketLock.lock();
        try {
            // If no ticket is available for the event, create a new condition for this event if not already created
            consumerConditions.putIfAbsent(eventName, ticketLock.newCondition());
            // Increment the usage counter
            eventUsageCount.put(eventName, eventUsageCount.getOrDefault(eventName, 0) + 1);
            // Now wait until the ticket for this event is available
            consumerConditions.get(eventName).await();
        } finally {
            ticketLock.unlock();
        }
    }

    // Helper method to find available ticket for a specific event
    private Ticket findAvailableTicket(String eventName) {
        ticketLock.lock();  // Locking to ensure thread-safe reading
        try {
            return tickets.stream()
                    .filter(t -> "Available".equals(t.getTicketStatus()) && eventName.equals(t.getEventName()))
                    .findFirst()
                    .orElse(null);
        } finally {
            ticketLock.unlock();  // Unlock after reading
        }
    }

    private void cleanupUnusedConditions() {
        ticketLock.lock(); // Ensure thread safety during cleanup
        try {
            // Iterate over the usage counter map
            Iterator<Map.Entry<String, Integer>> iterator = eventUsageCount.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Integer> entry = iterator.next();

                // Remove conditions for events with no waiting consumers
                if (entry.getValue() <= 0) {
                    consumerConditions.remove(entry.getKey());
                    iterator.remove(); // Safely remove from the map
                }
            }
        } finally {
            ticketLock.unlock();
        }
    }


    // Save the current list of tickets to the JSON file
    private void saveTickets() {
        ticketLock.lock();  // Lock here to ensure no other thread modifies the tickets while saving
        try (FileWriter writer = new FileWriter(TICKETS_FILE)) {
            gson.toJson(tickets, writer);
            loggerSave.info("Tickets saved to file.");
        } catch (IOException e) {
            loggerSave.error("Could not save tickets: " + e.getMessage());
        } finally {
            ticketLock.unlock();  // Unlock after saving
        }
    }

    // Display available tickets by vendor
    public void viewAvailableTicketCountsByVendor(String vendorId) {
        ticketLock.lock();  // Lock here to ensure no modifications happen during iteration
        try {
            Map<String, Integer> ticketTypeCounts = getTicketCountsByVendor(vendorId);
            if (ticketTypeCounts.isEmpty()) {
                System.out.println("No available tickets found for vendor " + vendorId + ".");
            } else {
                System.out.println("Available tickets by vendor " + vendorId + ":");
                ticketTypeCounts.forEach((type, count) -> System.out.println(type + ": " + count));
            }
        } finally {
            ticketLock.unlock();  // Unlock after reading
        }
    }

    // Get ticket counts by vendor
    private Map<String, Integer> getTicketCountsByVendor(String vendorId) {
        Map<String, Integer> ticketTypeCounts = new HashMap<>();

        for (Ticket ticket : tickets) {
            if (vendorId.equals(ticket.getVendorId()) && "Available".equals(ticket.getTicketStatus())) {
                String ticketType = ticket.getEventName(); // Using event name as ticket type
                ticketTypeCounts.put(ticketType, ticketTypeCounts.getOrDefault(ticketType, 0) + 1);
            }
        }
        return ticketTypeCounts;
    }

    // Count available tickets by event
    public void countAvailableTicketsByEvent() {
        ticketLock.lock();  // Lock to ensure thread-safe reading
        try {
            Map<String, Integer> availableTicketsByEvent = new HashMap<>();
            for (Ticket ticket : tickets) {
                if ("Available".equals(ticket.getTicketStatus())) {
                    String eventName = ticket.getEventName();
                    availableTicketsByEvent.put(eventName, availableTicketsByEvent.getOrDefault(eventName, 0) + 1);
                }
            }
            if (availableTicketsByEvent.isEmpty()) {
                System.out.println("No available tickets found in the ticket pool.");
            } else {
                System.out.println("Available tickets by event:");
                availableTicketsByEvent.forEach((eventName, count) -> System.out.println(eventName + ": " + count));
            }
        } finally {
            ticketLock.unlock();  // Unlock after reading
        }
    }

    public void countBookedTicketsByEvent() {
        ticketLock.lock();  // Lock to ensure thread-safe reading
        try {
            Map<String, Integer> bookedTicketsByEvent = new HashMap<>();
            for (Ticket ticket : tickets) {
                if ("Booked".equals(ticket.getTicketStatus())) {
                    String eventName = ticket.getEventName();
                    bookedTicketsByEvent.put(eventName, bookedTicketsByEvent.getOrDefault(eventName, 0) + 1);
                }
            }
            if (bookedTicketsByEvent.isEmpty()) {
                System.out.println("No booked tickets found in the ticket pool.");
            } else {
                System.out.println("Booked tickets by event:");
                bookedTicketsByEvent.forEach((eventName, count) -> System.out.println(eventName + ": " + count));
            }
        } finally {
            ticketLock.unlock();  // Unlock after reading
        }
    }


    public void countBookedTicketsByCustomerId(String customerId) {
        ticketLock.lock();
        try {
            // Group the tickets by event and count the tickets booked by the specific customer
            Map<String, Long> ticketsByEvent = tickets.stream()
                    .filter(ticket -> "Booked".equals(ticket.getTicketStatus()) && customerId.equals(ticket.getCustomerId()))
                    .collect(Collectors.groupingBy(Ticket::getEventName, Collectors.counting()));

            if (ticketsByEvent.isEmpty()) {
                System.out.println("No tickets found for customer ID: " + customerId);
            } else {
                System.out.println("Tickets booked by customer ID: " + customerId);
                ticketsByEvent.forEach((event, count) ->
                        System.out.println("Event: " + event + ", Tickets Booked: " + count));
            }
        } finally {
            ticketLock.unlock();
        }
    }
}
