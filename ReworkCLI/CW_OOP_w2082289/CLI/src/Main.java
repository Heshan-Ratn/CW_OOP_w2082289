import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner input = new Scanner(System.in);
    private static Configuration config = new Configuration().loadConfiguration("config.json");
    private static TicketPool ticketPool = TicketPool.getInstance(config.getMaxTicketCapacity());

    public static void main(String[] args) {
        System.out.println("Welcome to Real-time Ticketing System by HKR\n");
        while (true) {
            ticketPool.setMaxCapacity(config.getMaxTicketCapacity());
            System.out.println(ticketPool.getMaxCapacity());
            printMainMenu();
            int option = getUserChoice(1, 8);
            if (option == 8) break;
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
        System.out.println("5. Stimulate ticket operations in the ticket pool");
        System.out.println("6. Start System if Stopped");
        System.out.println("7. Stop System (Stop All Ticket Operations)");
        System.out.println("8. Exit System");
        System.out.println();
    }

    private static int getUserChoice(int min, int max) {
        while (true) {
            try {
                System.out.print("Enter your choice: ");
                int choice = Integer.parseInt(input.nextLine().trim());
                System.out.println();
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
            case 3 -> System.out.println("Viewing Available Tickets...\n");
            case 4 -> System.out.println("Viewing Real-time Tickets Being Sold and Added...\n");
            case 5 -> System.out.println("Stimulating ticket operations in the ticket pool...\n");
            case 6 -> System.out.println("Starting System if Stopped...\n");
            case 7 -> System.out.println("Stopping System (All Ticket Operations Halted)...\n");
            default -> System.out.println("Invalid option. Please try again.\n");
        }
    }

    private static void handleConfigurationSettings() {
        System.out.println("Configuration Settings:");
        System.out.println("1. Edit Admin credentials");
        System.out.println("2. Edit Configuration Setting");
        System.out.println("3. View Configuration Setting");
        System.out.println("4. Exit");
        int choice = getUserChoice(1, 4);
        switch (choice) {
            case 1 -> {System.out.println("Editing Admin Credentials...\n");
                config.setAdminCredentials();
                System.out.println("\n");
            }
            case 2 -> {System.out.println("Editing Configuration Setting...\n");
                config.promptUserForConfigurationUpdate();
                System.out.println("\n");
            }
            case 3 -> {System.out.println("Viewing Configuration Setting...\n");
                config.displayConfiguration();
                System.out.println("\n");
            }
            case 4 -> System.out.println("Exiting Configuration Settings...\n");
        }
    }

    private static void handleLogin() {
        System.out.println("Login:");
        System.out.println("1. Login as a Vendor");
        System.out.println("2. Login as a Customer");
        System.out.println("3. Exit");
        int choice = getUserChoice(1, 3);
        switch (choice) {
            case 1 -> handleVendorLogin();
            case 2 -> handleCustomerLogin();
            case 3 -> System.out.println("Exiting Login Menu...\n");
        }
    }

    private static void handleVendorLogin() {
        System.out.println("Vendor Login:");
        System.out.println("1. Sign up Vendor");
        System.out.println("2. Sign in Vendor");
        System.out.println("3. Exit");
        int choice = getUserChoice(1, 3);
        switch (choice) {
            case 1 -> {System.out.println("Signing up Vendor and Saving...\n");
               boolean signup = Vendor.signUp();
               if(signup){
                   System.out.println("Sign up was successful!\n");
               }else{
                   System.out.println("Vendor Sign up was unsuccessful!\n");
               }
            }
            case 2 -> {
                Vendor vendor = Vendor.signIn();
                if (vendor != null){
                handleVendorActions(vendor);}
            }
            case 3 -> System.out.println("Exiting Vendor Login...\n");
        }
    }

    private static void handleVendorActions(Vendor vendor) {
        vendor.setTicketReleaseRate();
        vendor.setTicketPool(ticketPool);
        boolean exit = false;
        while (!exit) {
            System.out.println("Vendor Actions:");
            System.out.println("1. Add Tickets (Create Threads)");
            System.out.println("2. View Added Tickets of the Vendor");
            System.out.println("3. View Other Tickets");
            System.out.println("4. Stop Ticket Release for the Vendor");
            System.out.println("5. Stop Ticket Release for the Vendor");
            System.out.println("6. View Real-time Tickets Being Sold and Added");
            System.out.println("7. Exit");
            int choice = getUserChoice(1, 7);
            exit = handleVendorChoice(choice, vendor);
        }
    }

    private static boolean handleVendorChoice(int choice, Vendor vendor) {
        switch (choice) {
            case 1 -> {
                System.out.println("Adding Tickets...\n");
                if (vendor.checkReleaseStatus()) {
                    List<Ticket> tickets = Ticket.createTicketsForVendor(vendor.getVendorId());
                    vendor.setTicketBatch(tickets);
                    Thread vendorThread = new Thread(vendor);
                    vendorThread.start();
                }
            }
            case 2 -> {
                System.out.println("Viewing Vendor's Added Tickets...\n");
                ticketPool.viewAvailableTicketCountsByVendor(vendor.getVendorId());
            }
            case 3 -> System.out.println("Viewing Other Tickets...\n");
            case 4 -> System.out.println("Stopping Ticket Release...\n");
            case 5 -> System.out.println("Activating Ticket Release...\n");
            case 6 -> System.out.println("Viewing Real-time Tickets Being Sold and Added...\n");
            case 7 -> {
                System.out.println("Exiting Vendor Actions...\n");
                return true;
            }
            default -> System.out.println("Invalid option. Please try again.\n");
        }
        return false;
    }

    private static void handleCustomerLogin() {
        System.out.println("Customer Login:");
        System.out.println("1. Sign up Customer");
        System.out.println("2. Sign in Customer");
        System.out.println("3. Exit");
        int choice = getUserChoice(1, 3);
        switch (choice) {
            case 1 -> System.out.println("Signing up Customer and Saving...\n");
            case 2 -> handleCustomerActions();
            case 3 -> System.out.println("Exiting Customer Login...\n");
        }
    }

    private static void handleCustomerActions() {
        boolean exit = false;
        while (!exit) {
            System.out.println("Customer Options:");
            System.out.println("1. Remove/Purchase Tickets");
            System.out.println("2. View Tickets Bought");
            System.out.println("3. View Other Tickets");
            System.out.println("4. Stop purchase of Tickets for the vendor");
            System.out.println("5. Activate purchase of Tickets for the vendor");
            System.out.println("6. View Real-time Tickets Being Sold and Added");
            System.out.println("7. Exit");
            int choice = getUserChoice(1, 7);
            exit = handleCustomerChoice(choice);
        }
    }

    private static boolean handleCustomerChoice(int choice) {
        switch (choice) {
            case 1 -> System.out.println("Removing/Purchasing Tickets...\n");
            case 2 -> System.out.println("Viewing Tickets Bought...\n");
            case 3 -> System.out.println("Viewing Other Tickets...\n");
            case 4 -> System.out.println("Stopping purchase of Tickets for the vendor...\n");
            case 5 -> System.out.println("Activating purchase of Tickets for the vendor...\n");
            case 6 -> System.out.println("Viewing Real-time Tickets Being Sold and Added...\n");
            case 7 -> {
                System.out.println("Exiting Customer Actions...\n");
                return true;
            }
            default -> System.out.println("Invalid option. Please try again.\n");
        }
        return false;
    }
}

