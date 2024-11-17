import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

public class Customer implements Runnable {
    private String customerId;
    private String password;
    private TicketPool ticketPool; // Shared ticket pool
    private AtomicBoolean purchasingTickets = new AtomicBoolean(true); // Consumer-level stop flag
    private static AtomicBoolean adminStopAllPurchases = new AtomicBoolean(false); // Admin-level stop flag
    private int customerRetrievalRate; // in milliseconds
    private static final String CUSTOMER_FILE = "customer.json";
    private static final int MAX_ATTEMPTS = 3;

    private static final Logger logger = LogManager.getLogger(Customer.class);
    private static final Logger loggerRun = LogManager.getLogger("CustomerRun");


    private PurchaseRequest purchaseRequest;

    // Constructor for sign-up and sign-in purposes
    public Customer(String customerId, String password) {
        this.customerId = customerId;
        this.password = password;
    }

    public String getCustomerId() {
        return customerId;
    }


    public String getPassword() {
        return password;
    }

    public void setTicketPool(TicketPool ticketPool) {
        this.ticketPool = ticketPool;
    }

    public void setCustomerRetrievalRate() {
        this.customerRetrievalRate = loadRetrievalRateFromConfig();
    }

    public void setPurchaseRequest(PurchaseRequest purchaseRequest) {
        this.purchaseRequest = purchaseRequest;
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

    public static String[] promptCustomerCredentials() {
        Scanner scanner = new Scanner(System.in);
        String customerId, password;

        for (int attempts = 0; attempts < MAX_ATTEMPTS; attempts++) {
            System.out.print("Enter Customer ID (7 characters, last 3 must be digits): ");
            customerId = scanner.nextLine().trim();

            if (!isValidCustomerId(customerId)) {
                System.out.println("Invalid Customer ID. Please ensure it is 7 characters with the last 3 as digits.");
                continue;
            }

            System.out.print("Enter Password (8-12 characters): ");
            password = scanner.nextLine().trim();

            if (!isValidPassword(password)) {
                System.out.println("Invalid Password. It must be between 8-12 characters.");
                continue;
            }

            return new String[]{customerId, password}; // Successful input
        }

        System.out.println("Maximum attempts reached. Returning to main menu.");
        return new String[]{"", ""}; // Failed input after max attempts
    }

    private static boolean isValidCustomerId(String customerId) {
        return customerId.length() == 7 && Pattern.matches("^[A-Za-z0-9_]{4}[0-9]{3}$", customerId);
    }
    private static boolean isValidPassword(String password) {
        return password.length() >= 8 && password.length() <= 12;
    }


    // Method for signing up a new consumer
    public static synchronized boolean signUp() {
        String[] newCustomerCredentials = promptCustomerCredentials();
        if (newCustomerCredentials[0].equals("")&&newCustomerCredentials[1].equals("")) {
            return false;
        }

        List<CustomerData> customers = loadCustomers();

        // Check if customerId is already taken
        for (CustomerData customer : customers) {
            if (customer.getCustomerId().equals(newCustomerCredentials[0])) {
                System.out.println("Consumer ID already taken.");
                return false;
            }
        }

        customers.add(new CustomerData(newCustomerCredentials[0], newCustomerCredentials[1]));

        // Save updated consumer list to file
        return saveCustomers(customers);
    }

    // Method for signing in an existing consumer
    public static synchronized Customer signIn() {
        String[] CustomerCredentials = promptCustomerCredentials();
        if (CustomerCredentials[0].equals("") && CustomerCredentials[1].equals("")) {
            System.out.println("Returning Back to main menu all the sign in attempts Failed!\n");
            return null;
        }

        List<CustomerData> customers = loadCustomers();
        if (customers.isEmpty()) {
            System.out.println("Customers database is empty!.\n");
            return null;
        }

        for (CustomerData customer : customers) {
            if (customer.getCustomerId().equals(CustomerCredentials[0]) && customer.getPassword().equals(CustomerCredentials[1])) {
                System.out.println("Consumer signed in successfully.\n");
                return new Customer(CustomerCredentials[0], CustomerCredentials[1]);
            }
        }
        System.out.println("Sign in failed: Incorrect ID or password.\n");
        return null;
    }


    // Runnable method to purchase tickets based on requested event and quantity
    @Override
    public void run() {

        // Extract details from the PurchaseRequest object
        String eventName = purchaseRequest.getEventName();
        int ticketsToBook = purchaseRequest.getTicketsToBook();

        for (int i = 0; i < ticketsToBook; i++) {
            if (!purchasingTickets.get() || adminStopAllPurchases.get()) {
                loggerRun.info("Ticket purchasing stopped for consumer: " + customerId + " (Event: " + eventName + ")");
                return;
            }

            boolean purchased = ticketPool.removeTicket(eventName, customerId);
            if (purchased) {
                loggerRun.info("Ticket purchased by " + customerId + " (Event: " + eventName + ")");
            } else {
                loggerRun.info("No tickets available for purchase"+ " (Event: " + eventName + ").");
                break;
            }

            try {
                //Brief break between purchases of each ticket.
                TimeUnit.MILLISECONDS.sleep(customerRetrievalRate);

                // Check the stop condition again after the sleep
                if (!purchasingTickets.get() || adminStopAllPurchases.get()) {
                    loggerRun.info("Ticket purchasing stopped after sleep for consumer: " + customerId + " (Event: " + eventName + ")");
                    return;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                loggerRun.error("Ticket purchasing interrupted for consumer: " + customerId + " (Event: " + eventName + ")");
                return;
            }
        }
        loggerRun.info("Ticket purchasing completed for consumer: " + customerId + " (Event: " + eventName + ")");
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



    public synchronized PurchaseRequest requestTickets(Set<String> eventDetails) {
        Scanner scanner = new Scanner(System.in);

        String eventName = promptForEventName(scanner, eventDetails);
        if (eventName == null) {
            System.out.println("Too many invalid attempts for event name. Request cancelled.");
            return null;
        }

        int ticketsToBook = promptForTicketQuantity(scanner);
        if (ticketsToBook == -1) {
            System.out.println("Too many invalid attempts for ticket quantity. Request cancelled.");
            return null;
        }

        System.out.println("Request successfully recorded: Event = " + eventName +
                ", Tickets to Book = " + ticketsToBook + "\n");

        return new PurchaseRequest(eventName, ticketsToBook);
    }

    // Helper method to prompt for a valid event name
    private String promptForEventName(Scanner scanner, Set<String> eventDetails) {
        for (int attempts = 3; attempts > 0; attempts--) {
            System.out.print("Enter the event name: ");
            String inputEventName = scanner.nextLine().trim();

            if (eventDetails.contains(inputEventName)) {
                return inputEventName;
            } else {
                System.out.println("Invalid event name. Please choose from the following events:");
                eventDetails.forEach(System.out::println);
            }
        }
        return null;  // Return null if all attempts are exhausted
    }

    // Helper method to prompt for a valid ticket quantity
    private int promptForTicketQuantity(Scanner scanner) {
        for (int attempts = 3; attempts > 0; attempts--) {
            System.out.print("Enter the number of tickets to book: ");
            try {
                int inputTicketsToBook = Integer.parseInt(scanner.nextLine().trim());

                if (inputTicketsToBook > 0) {
                    return inputTicketsToBook;
                } else {
                    System.out.println("Invalid quantity. Number of tickets must be greater than zero.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
        return -1;  // Return -1 if all attempts are exhausted
    }


    //This method will be used to check status of purchasing ability of tickets.
    public boolean checkPurchaseStatus() {
        if (adminStopAllPurchases.get()) {
            System.out.println("Ticket purchase paused by admin for all customer.\n");
            return false;
        } else if (!purchasingTickets.get()) {
            System.out.println("Ticket purchase paused by the customer: " + customerId+"\n");
            return false;
        } else {
            System.out.println("Ticket purchase is active for customer: " + customerId+"\n");
            return true;
        }
    }
}

