import com.google.gson.*;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Configuration {
    private long totalAvailableTickets;
    private double ticketReleaseRate;
    private double customerRetrievalRate;
    private long maxTicketCapacity;
    private String configAdminUser;
    private String configAdminPassword;

    public Configuration() {
        this(5000, 7000, 200); //Calls the parameterized constructor.
    }

    public Configuration(double ticketReleaseRate, double customerRetrievalRate, long maxTicketCapacity) {
        this.totalAvailableTickets = loadTotalAvailableTickets();
        this.ticketReleaseRate = Math.max(ticketReleaseRate, 0);
        this.customerRetrievalRate = Math.max(customerRetrievalRate, 0);
        this.maxTicketCapacity = Math.max(maxTicketCapacity,totalAvailableTickets);
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
        String[] authenticateCredentials = promptUserForCredentials();
        if (authenticate(authenticateCredentials[0],authenticateCredentials[1])) {
            System.out.println("Authentication passed! Enter new Admin Credentials:");
            String[] newAdminCredentials = promptUserForCredentials();
            this.configAdminUser = newAdminCredentials[0];
            this.configAdminPassword = newAdminCredentials[1];
            saveConfiguration("Config.json");
            System.out.println("Admin credentials updated!\n");
        }else{
            System.out.println("Authentication failed. Admin Credentials update denied.\n");
        }
    }

    // Method to authenticate admin before allowing changes
    public boolean authenticate(String username, String password) {
        return this.configAdminUser.equals(username) && this.configAdminPassword.equals(password);
    }

    // Method to prompt the user for username and password
    public String[] promptUserForCredentials() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Previous username: ");
        String username = scanner.nextLine();
        System.out.print("Enter Previous password: ");
        String password = scanner.nextLine();

        return new String[]{username, password};
    }

    // Method to ask user for input and update configuration
    public void promptUserForConfigurationUpdate() {
        Scanner scanner = new Scanner(System.in);

        // Ask for admin credentials
        System.out.print("Enter admin username: ");
        String username = scanner.nextLine();

        System.out.print("Enter admin password: ");
        String password = scanner.nextLine();

        if (authenticate(username, password)) {

            // Ask for configuration settings with exception handling for invalid data types
            double ticketReleaseRate = getValidDoubleInput(scanner, "Enter new ticket release rate (in ms): ");
            double customerRetrievalRate = getValidDoubleInput(scanner, "Enter new customer retrieval rate (in ms): ");
            long maxTicketCapacity = getValidLongInput(scanner, "Enter new max ticket capacity: ");

            // Call the updateConfiguration method with the user's inputs
            updateConfiguration(ticketReleaseRate, customerRetrievalRate, maxTicketCapacity);
        } else {
            System.out.println("Authentication failed. Configuration update denied.\n");
        }
    }

    // Method to get a valid double input from the user
    private double getValidDoubleInput(Scanner scanner, String prompt) {
        double input = -1;
        while (true) {
            try {
                System.out.print(prompt);
                input = scanner.nextDouble();
                break;  // Exit the loop if the input is valid
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a valid number.");
                scanner.nextLine();  // Clear the invalid input from the scanner buffer
            }
        }
        return input;
    }

    // Method to get a valid long input from the user
    private long getValidLongInput(Scanner scanner, String prompt) {
        long input = -1;
        while (true) {
            try {
                System.out.print(prompt);
                input = scanner.nextLong();
                break;  // Exit the loop if the input is valid
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a valid number.");
                scanner.nextLine();  // Clear the invalid input from the scanner buffer
            }
        }
        return input;
    }



    // Method to update configuration settings
    public void updateConfiguration(double ticketReleaseRate, double customerRetrievalRate, long maxTicketCapacity) {

        if (ticketReleaseRate < 0 || customerRetrievalRate < 0 || maxTicketCapacity < totalAvailableTickets) {
                System.out.println("Invalid input: Rates must be non-negative, and max capacity cannot be lower than total available tickets.");
                return;
        }
        this.totalAvailableTickets = loadTotalAvailableTickets();
        this.ticketReleaseRate = ticketReleaseRate;
        this.customerRetrievalRate = customerRetrievalRate;
        this.maxTicketCapacity = maxTicketCapacity;

        System.out.println("\nConfiguration updated successfully!");
        // Automatically save the updated configuration
        saveConfiguration("Config.json");//
        System.out.println("Configuration changes have been auto-saved.\n");//
    }

    // Load totalAvailableTickets from Tickets.json file
//    private long loadTotalAvailableTickets() {
//        long availableCount = 0;
//        try (Reader reader = new FileReader("Tickets.json")) {
//            JsonArray tickets = JsonParser.parseReader(reader).getAsJsonArray();
//            for (int i = 0; i < tickets.size(); i++) {
//                JsonObject ticket = tickets.get(i).getAsJsonObject();
//                if ("Available".equals(ticket.get("ticketStatus").getAsString())) {
//                    availableCount++;
//                }
//            }
//        } catch (IOException e) {
//            System.out.println("Error loading tickets: " + e.getMessage());
//        }
//        return availableCount;
//    }
    private long loadTotalAvailableTickets() {
        long availableCount = 0;
        try (Reader reader = new FileReader("Tickets.json")) {
            JsonElement element = JsonParser.parseReader(reader);

            // Check if the element is a JSON array
            if (element.isJsonArray()) {
                JsonArray tickets = element.getAsJsonArray();
                for (JsonElement ticketElement : tickets) {
                    JsonObject ticket = ticketElement.getAsJsonObject();
                    if ("Available".equals(ticket.get("ticketStatus").getAsString())) {
                        availableCount++;
                    }
                }
            } else {
                System.out.println("Tickets.json does not contain a JSON array.");
            }
        } catch (IOException e) {
            System.out.println("Error loading tickets: " + e.getMessage());
        }
        return availableCount;
    }


    // Display configuration
    public void displayConfiguration() {
        updateTotalAvailableTickets();
        System.out.println("Total Available Tickets: " + totalAvailableTickets);
        System.out.println("Ticket Release Rate: " + ticketReleaseRate + " ms");
        System.out.println("Customer Retrieval Rate: " + customerRetrievalRate + " ms");
        System.out.println("Max Ticket Capacity: " + maxTicketCapacity);
    }

    // Update totalAvailableTickets and refresh its count
    public void updateTotalAvailableTickets() {
        this.totalAvailableTickets = loadTotalAvailableTickets();
    }

    // Save Configuration to a JSON file
    public void saveConfiguration(String fileName) {
        try (FileWriter writer = new FileWriter(fileName)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(this, writer);
            System.out.println("Configuration saved to " + fileName);
        } catch (IOException e) {
            System.out.println("Error saving configuration: " + e.getMessage());
        }
    }

    // Load Configuration from a JSON file
    public Configuration loadConfiguration(String fileName) {
        try (Reader reader = new FileReader(fileName)) {
            Gson gson = new Gson();
            Configuration config = gson.fromJson(reader, Configuration.class);
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
    }



//    public static void main(String[] args) {
//        Configuration config = new Configuration();
//        config.displayConfiguration();
//        //Indirect updating method of configuration setting.
//        config.promptUserForConfigurationUpdate();
//
//        // Setting new admin credentials
//        config.setAdminCredentials();
//
////        // Attempting to update settings with incorrect credentials
////        config.updateConfiguration("NewAdmin", "wrongpassword", 4000, 6000, 250);
////
////        // Updating settings with correct credentials
////        config.updateConfiguration("NewAdmin", "newpassword", 4050, 6000, 250);
////        config.updateConfiguration("NewAdmin", "newpassword", -1, 6000, 250);
////        config.updateConfiguration("NewAdmin", "newpassword", 4050, -1, 250);
////        config.updateConfiguration("NewAdmin", "newpassword", 4050, 6000, 20);
////        // Displaying configuration again to check updates
//        config.displayConfiguration();
//
//        // Save and load configuration example
//        config.saveConfiguration("Config.json");
//        Configuration config2= config.loadConfiguration("Config.json");
//        config2.displayConfiguration();
//    }
}
