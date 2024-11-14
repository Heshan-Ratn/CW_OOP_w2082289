import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TicketPool {
    private static TicketPool instance;  // Single instance for the Singleton pattern
    private final List<Ticket> tickets = java.util.Collections.synchronizedList(new ArrayList<>());
    private long maxCapacity;
    private static final String TICKETS_FILE = "Tickets.json";
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

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

    public long getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(long maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    // Adds a ticket to the pool if it doesn't exceed max capacity, returns true if successful, false otherwise
    public synchronized boolean addTicket(Ticket ticket) {
        if (countAvailableTickets() < maxCapacity) {
            tickets.add(ticket);
            System.out.println("Ticket added to pool: " + ticket.getTicketId());
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

    // Removes tickets based on eventName, changes status to "Booked" and assigns customerId if available
//    public synchronized boolean removeTickets(String eventName, String customerId, int numTickets) {
//        List<Ticket> availableTickets = tickets.stream()
//                .filter(ticket -> "Available".equals(ticket.getTicketStatus()) && eventName.equals(ticket.getEventName()))
//                .collect(Collectors.toList());
//
//        if (availableTickets.size() >= numTickets) {
//            for (int i = 0; i < numTickets; i++) {
//                Ticket ticket = availableTickets.get(i);
//                ticket.setTicketStatus("Booked");
//                ticket.setCustomerId(customerId);
//                System.out.println("Ticket booked: " + ticket.getTicketId() + " for customer: " + customerId);
//            }
//            // Save the updated list of tickets to the file after the change
//            saveTickets();
//            return true;
//        } else {
//            System.out.println("Not enough tickets available for event: " + eventName);
//            return false;
//        }
//    }

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
            System.out.println("Ticket booked: " + ticket.getTicketId() + " for customer: " + customerId);

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
            System.out.println("Tickets saved to file.");
        } catch (IOException e) {
            System.out.println("Could not save tickets: " + e.getMessage());
        }
    }

//    // Method to retrieve tickets based on vendorId
//    public synchronized void viewTicketsByVendor(String vendorId) {
//        System.out.println("Tickets released by vendor " + vendorId + ":");
//
//        for (Ticket ticket : tickets) {
//            if (vendorId.equals(ticket.getVendorId())) {
//                System.out.println(ticket);
//            }
//        }
//    }

    // Method to display counts of each type of "Available" ticket for a vendor
    public synchronized void viewAvailableTicketCountsByVendor(String vendorId) {
        Map<String, Integer> ticketTypeCounts = new HashMap<>();

        System.out.println("Available tickets by vendor " + vendorId + ":");

        for (Ticket ticket : tickets) {
            if (vendorId.equals(ticket.getVendorId()) && "Available".equals(ticket.getTicketStatus())) {
                String ticketType = ticket.getEventName(); // Assuming Ticket class has a getType() method

                // Increment the count for this ticket type
                ticketTypeCounts.put(ticketType, ticketTypeCounts.getOrDefault(ticketType, 0) + 1);
            }
        }

        if (ticketTypeCounts.isEmpty()) {
            System.out.println("No available tickets found for vendor " + vendorId + ".");
        } else {
            // Print the count of each ticket type
            ticketTypeCounts.forEach((type, count) -> System.out.println(type + ": " + count));
        }
    }


    public synchronized void countAvailableTicketsByEvent() {
        Map<String, Integer> availableTicketsByEvent = new HashMap<>();

        for (Ticket ticket : tickets) {
            if ("Available".equals(ticket.getTicketStatus())) {
                String eventName = ticket.getEventName();
                availableTicketsByEvent.put(eventName, availableTicketsByEvent.getOrDefault(eventName, 0) + 1);
            }
        }

        System.out.println("Available tickets by event:");
        availableTicketsByEvent.forEach((eventName, count) ->
                System.out.println("Event: " + eventName + ", Available Tickets: " + count)
        );
    }

    public synchronized int countBookedTicketsByCustomerId(String customerId) {
        return (int) tickets.stream()
                .filter(ticket -> "Booked".equals(ticket.getTicketStatus()) && customerId.equals(ticket.getCustomerId()))
                .count();
    }
}

