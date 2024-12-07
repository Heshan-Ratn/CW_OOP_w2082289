import com.google.gson.*;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;

public class Configuration {
    private static final ReentrantLock lock = new ReentrantLock();
    private long totalAvailableTickets;
    private double ticketReleaseRate;
    private double customerRetrievalRate;
    private long maxTicketCapacity;
    private String configAdminUser;
    private String configAdminPassword;

    public Configuration() {
        this(5000, 7000, 200); // Calls the parameterized constructor.
    }

    public Configuration(double ticketReleaseRate, double customerRetrievalRate, long maxTicketCapacity) {
        this.totalAvailableTickets = loadTotalAvailableTickets();
        this.ticketReleaseRate = Math.max(ticketReleaseRate, 0);
        this.customerRetrievalRate = Math.max(customerRetrievalRate, 0);
        this.maxTicketCapacity = Math.max(maxTicketCapacity, totalAvailableTickets);
        this.configAdminUser = "AdminSuper";
        this.configAdminPassword = "heshanplus1";
    }

    public double getTicketReleaseRate() {
        return ticketReleaseRate;
    }

    public double getCustomerRetrievalRate() {
        return customerRetrievalRate;
    }

    public long getMaxTicketCapacity() {
        return maxTicketCapacity;
    }

    // Method to set admin credentials
    public void setAdminCredentials() {
        lock.lock(); // Acquire the lock
        try {
            String[] currentCredentials = promptUserForCredentials("Enter current admin username: ", "Enter current admin password: ");
            if (authenticate(currentCredentials[0], currentCredentials[1])) {
                System.out.println("Authentication successful! Enter new admin credentials:");
                String[] newCredentials = promptUserForCredentials("Enter new admin username: ", "Enter new admin password: ");
                this.configAdminUser = newCredentials[0];
                this.configAdminPassword = newCredentials[1];
                saveConfiguration("Config.json");
                System.out.println("Admin credentials updated successfully.\n");
            } else {
                System.out.println("Incorrect credentials. Unable to update admin details.\n");
            }
        } finally {
            lock.unlock(); // Ensure the lock is released
        }
    }

    // Method to authenticate admin before allowing changes
    public boolean authenticate(String username, String password) {
        lock.lock(); // Acquire the lock
        try {
            return this.configAdminUser.equals(username) && this.configAdminPassword.equals(password);
        } finally {
            lock.unlock(); // Release the lock
        }
    }

    // Method to prompt the user for username and password
    public String[] promptUserForCredentials(String usernamePrompt, String passwordPrompt) {
        Scanner scanner = new Scanner(System.in);
        System.out.
                print(usernamePrompt);
        String username = scanner.nextLine();
        System.out.print(passwordPrompt);
        String password = scanner.nextLine();
        return new String[]{username, password};
    }

    // Method to ask user for input and update configuration
    public void promptUserForConfigurationUpdate() {
        lock.lock(); // Acquire the lock
        try {
            Scanner scanner = new Scanner(System.in);

            // Ask for admin credentials
            String[] credentials = promptUserForCredentials("Enter admin username: ", "Enter admin password: ");

            if (authenticate(credentials[0], credentials[1])) {
                System.out.println("Authentication successful! Enter new configuration values.");

                // Ask for configuration settings with exception handling for invalid data types
                double ticketReleaseRate = getValidDoubleInput(scanner, "Enter new ticket release rate (in ms): ");
                double customerRetrievalRate = getValidDoubleInput(scanner, "Enter new customer retrieval rate (in ms): ");
                long maxTicketCapacity = getValidLongInput(scanner, "Enter new max ticket capacity: ");

                // Update configuration with the user's inputs
                updateConfiguration(ticketReleaseRate, customerRetrievalRate, maxTicketCapacity);
            } else {
                System.out.println("Incorrect credentials. Configuration update denied.\n");
            }
        } finally {
            lock.unlock(); // Ensure the lock is released
        }
    }

    // Method to get a valid double input from the user
    private double getValidDoubleInput(Scanner scanner, String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return scanner.nextDouble();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a valid number.");
                scanner.nextLine(); // Clear the invalid input from the scanner buffer
            }
        }
    }

    // Method to get a valid long input from the user
    private long getValidLongInput(Scanner scanner, String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return scanner.nextLong();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a valid number.");
                scanner.nextLine(); // Clear the invalid input from the scanner buffer
            }
        }
    }

    // Method to update configuration settings
    public void updateConfiguration(double ticketReleaseRate, double customerRetrievalRate, long maxTicketCapacity) {
        lock.lock(); // Acquire lock
        try {
            if (ticketReleaseRate < 0 || customerRetrievalRate < 0 || maxTicketCapacity < totalAvailableTickets) {
                System.out.println("Invalid input: Rates must be non-negative, and max capacity cannot be lower than total available tickets.");
                return;
            }
            this.totalAvailableTickets = loadTotalAvailableTickets();
            this.ticketReleaseRate = ticketReleaseRate;
            this.customerRetrievalRate = customerRetrievalRate;
            this.maxTicketCapacity = maxTicketCapacity;

            System.out.println("\nConfiguration updated successfully!");
            saveConfiguration("Config.json");
            System.out.println("Configuration changes have been auto-saved.\n");
        } finally {
            lock.unlock(); // Release lock
        }
    }

    public long loadTotalAvailableTickets() {
        lock.lock(); // Acquire the lock
        try {
            long availableCount = 0;
            try (Reader reader = new FileReader("Tickets.json")) {
                JsonElement element = JsonParser.parseReader(reader);
                if (element.isJsonArray()) {
                    JsonArray tickets = element.getAsJsonArray();
                    for (JsonElement ticketElement : tickets) {
                        JsonObject ticket = ticketElement.getAsJsonObject();
                        if ("Available".equals(ticket.get("ticketStatus").getAsString())) {
                            availableCount++;
                        }
                    }
                } else {
                    System.out.println("Tickets.json does not contain a valid JSON array.");
                }
            } catch (IOException e) {
                System.out.println("Error loading tickets: " + e.getMessage());
            }
            return availableCount;
        } finally {
            lock.unlock(); // Release the lock
        }
    }

    public void displayConfiguration() {
        lock.lock(); // Acquire lock
        try {
            updateTotalAvailableTickets();
            System.out.printf("Total Available Tickets: %d%n", totalAvailableTickets);
            System.out.printf("Ticket Release Rate: %.2f ms%n", ticketReleaseRate);
            System.out.printf("Customer Retrieval Rate: %.2f ms%n", customerRetrievalRate);
            System.out.printf("Max Ticket Capacity: %d%n", maxTicketCapacity);
        } finally {
            lock.unlock(); // Release lock
        }
    }


    public void updateTotalAvailableTickets() {
        lock.lock(); // Acquire the lock
        try {
            this.totalAvailableTickets = loadTotalAvailableTickets();
        } finally {
            lock.unlock(); // Release the lock
        }
    }

    public void saveConfiguration(String fileName) {
        lock.lock(); // Acquire lock
        try {
            try (FileWriter writer = new FileWriter(fileName)) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                gson.toJson(this, writer);
                System.out.println("Configuration saved to " + fileName);
            } catch (IOException e) {
                System.out.println("Error saving configuration: " + e.getMessage());
            }
        } finally {
            lock.unlock(); // Release lock
        }
    }


    public Configuration loadConfiguration(String fileName) {
        lock.lock(); // Acquire lock
        try {
            try (Reader reader = new FileReader(fileName)) {
                Gson gson = new Gson();
                Configuration config = gson.fromJson(reader, Configuration.class);
                this.totalAvailableTickets = loadTotalAvailableTickets();
                this.ticketReleaseRate = Math.max(config.ticketReleaseRate, 0);
                this.customerRetrievalRate = Math.max(config.customerRetrievalRate, 0);
                this.maxTicketCapacity = Math.max(config.maxTicketCapacity, config.totalAvailableTickets);
                this.configAdminUser = config.configAdminUser;
                this.configAdminPassword = config.configAdminPassword;
                System.out.println("Configuration loaded from " + fileName);
                return config;
            } catch (IOException e) {
                System.out.println("Error loading configuration: " + e.getMessage());
                return new Configuration();
            }
        } finally {
            lock.unlock(); // Release lock
        }
    }
}
