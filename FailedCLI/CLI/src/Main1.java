import java.util.Scanner;

public class Main1 {
    private static final Scanner input = new Scanner(System.in);
    private static Configuration config = ConfigControl.checkConfiguration();

    public static void main(String[] args) {
        System.out.println("Welcome to Real-time Ticketing System by HKR\n");
        while (true) {
            printMainMenu();
            int option = getUserChoice(1, 5);
            if (option == 5) break;
            handleMainOption(option);
        }
        System.out.println("Exiting System...");
    }

    private static void printMainMenu() {
        System.out.println("Pick an option below:");
        System.out.println("1. Configuration Settings");
        System.out.println("2. Login to System");
        System.out.println("3. View Available Tickets");
        System.out.println("4. View Real-time Tickets Being Sold and Added");
        System.out.println("5. Exit System");
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
            case 4 -> {
                System.out.println("Viewing Real-time Tickets Being Sold and Added...\n");
            }
            default -> {
                System.out.println("Invalid option. Please try again.\n");
            }
        }
    }

    private static void handleConfigurationSettings() {
        System.out.println("Configuration Settings:");
        System.out.println("1. Edit Configuration Setting");
        System.out.println("2. View Configuration Setting");
        System.out.println(); // Extra space for readability
        int choice = getUserChoice(1, 2);
        if (choice == 1) {
            System.out.println("Editing Configuration Setting...\n");
            ConfigControl.editConfiguration();
            config = ConfigControl.checkConfiguration();
        } else {
            System.out.println("Viewing Configuration Setting...\n");
            config = ConfigControl.checkConfiguration();
            ConfigControl.viewConfiguration(config);
        }
    }

    private static void handleLogin() {
        System.out.println("Login:");
        System.out.println("1. Login as a Vendor");
        System.out.println("2. Login as a Consumer");
        System.out.println(); // Extra space for readability
        int choice = getUserChoice(1, 2);
        if (choice == 1) handleVendorLogin();
        else if (choice == 2) handleConsumerLogin();
    }

    private static void handleVendorLogin() {
        System.out.println("Vendor Login:");
        System.out.println("1. Sign up Vendor");
        System.out.println("2. Sign in Vendor");
        System.out.println(); // Extra space for readability
        int choice = getUserChoice(1, 2);
        if (choice == 1) {
            VendorSignUp vendorSignUp = new VendorSignUp();
            vendorSignUp.signUp();
            System.out.println("Saving Vendor...\n");
        }
        else if (choice == 2){
            VendorSignIn newSignIn = new VendorSignIn();
            Vendor vendorSignedIn = newSignIn.signIn(config);
            boolean vendorFound = newSignIn.isVendorFound(vendorSignedIn);
            handleVendorActions(vendorFound,vendorSignedIn);}
    }

    private static void handleVendorActions( boolean vendorFound, Vendor vendorSignedIn) {
        boolean exit = false;
        if(vendorFound) {
            System.out.println("Vendor with ID: "+ vendorSignedIn.getVendorId()+" found!\n");
            while (!exit) {
                System.out.println("Vendor Actions:");
                System.out.println("1. Add Tickets to the Ticket pool");
                System.out.println("2. View Added Tickets");
                System.out.println("3. View Other Tickets");
                System.out.println("4. Stop/Start for Tickets release to the Ticket pool");
                System.out.println("5. View Real-time Tickets Being Sold and Added");
                System.out.println("6. Exit");
                System.out.println(); // Extra space for readability
                exit = handleVendorChoice(getUserChoice(1, 6),vendorSignedIn);
            }
        }else{
            System.out.println("Credentials not found, unable to sign in.\n ");
        }
    }


    private static boolean handleVendorChoice(int choice, Vendor vendor) {
        switch (choice) {
            case 1: // Add Tickets
                System.out.println("Ticket addition process initiated...\n");
                if (vendor.CheckReleaseStatus()){
                    vendor.setTicketReleaseParameters(); // Set parameters before starting
                    Thread vendorThread = new Thread(vendor);
                    vendorThread.start(); // Start the vendor's ticket release thread
                }
                return false; // Return false to keep in the menu

            case 2:
                System.out.println("Viewing Added Tickets...\n");
                return false;
            case 3:
                System.out.println("Viewing Other Tickets...\n");
                TicketPool ticketPool = new TicketPool();
                ticketPool.viewAllTickets();
                return false;
            case 4: // Stop Tickets Added
                //vendor.stopSession(); // Call the stop method to halt ticket release
                return false;
            case 5:
                System.out.println("Viewing Real-time Tickets...\n");
                return false;
            case 6: // Exit
                return true;
            default:
                return false;
        }
    }


    private static void handleConsumerLogin() {
        System.out.println("Consumer Login:");
        System.out.println("1. Sign up Customer");
        System.out.println("2. Sign in Customer");
        System.out.println(); // Extra space for readability
        int choice = getUserChoice(1, 2);
        if (choice == 1) System.out.println("Saving Customer...\n");
        else if (choice == 2) handleConsumerActions();
    }

    private static void handleConsumerActions() {
        boolean exit = false;
        while (!exit) {
            System.out.println("Consumer Options:");
            System.out.println("1. Remove/Purchase Tickets");
            System.out.println("2. View Tickets Bought");
            System.out.println("3. View Other Tickets");
            System.out.println("4. View Real-time Tickets");
            System.out.println("5. Exit");
            System.out.println(); // Extra space for readability
            exit = handleConsumerChoice(getUserChoice(1, 5));
        }
    }

    private static boolean handleConsumerChoice(int choice) {
        return switch (choice) {
            case 1 -> {
                System.out.println("Removing/Purchasing Tickets...\n");
                yield false;
            }
            case 2 -> {
                System.out.println("Viewing Tickets Bought...\n");
                yield false;
            }
            case 3 -> {
                System.out.println("Viewing Other Tickets...\n");
                yield false;
            }
            case 4 -> {
                System.out.println("Viewing Real-time Tickets...\n");
                yield false;
            }
            case 5 -> true;
            default -> false;
        };
    }
}
