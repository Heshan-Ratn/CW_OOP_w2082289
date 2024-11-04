import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TicketPool {
    private static int maxCapacity; // Shared max capacity across all instances
    private static boolean maxCapacityLoaded = false; // Flag to check if max capacity is already loaded
    private static final List<Ticket> tickets = Collections.synchronizedList(new ArrayList<>());
    private static boolean ticketsLoaded = false; // Flag to check if tickets are already loaded
    private static final String ticketFilePath ="Tickets.json";
    private static final String configFilePath ="config.json";

    public TicketPool() {
//        this.ticketFilePath = "Tickets.json";
//        this.configFilePath = "config.json";

        // Load max capacity only once
        if (!maxCapacityLoaded) {
            loadMaxCapacityFromConfig();
            maxCapacityLoaded = true;
        }

        // Load tickets only once
        if (!ticketsLoaded) {
            loadTicketsFromFile();
            ticketsLoaded = true;
        }
    }

    public static int getMaxCapacity() {
        return maxCapacity;
    }

    // Load max capacity from config.json
    private static void loadMaxCapacityFromConfig() {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(configFilePath)) {
            Configuration config = gson.fromJson(reader, Configuration.class);
            maxCapacity = config.getMaxTicketCapacity();
        } catch (IOException e) {
            System.out.println("Could not load max capacity from config file.");
            e.printStackTrace();
            maxCapacity = 100;  // Default to 100 if config load fails
        }
    }

    // Adds tickets to the pool and updates the JSON file
    public synchronized boolean addTickets(String vendorId,String eventDetails, double price, int quantity, Vendor vendor) {
//        try {
//            for (int i = 0; i < quantity; i++) {
//                // Check capacity before adding each ticket
//                while (tickets.size() >= maxCapacity) {
//                    System.out.println("Waiting to add tickets, pool at max capacity.");
//                    wait(); // Wait until enough space is available
//                }
//
//                // Add a single ticket
//                tickets.add(new Ticket("Ticket" + (tickets.size() + 1), eventDetails, price, vendorId));
//                saveTicketsToFile(); // Save after each ticket is added
//                notifyAll(); // Notify waiting threads after adding each ticket
//            }
//            return true;
//
//        } catch (InterruptedException e) {
//            System.out.println("Thread interrupted during addTickets.");
//            Thread.currentThread().interrupt();
//            return false;

        try {
            for (int i = 0; i < quantity; i++) {
                // Check if the vendor has stopped the session
                if (!vendor.isActive) {
                    System.out.println("Ticket addition stopped mid-batch for vendor: " + vendorId);
                    return false;  // Exit if stopSession is triggered
                }

                synchronized (this) {
                    // Check capacity before adding each ticket
                    while (tickets.size() >= maxCapacity) {
                        System.out.println("Waiting to add tickets, pool at max capacity.");
                        wait(); // Wait until enough space is available
                    }

                    // Add a single ticket
                    tickets.add(new Ticket("Ticket" + (tickets.size() + 1), eventDetails, price, vendorId));
                    saveTicketsToFile(); // Save after each ticket is added
                    notifyAll(); // Notify waiting threads after adding each ticket
                }
            }
            return true;

        } catch (InterruptedException e) {
            System.out.println("Thread interrupted during addTickets.");
            Thread.currentThread().interrupt();
            return false;
        }
    }



    // Removes a ticket from the pool and updates the JSON file
    public synchronized Ticket removeTicket() {
        try {
            while (tickets.isEmpty()) {
                System.out.println("Waiting to remove ticket, no tickets available.");
                wait();
            }
            Ticket removedTicket = tickets.remove(0);
            saveTicketsToFile();
            notifyAll();  // Notify producers that a ticket has been removed
            return removedTicket;
        } catch (InterruptedException e) {
            System.out.println("Thread interrupted during removeTicket.");
            Thread.currentThread().interrupt();
            return null;
        }
    }

    // Gets total tickets from the JSON file
    public int getTotalTicketsFromFile() {
        loadTicketsFromFile(); // Refresh the tickets list from the file
        return tickets.size();
    }


    // Loads tickets from JSON file into the shared list
    private synchronized void loadTicketsFromFile() {
        if (ticketsLoaded) return; // Prevent redundant loading
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(ticketFilePath)) {
            Type ticketListType = new TypeToken<ArrayList<Ticket>>() {}.getType();
            List<Ticket> loadedTickets = gson.fromJson(reader, ticketListType);
            tickets.clear();
            if (loadedTickets != null) {
                tickets.addAll(loadedTickets);
            }
        } catch (IOException e) {
            System.out.println("Could not load tickets from file.");
            e.printStackTrace();
        }
    }

    // Saves the current list of tickets to JSON file
    private synchronized void saveTicketsToFile() {
//        Gson gson = new Gson();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(ticketFilePath)) {
            gson.toJson(tickets, writer);
        } catch (IOException e) {
            System.out.println("Could not save tickets to file.");
            e.printStackTrace();
        }
    }
}
