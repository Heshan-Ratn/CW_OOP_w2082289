import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Main {
    private static final Scanner input = new Scanner(System.in);
    private static Configuration config;
    private static TicketPool ticketPool;

    public static void main(String[] args) {
        System.out.println("Welcome to the Real-time Ticketing System by HKR!");

        // Load Configuration Settings
        config = new Configuration().loadConfiguration("Config.json");
        if (config == null) {
            System.out.println("No saved configuration found. Loading default settings...");
            config = new Configuration(); // Load default settings
        }

        // Display Configuration Menu (only once per application run)
        boolean configExit = false;
        while (!configExit) {
            configExit = handleConfigurationSettings();
        }
        //Initializing the shared ticket pool to be used in the application.
        ticketPool = TicketPool.getInstance(config.getMaxTicketCapacity());

        // Main Menu Loop
        boolean exitSystem = false;
        while (!exitSystem) {
            printMainMenu();
            int option = getUserChoice(1, 7); // Adjusted based on your main menu structure
            exitSystem = handleMainOption(option);
        }

        System.out.println("Exiting System... Goodbye!");
    }

    // Configuration Menu
    private static boolean handleConfigurationSettings() {
        System.out.println("\nConfiguration Settings:");
        System.out.println("1. Edit Admin credentials");
        System.out.println("2. Edit Configuration Setting");
        System.out.println("3. View Configuration Setting");
        System.out.println("4. Exit");

        int choice = getUserChoice(1, 4);
        switch (choice) {
            case 1 -> {System.out.println("Editing Admin Credentials...\n");
                config.setAdminCredentials();
                System.out.println();
            }
            case 2 -> {System.out.println("Editing Configuration Setting...\n");
                config.promptUserForConfigurationUpdate();
                System.out.println("\n");
            }
            case 3 -> {System.out.println("Viewing Configuration Setting...\n");
                config.displayConfiguration();
                System.out.println("\n");
            }
            case 4 -> {System.out.println("Exiting Configuration Settings...\n");
                return true;
            }
        }
        return false;
    }

    // Main Menu
    private static void printMainMenu() {
        System.out.println("\nMain Menu:");
        System.out.println("1. Login");
        System.out.println("2. View Available Tickets");
        System.out.println("3. View Number of Tickets Booked for each event");
        System.out.println("4. Stimulate ticket operations in the ticket pool");
        System.out.println("5. Start System if Stopped");
        System.out.println("6. Stop System (Stop All Ticket Operations)");
        System.out.println("7. Exit System");
    }

    private static boolean handleMainOption(int option) {
        switch (option) {
            case 1 -> handleLogin();
            case 2 ->  {
                System.out.println("Viewing Available Tickets...\n");
                ticketPool.countAvailableTicketsByEvent();
                System.out.println();
            }
            case 3 -> {
                System.out.println("Viewing number of booked tickets per each event... \n");
                ticketPool.countBookedTicketsByEvent();
                System.out.println();
            }
            case 4 -> {
                System.out.println("Stimulating ticket operations in the ticket pool...\n");
                StimulateRelease stimulateVendor = new StimulateRelease(ticketPool,(int)config.getTicketReleaseRate());
                stimulateVendor.startBotRelease();

                StimulatePurchase stimulateCustomers = new StimulatePurchase(ticketPool, (int)config.getCustomerRetrievalRate());
                stimulateCustomers.startBotPurcahse();
            }
            case 5 -> {
                System.out.println("Starting System if Stopped...\n");
                Vendor.resumeAllReleases();
                Customer.resumeAllPurchases();
            }
            case 6 -> {
                System.out.println("Stopping System (All Ticket Operations Halted)...\n");
                Vendor.stopAllReleases();
                Customer.stopAllPurchases();
            }
            case 7 -> {
                return true;
            }
            default -> System.out.println("Invalid option. Please try again.");
        }
        return false;
    }

    // Login Menu
    private static void handleLogin() {
        System.out.println("\nLogin:");
        System.out.println("1. Login as a Vendor");
        System.out.println("2. Login as a Customer");
        System.out.println("3. Exit");

        int choice = getUserChoice(1, 3);
        switch (choice) {
            case 1 -> handleVendorLogin();
            case 2 -> handleCustomerLogin();
            case 3 -> System.out.println("Exiting login menu...");
            default -> System.out.println("Invalid option. Please try again.");
        }
    }

    private static void handleVendorLogin() {
        System.out.println("\nVendor Login:");
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
            case 3 -> System.out.println("Exiting vendor login...");
            default -> System.out.println("Invalid option. Please try again.");
        }
    }

    private static void handleVendorActions(Vendor vendor) {
        vendor.setTicketReleaseRate((int)config.getTicketReleaseRate());
        vendor.setTicketPool(ticketPool);
        boolean exit = false;
        while (!exit) {
            System.out.println("Vendor Actions:");
            System.out.println("1. Add Tickets (Create Threads)");
            System.out.println("2. View Added Tickets of the Vendor");
            System.out.println("3. View Other Tickets");
            System.out.println("4. Stop Ticket Release for the Vendor");
            System.out.println("5. Activating Ticket Release for the Vendor");
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
                System.out.println();
            }
            case 3 -> {
                System.out.println("Viewing Other Tickets...\n");
                ticketPool.countAvailableTicketsByEvent();
                System.out.println();
            }
            case 4 -> {
                System.out.println("Stopping Ticket Release...\n");
                vendor.stopReleasingTickets();
            }
            case 5 -> {
                System.out.println("Activating Ticket Release...\n");
                vendor.resumeReleasingTickets();
            }
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
        System.out.println("\nCustomer Login:");
        System.out.println("1. Sign up Customer");
        System.out.println("2. Sign in Customer");
        System.out.println("3. Exit");

        int choice = getUserChoice(1, 3);
        switch (choice) {
            case 1 -> {
                System.out.println("Signing up Customer and Saving...\n");
                boolean signup = Customer.signUp();
                if(signup){
                    System.out.println("Sign up was successful!\n");
                }else{
                    System.out.println("Customer Sign up was unsuccessful!\n");
                }
            }
            case 2 -> {
                Customer customer = Customer.signIn();
                if(customer != null){
                    handleCustomerActions(customer);}
            }
            case 3 -> System.out.println("Exiting customer login...");
            default -> System.out.println("Invalid option. Please try again.");
        }
    }

    private static void handleCustomerActions(Customer customer) {
        customer.setCustomerRetrievalRate((int) config.getCustomerRetrievalRate());
        customer.setTicketPool(ticketPool);
        boolean exit = false;
        while (!exit) {
            System.out.println("Customer Options:");
            System.out.println("1. Purchase Tickets");
            System.out.println("2. View Tickets Bought");
            System.out.println("3. View Other Tickets");
            System.out.println("4. Stop purchase of Tickets for the customer");
            System.out.println("5. Activate purchase of Tickets for the customer");
            System.out.println("6. View Real-time Tickets Being Sold and Added");
            System.out.println("7. Exit");
            int choice = getUserChoice(1, 7);
            exit = handleCustomerChoice(choice, customer);
        }
    }

    private static boolean handleCustomerChoice(int choice, Customer customer) {
        switch (choice) {
            case 1 -> {
                System.out.println("Purchasing Tickets...\n");
                if (customer.checkPurchaseStatus()) {
                    Set<String> eventDetails = ticketPool.getEventDetails();  // Fetch valid event names
                    PurchaseRequest request = PurchaseRequest.requestTickets(eventDetails);
                    if (request != null) {
                        customer.setPurchaseRequest(request);
                        Thread customerThread = new Thread(customer);
                        customerThread.start();
                    } else {
                        System.out.println("No purchase request available to process.\n");
                    }
                }
            }
            case 2 -> {
                System.out.println("Viewing Tickets Bought...\n");
                ticketPool.countBookedTicketsByCustomerId(customer.getCustomerId());
                System.out.println();
            }
            case 3 -> {
                System.out.println("Viewing Other Tickets...\n");
                ticketPool.countAvailableTicketsByEvent();
                System.out.println();
            }
            case 4 -> {
                System.out.println("Stopping purchase of Tickets for the customer...\n");
                customer.stopPurchasingTickets();
            }
            case 5 -> {
                System.out.println("Activating purchase of Tickets for the customer...\n");
                customer.resumePurchasingTickets();
            }
            case 6 -> System.out.println("Viewing Real-time Tickets Being Sold and Added...\n");
            case 7 -> {
                System.out.println("Exiting Customer Actions...\n");
                return true;
            }
            default -> System.out.println("Invalid option. Please try again.\n");
        }
        return false;
    }

    // Helper Methods
    private static int getUserChoice(int min, int max) {
        while (true) {
            try {
                System.out.print("Enter your choice: ");
                int choice = Integer.parseInt(input.nextLine().trim());
                if (choice >= min && choice <= max) {
                    return choice;
                }
                System.out.printf("Please enter a number between %d and %d.%n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }
}

