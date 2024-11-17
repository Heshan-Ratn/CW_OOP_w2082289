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
import java.util.stream.Collectors;

public class TicketPool {
    private static TicketPool instance;  // Single instance for the Singleton pattern
    private final List<Ticket> tickets = java.util.Collections.synchronizedList(new ArrayList<>());
    private long maxCapacity;
    private static final String TICKETS_FILE = "Tickets.json";
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private static final Logger logger = LogManager.getLogger(TicketPool.class);  // Logger for general class
    private static final Logger loggerAdd = LogManager.getLogger("TicketPoolAdd"); // Custom logger for add ticket logs
    private static final Logger loggerSave = LogManager.getLogger("TicketPoolSave");
    private static final Logger loggerRemove = LogManager.getLogger("TicketPoolRemove");

    // Private constructor to prevent direct instantiation
    private TicketPool(long maxCapacity) {
        this.maxCapacity = maxCapacity;
        loadTickets();  // Load tickets from the JSON file on initialization
    }

    // Public method to get the single instance of TicketPool
    public static synchronized TicketPool getInstance(long maxCapacity) {
        if (instance == null) {
            instance = new TicketPool(maxCapacity);
        }
        return instance;
    }

    public synchronized long getMaxCapacity() {
        return maxCapacity;
    }

    public synchronized void setMaxCapacity(long maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public synchronized Set<String> getEventDetails() {
        return tickets.stream()
                .map(Ticket::getEventName)  // Assuming `getEventName` returns the event name for a ticket
                .collect(Collectors.toSet());
    }

    // Adds a ticket to the pool if it doesn't exceed max capacity, returns true if successful, false otherwise
    public synchronized boolean addTicket(Ticket ticket) {
        if (countAvailableTickets() < maxCapacity) {
            tickets.add(ticket);
            loggerAdd.info("Ticket added to pool: " + ticket.getTicketId() + " "+ticket.getEventName());
            saveTickets();  // Save tickets to the JSON file after adding a new one
            return true;
        } else {
            System.out.println("Cannot add ticket: Pool is at max capacity.");
            return false;
        }
    }

    // Loads tickets from JSON file into the ticket pool at initialization
    private void loadTickets() {
        try (FileReader reader = new FileReader(TICKETS_FILE)) {
            Type ticketListType = new TypeToken<List<Ticket>>() {}.getType();
            List<Ticket> loadedTickets = gson.fromJson(reader, ticketListType);
            if (loadedTickets != null) {
                tickets.addAll(loadedTickets);
            }
            System.out.println("Loaded tickets from file.");
        } catch (IOException e) {
            System.out.println("Could not load tickets: " + e.getMessage());
        }
    }

    // Counts and returns the number of tickets with status "Available"
    public synchronized int countAvailableTickets() {
        return (int) tickets.stream().filter(ticket -> "Available".equals(ticket.getTicketStatus())).count();
    }


    public synchronized boolean removeTicket(String eventName, String customerId) {
        // Find the first available ticket for the specified event
        Ticket ticket = tickets.stream()
                .filter(t -> "Available".equals(t.getTicketStatus()) && eventName.equals(t.getEventName()))
                .findFirst()
                .orElse(null);  // If no ticket is found, return null

        if (ticket != null) {
            // Book the ticket by changing its status and setting the customer ID
            ticket.setTicketStatus("Booked");
            ticket.setCustomerId(customerId);
            loggerRemove.info("Ticket booked: " + ticket.getTicketId() + " for customer: " + customerId + " (Event: " + eventName + ")");

            // Save the updated list of tickets to the file after the change
            saveTickets();
            return true;
        } else {
            System.out.println("No available tickets for event: " + eventName);
            return false;
        }
    }


    // Saves the current list of tickets in the pool to the JSON file
    public synchronized void saveTickets() {
        try (FileWriter writer = new FileWriter(TICKETS_FILE)) {
            gson.toJson(tickets, writer);
            loggerSave.info("Tickets saved to file.");
        } catch (IOException e) {
            loggerSave.error("Could not save tickets: " + e.getMessage());
        }
    }



    // Method to display counts of each type of "Available" ticket for a vendor
    public synchronized void viewAvailableTicketCountsByVendor(String vendorId) {
        Map<String, Integer> ticketTypeCounts = getStringIntegerMap(vendorId);

        if (ticketTypeCounts.isEmpty()) {
            System.out.println("No available tickets found for vendor " + vendorId + ".");
        } else {
            // Print the count of each ticket type
            System.out.println("Available tickets by vendor " + vendorId + ":");
            ticketTypeCounts.forEach((type, count) -> System.out.println(type + ": " + count));
        }
    }

    private Map<String, Integer> getStringIntegerMap(String vendorId) {
        Map<String, Integer> ticketTypeCounts = new HashMap<>();


        for (Ticket ticket : tickets) {
            if (vendorId.equals(ticket.getVendorId()) && "Available".equals(ticket.getTicketStatus())) {
                String ticketType = ticket.getEventName(); // Assuming Ticket class has a getType() method

                // Increment the count for this ticket type
                ticketTypeCounts.put(ticketType, ticketTypeCounts.getOrDefault(ticketType, 0) + 1);
            }
        }
        return ticketTypeCounts;
    }


    public synchronized void countAvailableTicketsByEvent() {
        Map<String, Integer> availableTicketsByEvent = new HashMap<>();

        for (Ticket ticket : tickets) {
            if ("Available".equals(ticket.getTicketStatus())) {
                String eventName = ticket.getEventName();
                availableTicketsByEvent.put(eventName, availableTicketsByEvent.getOrDefault(eventName, 0) + 1);
            }
        }

        if (availableTicketsByEvent.isEmpty()) {
            System.out.println("No available tickets found in the ticket pool.\n");
        } else {
            // Print the count of each ticket type
            System.out.println("Available tickets by event:");
            availableTicketsByEvent.forEach((eventName, count) ->
                    System.out.println("Event: " + eventName + ", Available Tickets: " + count)
            );
        }
    }

//    public synchronized int countBookedTicketsByCustomerId(String customerId) {
//        return (int) tickets.stream()
//                .filter(ticket -> "Booked".equals(ticket.getTicketStatus()) && customerId.equals(ticket.getCustomerId()))
//                .count();
//    }

    public synchronized void countBookedTicketsByCustomerId(String customerId) {
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
    }

}

