//import java.util.Scanner;
//
//public class Main2 {
//    public static void main(String[] args) {
//        checkConfiguration();
//        editConfiguration();
//        checkConfiguration();
//        TicketPool ticketPool = new TicketPool();
//        System.out.println(ticketPool.getMaxCapacity());
//        System.out.println(ticketPool.getTotalTicketsFromFile());
//    }
//
//    public static void checkConfiguration() {
//        // Load the configuration from a JSON file
//        Configuration loadedConfig = Configuration.loadConfigFromFile("config.json");
//        if (loadedConfig != null && loadedConfig.validateConfig()) {
//            System.out.println("Configuration loaded successfully!");
//        } else {
//            System.out.println("Failed to load configuration.");
//        }
//    }
//
//
//
//
//
//    public static void editConfiguration() {
//        if (promptYesOrNo("Do you want to edit the configuration (y/n)? : ")) {
//            Configuration config = new Configuration();
//
////            config.setTotalTickets(promptForInt("Enter total tickets (must be > 0): ", value -> value > 0));
//            config.setTicketReleaseRate(promptForInt("Enter ticket release rate (must be > 0): ", value -> value > 0));
//            config.setCustomerRetrievalRate(promptForInt("Enter customer retrieval rate (must be > 0): ", value -> value > 0));
//            config.setMaxTicketCapacity(promptForInt("Enter max ticket capacity (must be >= total tickets): ", value -> value >= config.getTotalTickets()));
//
//            if (promptYesOrNo("Do you want to save the configuration (y/n)? : ")) {
//                config.saveConfigToFile("config.json");
//            } else {
//                System.out.println("Configuration changes were not saved.");
//            }
//        } else {
//            System.out.println("Exiting edit configuration.");
//        }
//    }
//    private static int promptForInt(String message, java.util.function.IntPredicate condition) {
//        Scanner input = new Scanner(System.in);
//        System.out.print(message);
//        while (true) {
//            if (input.hasNextInt()) {
//                int value = input.nextInt();
//                if (condition.test(value)) {
//                    return value;
//                } else {
//                    System.out.print("Invalid input. " + message);
//                }
//            } else {
//                System.out.print("Invalid input. Enter a valid integer. " + message);
//                input.next(); // Consume the invalid input
//            }
//        }
//    }
//
//    private static boolean promptYesOrNo(String message) {
//        Scanner input = new Scanner(System.in);
//        System.out.print(message);
//        String answer = input.nextLine().trim().toLowerCase();
//        while (!answer.equals("y") && !answer.equals("n")) {
//            System.out.print("Please enter a valid option (y/n): ");
//            answer = input.nextLine().trim().toLowerCase();
//        }
//        return answer.equals("y");
//    }
//}


import java.util.Scanner;

public class Main2 {
    private static final Scanner input = new Scanner(System.in);
    private static Configuration config = ConfigControl.checkConfiguration();

    public static void main(String[] args) {
        System.out.println("Welcome to Real-time Ticketing System by HKR\n");
//        loadOrInitializeSettings();
        while (true) {
            printMainMenu();
            int option = getUserChoice(1, 6);  // Adjusted to include option 6 for "Exit System"
            if (option == 6) break;
            handleMainOption(option);
        }
        System.out.println("Exiting System...");
    }

    private static void printMainMenu() {
        System.out.println("Pick an option below:");
        System.out.println("1. Configure Settings");
        System.out.println("2. Login to System");
        System.out.println("3. View Available Tickets");
        System.out.println("4. View Real-time Tickets Being Sold and Added");
        System.out.println("5. Start/Stop All Threads (Vendors and Customers)");
        System.out.println("6. Exit System");
        System.out.println(); // Add extra space for readability
    }

    private static int getUserChoice(int min, int max) {
        while (true) {
            try {
                System.out.print("Enter your choice: ");
                int choice = Integer.parseInt(input.nextLine().trim());
                System.out.println(); // Add space after user input
                if (choice >= min && choice <= max) return choice;
                System.out.printf("Please enter a number between %d and %d.%n%n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid integer.\n");
            }
        }
    }

    private static void handleMainOption(int option) {
        switch (option) {
            case 1 -> handleConfigurationSettings();
            case 2 -> handleLogin();
            case 3 -> {
                System.out.println("Viewing Available Tickets...\n");
                TicketPool ticketPool = new TicketPool();
                ticketPool.viewAllTickets();
            }
            case 4 -> System.out.println("Viewing Real-time Tickets Being Sold and Added...\n");
            case 5 -> handleThreadManagement();
            default -> System.out.println("Invalid option. Please try again.\n");
        }
    }

    private static void handleConfigurationSettings() {
        System.out.println("Configuration Settings:");
        System.out.println("1. Edit Configuration Setting");
        System.out.println("2. View Configuration Setting");
        System.out.println("3. Exit");
        System.out.println(); // Extra space for readability
        int choice = getUserChoice(1, 3);
        switch (choice) {
            case 1 -> {
                System.out.println("Editing Configuration Setting...\n");
                ConfigControl.editConfiguration();
                config = ConfigControl.checkConfiguration();
            }
            case 2 -> {
                System.out.println("Viewing Configuration Setting...\n");
                config = ConfigControl.checkConfiguration();
                ConfigControl.viewConfiguration(config);
            }
            case 3 -> System.out.println("Exiting Configuration Settings...\n");
        }
    }

    private static void handleLogin() {
        System.out.println("Login:");
        System.out.println("1. Login as a Vendor");
        System.out.println("2. Login as a Consumer");
        System.out.println("3. Exit");
        System.out.println(); // Extra space for readability
        int choice = getUserChoice(1, 3);
        if (choice == 1) handleVendorLogin();
        else if (choice == 2) handleConsumerLogin();
        else System.out.println("Exiting Login...\n");
    }

    private static void handleVendorLogin() {
        System.out.println("Vendor Login:");
        System.out.println("1. Sign up Vendor");
        System.out.println("2. Sign in Vendor");
        System.out.println("3. Exit");
        System.out.println(); // Extra space for readability
        int choice = getUserChoice(1, 3);
        switch (choice) {
            case 1 -> {
                VendorSignUp vendorSignUp = new VendorSignUp();
                vendorSignUp.signUp();
                System.out.println("Saving Vendor...\n");
            }
            case 2 -> {
                VendorSignIn newSignIn = new VendorSignIn();
                Vendor vendorSignedIn = newSignIn.signIn(config);
                boolean vendorFound = newSignIn.isVendorFound(vendorSignedIn);
                handleVendorActions(vendorFound, vendorSignedIn);
            }
            case 3 -> System.out.println("Exiting Vendor Login...\n");
        }
    }

    private static void handleVendorActions(boolean vendorFound, Vendor vendorSignedIn) {
        if (vendorFound) {
            System.out.println("Vendor with ID: " + vendorSignedIn.getVendorId() + " found!\n");
            boolean exit = false;
            while (!exit) {
                System.out.println("Vendor Actions:");
                System.out.println("1. Add Tickets to the Ticket pool");
                System.out.println("2. View Added Tickets");
                System.out.println("3. View Other Tickets");
                System.out.println("4. Start/Stop Ticket Release");
                System.out.println("5. View Real-time Tickets Being Sold and Added");
                System.out.println("6. Exit");
                System.out.println(); // Extra space for readability
                exit = handleVendorChoice(getUserChoice(1, 6), vendorSignedIn);
            }
        } else {
            System.out.println("Credentials not found, unable to sign in.\n");
        }
    }

    private static boolean handleVendorChoice(int choice, Vendor vendor) {
        switch (choice) {
            case 1 -> {
                System.out.println("Ticket addition process initiated...\n");
                if (vendor.CheckReleaseStatus()) {
                    vendor.setTicketReleaseParameters();
                    Thread vendorThread = new Thread(vendor);
                    vendorThread.start();
                }
                return false;
            }
            case 2 -> System.out.println("Viewing Added Tickets...\n");
            case 3 -> {
                System.out.println("Viewing Other Tickets...\n");
                TicketPool ticketPool = new TicketPool();
                ticketPool.viewAllTickets();
            }
            case 4 -> {
                vendor.ControlSession();
            }
            case 5 -> System.out.println("Viewing Real-time Tickets...\n");
            case 6 -> {
                System.out.println("Exiting Vendor Actions...\n");
                return true;
            }
            default -> System.out.println("Invalid option. Please try again.\n");
        }
        return false;
    }

    private static void handleConsumerLogin() {
        System.out.println("Consumer Login:");
        System.out.println("1. Sign up Customer");
        System.out.println("2. Sign in Customer");
        System.out.println("3. Exit");
        System.out.println(); // Extra space for readability
        int choice = getUserChoice(1, 3);
        switch (choice) {
            case 1 -> System.out.println("Saving Customer...\n");
            case 2 -> handleConsumerActions();
            case 3 -> System.out.println("Exiting Consumer Login...\n");
        }
    }

    private static void handleConsumerActions() {
        boolean exit = false;
        while (!exit) {
            System.out.println("Consumer Options:");
            System.out.println("1. Remove/Purchase Tickets");
            System.out.println("2. View Tickets Bought");
            System.out.println("3. View Other Tickets");
            System.out.println("4. View Real-time Tickets Being Sold and Added");
            System.out.println("5. Exit");
            System.out.println(); // Extra space for readability
            exit = handleConsumerChoice(getUserChoice(1, 5));
        }
    }

    private static boolean handleConsumerChoice(int choice) {
        switch (choice) {
            case 1 -> System.out.println("Removing/Purchasing Tickets...\n");
            case 2 -> System.out.println("Viewing Tickets Bought...\n");
            case 3 -> System.out.println("Viewing Other Tickets...\n");
            case 4 -> System.out.println("Viewing Real-time Tickets...\n");
            case 5 -> {
                System.out.println("Exiting Consumer Actions...\n");
                return true;
            }
            default -> System.out.println("Invalid option. Please try again.\n");
        }
        return false;
    }

    private static void handleThreadManagement() {
        System.out.println("Starting/Stopping all threads for Vendors and Customers...\n");
        // Logic to start/stop threads across the system
    }
}


