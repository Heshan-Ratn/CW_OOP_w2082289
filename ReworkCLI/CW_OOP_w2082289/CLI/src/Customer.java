//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//
//import java.io.FileReader;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.lang.reflect.Type;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.atomic.AtomicBoolean;
//
//public class Customer implements Runnable {
//    private String customerId;
//    private String password;
//    private TicketPool ticketPool; // Shared ticket pool
//    private AtomicBoolean purchasingTickets = new AtomicBoolean(true); // Consumer-level stop flag
//    private static AtomicBoolean adminStopAllPurchases = new AtomicBoolean(false); // Admin-level stop flag
//    private int customerRetrievalRate; // in milliseconds
//
//    // Instance variables for event name and ticket quantity to be booked
//    private String eventName;
//    private int ticketsToBook;
//
//    // Constructor for sign-up and sign-in purposes
//    public Customer(String customerId, String password) {
//        this.customerId = customerId;
//        this.password = password;
//    }
//
//    // Constructor for initializing consumer with TicketPool and retrieval rate
//    public Customer(String customerId, String password, TicketPool ticketPool) {
//        this.customerId = customerId;
//        this.password = password;
//        this.ticketPool = ticketPool;
//        this.customerRetrievalRate = loadRetrievalRateFromConfig();
//    }
//
//    // Static method for admin-level stop
//    public static void stopAllPurchases() {
//        adminStopAllPurchases.set(true);
//    }
//
//    public static void resumeAllPurchases() {
//        adminStopAllPurchases.set(false);
//    }
//
//    // Load retrieval rate from config
//    private int loadRetrievalRateFromConfig() {
//        try (FileReader reader = new FileReader("config.json")) {
//            Gson gson = new Gson();
//            Configuration config = gson.fromJson(reader, Configuration.class);
//            return (int) config.getCustomerRetrievalRate();  // Get the release rate from config
//        } catch (IOException e) {
//            System.out.println("Error reading config.json, using default rate.");
//            return 1000;  // Default value if config.json is not found or read error occurs
//        }
//    }
//
//    // Method for signing up a new consumer
//    public static synchronized boolean signUp(String customerId, String password) {
//        List<Customer> consumers = loadCustomers();
//        if (consumers == null) {
//            consumers = new ArrayList<>();
//        }
//
//        // Check if customerId is already taken
//        for (Customer c : consumers) {
//            if (c.customerId.equals(customerId)) {
//                System.out.println("Consumer ID already taken.");
//                return false;
//            }
//        }
//
//        consumers.add(new Customer(customerId, password));
//
//        // Save updated consumer list to file
//        return saveCustomers(consumers);
//    }
//
//    // Method for signing in an existing consumer
//    public static synchronized Customer signIn(String customerId, String password) {
//        List<Customer> consumers = loadCustomers();
//        if (consumers == null) return null;
//
//        for (Customer c : consumers) {
//            if (c.customerId.equals(customerId) && c.password.equals(password)) {
//                System.out.println("Consumer signed in successfully.");
//                return new Customer(customerId, password);
//            }
//        }
//        System.out.println("Sign in failed: Incorrect ID or password.");
//        return null;
//    }
//
//    // Method to store event name and ticket quantity requested by the consumer
//    public void requestTickets(String eventName, int ticketsToBook) {
//        this.eventName = eventName;
//        this.ticketsToBook = ticketsToBook;
//    }
//
//    // Runnable method to purchase tickets based on requested event and quantity
//    @Override
//    public void run() {
//        for (int i = 0; i < ticketsToBook; i++) {
//            if (!purchasingTickets.get() || adminStopAllPurchases.get()) {
//                System.out.println("Ticket purchasing stopped for consumer: " + customerId);
//                return;
//            }
//
//            boolean purchased = ticketPool.removeTicket(eventName, customerId);
//            if (purchased) {
//                System.out.println("Ticket purchased by " + customerId);
//            } else {
//                System.out.println("No tickets available for purchase.");
//                break;
//            }
//
//            try {
//                TimeUnit.MILLISECONDS.sleep(customerRetrievalRate);
//
//                // Check the stop condition again after the sleep
//                if (!purchasingTickets.get() || adminStopAllPurchases.get()) {
//                    System.out.println("Ticket purchasing stopped after sleep for consumer: " + customerId);
//                    return;
//                }
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//                System.out.println("Ticket purchasing interrupted for consumer: " + customerId);
//                return;
//            }
//        }
//        System.out.println("Ticket purchasing completed for consumer: " + customerId);
//    }
//
//    // Load consumers from file
//    private static synchronized List<Customer> loadCustomers() {
//        try (FileReader reader = new FileReader("customer.json")) {
//            Gson gson = new Gson();
//            Type consumerListType = new TypeToken<List<Customer>>() {}.getType();
//            return gson.fromJson(reader, consumerListType);
//        } catch (IOException e) {
//            System.out.println("Error loading consumers.");
//            return null;
//        }
//    }
//
//    // Save consumers to file
//    private static synchronized boolean saveCustomers(List<Customer> consumers) {
//        try (FileWriter writer = new FileWriter("customer.json")) {
//            Gson gson = new Gson();
//            gson.toJson(consumers, writer);
//            System.out.println("Consumer saved successfully.");
//            return true;
//        } catch (IOException e) {
//            System.out.println("Error saving consumer.");
//            return false;
//        }
//    }
//
//    // Stop and resume ticket purchasing for this consumer
//    public void stopPurchasingTickets() {
//        purchasingTickets.set(false);
//    }
//
//    public void resumePurchasingTickets() {
//        purchasingTickets.set(true);
//    }
//}





import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class Customer implements Runnable {
    private String customerId;
    private String password;
    private TicketPool ticketPool; // Shared ticket pool
    private AtomicBoolean purchasingTickets = new AtomicBoolean(true); // Consumer-level stop flag
    private static AtomicBoolean adminStopAllPurchases = new AtomicBoolean(false); // Admin-level stop flag
    private int customerRetrievalRate; // in milliseconds
    private static final String CUSTOMER_FILE = "customer.json";


    // Instance variables for event name and ticket quantity to be booked
    private String eventName;
    private int ticketsToBook;

    // Constructor for sign-up and sign-in purposes
    public Customer(String customerId, String password) {
        this.customerId = customerId;
        this.password = password;
    }

    public String getCustomerId() {
        return customerId;
    }

    // Constructor for initializing consumer with TicketPool and retrieval rate
    public Customer(String customerId, String password, TicketPool ticketPool) {
        this.customerId = customerId;
        this.password = password;
        this.ticketPool = ticketPool;
        this.customerRetrievalRate = loadRetrievalRateFromConfig();
    }

    // Static method for admin-level stop
    public static void stopAllPurchases() {
        adminStopAllPurchases.set(true);
    }

    public static void resumeAllPurchases() {
        adminStopAllPurchases.set(false);
    }

    // Load retrieval rate from config
    private int loadRetrievalRateFromConfig() {
        try (FileReader reader = new FileReader("config.json")) {
            Gson gson = new Gson();
            Configuration config = gson.fromJson(reader, Configuration.class);
            return (int) config.getCustomerRetrievalRate();  // Get the retrieval rate from config
        } catch (IOException e) {
            System.out.println("Error reading config.json, using default rate.");
            return 1000;  // Default value if config.json is not found or read error occurs
        }
    }

    // Method for signing up a new consumer
    public static synchronized boolean signUp(String customerId, String password) {
        List<CustomerData> customers = loadCustomers();

        // Check if customerId is already taken
        for (CustomerData customer : customers) {
            if (customer.getCustomerId().equals(customerId)) {
                System.out.println("Consumer ID already taken.");
                return false;
            }
        }

        customers.add(new CustomerData(customerId, password));

        // Save updated consumer list to file
        return saveCustomers(customers);
    }

    // Method for signing in an existing consumer
    public static synchronized Customer signIn(String customerId, String password) {
        List<CustomerData> customers = loadCustomers();
        if (customers == null) return null;

        for (CustomerData customer : customers) {
            if (customer.getCustomerId().equals(customerId) && customer.password.equals(password)) {
                System.out.println("Consumer signed in successfully.");
                return new Customer(customerId, password);
            }
        }
        System.out.println("Sign in failed: Incorrect ID or password.");
        return null;
    }

    // Method to store event name and ticket quantity requested by the consumer
    public void requestTickets(String eventName, int ticketsToBook) {
        this.eventName = eventName;
        this.ticketsToBook = ticketsToBook;
    }

    // Runnable method to purchase tickets based on requested event and quantity
    @Override
    public void run() {
        for (int i = 0; i < ticketsToBook; i++) {
            if (!purchasingTickets.get() || adminStopAllPurchases.get()) {
                System.out.println("Ticket purchasing stopped for consumer: " + customerId);
                return;
            }

            boolean purchased = ticketPool.removeTicket(eventName, customerId);
            if (purchased) {
                System.out.println("Ticket purchased by " + customerId);
            } else {
                System.out.println("No tickets available for purchase.");
                break;
            }

            try {
                TimeUnit.MILLISECONDS.sleep(customerRetrievalRate);

                // Check the stop condition again after the sleep
                if (!purchasingTickets.get() || adminStopAllPurchases.get()) {
                    System.out.println("Ticket purchasing stopped after sleep for consumer: " + customerId);
                    return;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Ticket purchasing interrupted for consumer: " + customerId);
                return;
            }
        }
        System.out.println("Ticket purchasing completed for consumer: " + customerId);
    }

    // Load consumers from file
    private static synchronized List<CustomerData> loadCustomers() {
        try (FileReader reader = new FileReader(CUSTOMER_FILE)) {
            Gson gson = new Gson();
            Type customerListType = new TypeToken<List<CustomerData>>() {}.getType();
            List<CustomerData> customers = gson.fromJson(reader, customerListType);
            return customers != null ? customers : new ArrayList<>(); // Ensure non-null list
        } catch (IOException e) {
            System.out.println("Error loading consumers: " + e.getMessage());
            return new ArrayList<>(); // Return empty list if error occurs
        }
    }

    // Save consumers to file
    private static synchronized boolean saveCustomers(List<CustomerData> customers) {
        try (FileWriter writer = new FileWriter(CUSTOMER_FILE)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(customers, writer);
            System.out.println("Consumer saved successfully.");
            return true;
        } catch (IOException e) {
            System.out.println("Error saving consumer: " + e.getMessage());
            return false;
        }
    }

    // Inner class for vendor data storage
    private static class CustomerData {
        private final String customerId;
        private final String password;

        public CustomerData(String customerId, String password) {
            this.customerId = customerId;
            this.password = password;
        }

        public String getCustomerId() {
            return customerId;
        }

        public String getPassword() {
            return password;
        }
    }

    // Stop and resume ticket purchasing for this consumer
    public void stopPurchasingTickets() {
        purchasingTickets.set(false);
    }

    public void resumePurchasingTickets() {
        purchasingTickets.set(true);
    }
}

