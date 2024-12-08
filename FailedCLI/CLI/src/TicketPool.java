import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TicketPool {
    private static int maxCapacity;
    private static boolean maxCapacityLoaded = false;
    private static final List<Ticket> tickets = Collections.synchronizedList(new ArrayList<>());
    private static boolean ticketsLoaded = false;
    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private static final String ticketFilePath = "Tickets.json";
    private static final String configFilePath = "config.json";

    public TicketPool() {
        if (!maxCapacityLoaded) {
            loadMaxCapacityFromConfig();
            maxCapacityLoaded = true;
        }
        if (!ticketsLoaded) {
            loadTicketsFromFile();
            ticketsLoaded = true;
        }
    }

    public static int getMaxCapacity() {
        return maxCapacity;
    }

    private static void loadMaxCapacityFromConfig() {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(configFilePath)) {
            Configuration config = gson.fromJson(reader, Configuration.class);
            maxCapacity = config.getMaxTicketCapacity();
        } catch (IOException e) {
            System.out.println("Could not load max capacity from config file.");
            e.printStackTrace();
            maxCapacity = 100;
        }
    }

    public boolean addTickets(String vendorId, String eventDetails, double price, int quantity, Vendor vendor) {
        lock.writeLock().lock();  // Acquire write lock for adding
        try {
            List<Ticket> batch = new ArrayList<>();  // Temporary list to store the batch of tickets//
            for (int i = 0; i < quantity; i++) {
                if (!vendor.isActive) {
//                    System.out.println("Ticket addition stopped for vendor: " + vendorId);
                    //Thread.currentThread().interrupt();//
                    return false;
                }

                while (tickets.size() >= maxCapacity) {
                    System.out.println("Pool at max capacity, waiting to add tickets.");
                    lock.writeLock().newCondition().await();
                }

                // Simulate processing time before adding the ticket
                Thread.sleep(1000);

                Ticket ticket = new Ticket("Ticket" + (tickets.size() + 1), eventDetails, price, vendorId);//
                //tickets.add(new Ticket("Ticket" + (tickets.size() + 1), eventDetails, price, vendorId));
                batch.add(ticket);  // Add ticket to the batch//
                //saveTicketsToFile();  // Save after each ticket is added
                //lock.writeLock().newCondition().signalAll();  // Notify waiting threads
            }
            // Add the batch to the main ticket list after the loop completes//
            tickets.addAll(batch);//
            saveTicketsToFile();  // Save the batch to the file once all tickets are added//

            // Signal all waiting threads that tickets have been added and saved//
            lock.writeLock().newCondition().signalAll();//

            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Thread interrupted! "+vendor.getVendorId());//
            return false;
        } finally {
            lock.writeLock().unlock();  // Release write lock
        }
    }

//    public boolean addTickets(String vendorId, String eventDetails, double price, int quantity, Vendor vendor) {
//        synchronized (TicketPool.class) {  // Lock on the class object to synchronize access to tickets
//            try {
//                List<Ticket> batch = new ArrayList<>();  // Temporary list to store the batch of tickets
//                for (int i = 0; i < quantity; i++) {
//                    if (!vendor.isActive) {
//                        return false; // Stop adding if vendor is inactive
//                    }
//
//                    while (tickets.size() >= maxCapacity) {
//                        // Wait if the ticket pool has reached its max capacity
//                        lock.writeLock().newCondition().await();
//                    }
//
//                    // Simulate ticket processing time
//                    Thread.sleep(1000);
//
//                    Ticket ticket = new Ticket("Ticket" + (tickets.size() + 1), eventDetails, price, vendorId);
//                    batch.add(ticket); // Add ticket to batch
//                }
//                // Add the batch to the tickets list
//                tickets.addAll(batch);
//                saveTicketsToFile();  // Save the tickets once all are added
//                lock.writeLock().newCondition().signalAll();  // Notify waiting threads
//
//                return true;
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//                return false;
//            }
//        }
//    }


    public Ticket removeTicket() {
        lock.writeLock().lock();  // Acquire write lock for removing
        try {
            while (tickets.isEmpty()) {
                System.out.println("Waiting to remove ticket, no tickets available.");
                lock.writeLock().newCondition().await();
            }
            Ticket removedTicket = tickets.remove(0);
            saveTicketsToFile();
            lock.writeLock().newCondition().signalAll();  // Notify waiting threads
            return removedTicket;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } finally {
            lock.writeLock().unlock();  // Release write lock
        }
    }

    // Gets total tickets from the JSON file
    public int getTotalTicketsFromFile() {
        loadTicketsFromFile(); // Refresh the tickets list from the file
        return tickets.size();
    }

    public void viewAllTickets() {
        lock.readLock().lock();  // Acquire read lock for viewing
        try {
            Map<String, Integer> ticketCountMap = new HashMap<>();
            for (Ticket ticket : tickets) {
                ticketCountMap.put(ticket.getEventDetails(), ticketCountMap.getOrDefault(ticket.getEventDetails(), 0) + 1);
            }

            if (ticketCountMap.isEmpty()) {
                System.out.println("No tickets available in the pool.\n");
            } else {
                System.out.println("Tickets in the pool:");
                for (Map.Entry<String, Integer> entry : ticketCountMap.entrySet()) {
                    System.out.println("Ticket Name: " + entry.getKey() + ", Count: " + entry.getValue());
                }
                System.out.println();
            }
        } finally {
            lock.readLock().unlock();  // Release read lock
        }
    }

    private void loadTicketsFromFile() {
        lock.writeLock().lock();  // Load should be treated as a write operation
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
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void saveTicketsToFile() {
        lock.writeLock().lock();  // Save should be treated as a write operation
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(ticketFilePath)) {
            gson.toJson(tickets, writer);
        } catch (IOException e) {
            System.out.println("Could not save tickets to file.");
            e.printStackTrace();
        } finally {
            lock.writeLock().unlock();
        }
    }
}

