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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

public class Vendor implements Runnable {
    private String vendorId;
    private String password;
    private TicketPool ticketPool; // Shared ticket pool
    private AtomicBoolean releasingTickets = new AtomicBoolean(true); // Vendor-level stop/start authority
    private static AtomicBoolean adminStopAll = new AtomicBoolean(false); // Admin-level stop/start authority
    private int ticketReleaseRate; // in milliseconds
    private List<Ticket> ticketBatch; // For ticket batch storage
    private static final String VENDOR_FILE = "Vendors.json";
    private static final int MAX_ATTEMPTS = 3;
    private static final Logger logger = LogManager.getLogger(Vendor.class);
    private static final Logger loggerRun = LogManager.getLogger("VendorRun");

    // Constructor for initializing vendor credentials only (for sign-in or sign-up)
    private Vendor(String vendorId, String password) {
        this.vendorId = vendorId;
        this.password = password;
    }

    // Constructor for using vendor functions
    public Vendor(String vendorId, String password, TicketPool ticketPool, List<Ticket> ticketBatch) {
        this.vendorId = vendorId;
        this.password = password;
        this.ticketPool = ticketPool;
        this.ticketReleaseRate = loadTicketReleaseRateFromConfig();
        this.ticketBatch = ticketBatch;
    }

    public void setTicketPool(TicketPool ticketPool) {
        this.ticketPool = ticketPool;
    }

    public void setTicketReleaseRate() {
        this.ticketReleaseRate = loadTicketReleaseRateFromConfig();
    }

    public void setTicketBatch(List<Ticket> ticketBatch) {
        this.ticketBatch = ticketBatch;
    }

    public String getVendorId() {
        return vendorId;
    }

    public static AtomicBoolean isAdminStopAll() {
        return adminStopAll;
    }

    public AtomicBoolean isReleasingTickets() {
        return releasingTickets;
    }


    // Method to load the ticket release rate from the config.json file
    private int loadTicketReleaseRateFromConfig() {
        try (FileReader reader = new FileReader("config.json")) {
            Gson gson = new Gson();
            Configuration config = gson.fromJson(reader, Configuration.class);
            return (int) config.getTicketReleaseRate();  // Get the release rate from config
        } catch (IOException e) {
            System.out.println("Error reading config.json, using default rate.");
            return 1000;  // Default value if config.json is not found or read error occurs
        }
    }


     public static String[] promptVendorCredentials() {
        Scanner scanner = new Scanner(System.in);
        String vendorId, password;

        for (int attempts = 0; attempts < MAX_ATTEMPTS; attempts++) {
            System.out.print("Enter Vendor ID (7 characters, last 3 must be digits): ");
            vendorId = scanner.nextLine().trim();

            if (!isValidVendorId(vendorId)) {
                System.out.println("Invalid Vendor ID. Please ensure it is 7 characters with the last 3 as digits.");
                continue;
            }

            System.out.print("Enter Password (8-12 characters): ");
            password = scanner.nextLine().trim();

            if (!isValidPassword(password)) {
                System.out.println("Invalid Password. It must be between 8-12 characters.");
                continue;
            }

            return new String[]{vendorId, password}; // Successful input
        }

        System.out.println("Maximum attempts reached. Returning to main menu.");
        return new String[]{"", ""}; // Failed input after max attempts
    }

    private static boolean isValidVendorId(String vendorId) {
        return vendorId.length() == 7 && Pattern.matches("^[A-Za-z0-9_]{4}[0-9]{3}$", vendorId);
    }
    private static boolean isValidPassword(String password) {
        return password.length() >= 8 && password.length() <= 12;
    }



    // Sign up a new vendor
    public static synchronized boolean signUp() {
        String[] newVendorCredentials = promptVendorCredentials();
        if (newVendorCredentials[0].equals("")&&newVendorCredentials[1].equals("")) {
            return false;
        }
        List<VendorData> vendors = loadVendors();

        // Check if vendorId is already taken
        for (VendorData vendor : vendors) {
            if (vendor.getVendorId().equals(newVendorCredentials[0])) {
                System.out.println("Vendor ID already taken.");
                return false;
            }
        }

        vendors.add(new VendorData(newVendorCredentials[0], newVendorCredentials[1]));

        // Write updated vendor list back to file
        return saveVendors(vendors);
    }

    // Sign in an existing vendor
    public static synchronized Vendor signIn() {
        String[] vendorCredentials = promptVendorCredentials();
        if (vendorCredentials[0].equals("") && vendorCredentials[1].equals("")) {
            System.out.println("Returning Back to main menu all the sign in attempts Failed!\n");
            return null;
        }

        List<VendorData> vendors = loadVendors();
        if (vendors.isEmpty()) {
            System.out.println("Vendors database is empty!.\n");
            return null;
        }

        for (VendorData vendor : vendors) {
            if (vendor.getVendorId().equals(vendorCredentials[0]) && vendor.getPassword().equals(vendorCredentials[1])) {
                System.out.println("Vendor signed in successfully!\n");
                return new Vendor(vendorCredentials[0], vendorCredentials[1]); // Initialize only credentials for sign-in
            }
        }
        System.out.println("Sign in failed: Incorrect ID or password.\n");
        return null;
    }

    // Load vendors from file
    private static List<VendorData> loadVendors() {
        try (FileReader reader = new FileReader(VENDOR_FILE)) {
            Gson gson = new Gson();
            Type vendorListType = new TypeToken<List<VendorData>>() {}.getType();
            List<VendorData> vendors = gson.fromJson(reader, vendorListType);
            return vendors != null ? vendors : new ArrayList<>(); // Return empty list if JSON is null
        } catch (IOException e) {
            System.out.println("Error loading vendors: " + e.getMessage());
            return new ArrayList<>(); // Return an empty list if file doesn't exist or read error occurs
        }
    }

    // Save vendors to file
    private static boolean saveVendors(List<VendorData> vendors) {
        try (FileWriter writer = new FileWriter(VENDOR_FILE)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(vendors, writer);
            System.out.println("Vendor saved successfully.");
            return true;
        } catch (IOException e) {
            System.out.println("Error saving vendor: " + e.getMessage());
            return false;
        }
    }

    // Inner class for vendor data storage
    private static class VendorData {
        private final String vendorId;
        private final String password;

        public VendorData(String vendorId, String password) {
            this.vendorId = vendorId;
            this.password = password;
        }

        public String getVendorId() {
            return vendorId;
        }

        public String getPassword() {
            return password;
        }
    }

    // Runnable method: releasing tickets in batches
    @Override
    public void run() {
        for (Ticket ticket : ticketBatch) {
            if (!releasingTickets.get() || adminStopAll.get()) {
                loggerRun.info("Ticket release stopped for vendor: " + vendorId);
                return;
            }

            boolean added = ticketPool.addTicket(ticket); // Synchronization handled in TicketPool
            if (added) {
                loggerRun.info("Ticket added by " + vendorId + ": " + ticket.getTicketId()+ " "+ticket.getEventName());
            } else {
                loggerRun.info("Ticket pool is full. Could not add ticket: " + ticket.getTicketId() + " "+ticket.getEventName());
                break;
            }

            try {
                TimeUnit.MILLISECONDS.sleep(ticketReleaseRate);
                if (!releasingTickets.get() || adminStopAll.get()) {
                    loggerRun.info("Ticket release stopped after sleep for vendor: " + vendorId);
                    return;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                loggerRun.error("Ticket release interrupted for vendor: " + vendorId);
                return;
            }
        }
        loggerRun.info("Ticket release completed for vendor: " + vendorId);
    }

    // Admin method to stop and resume all vendors
    public static void stopAllReleases() {
        adminStopAll.set(true);
    }

    public static void resumeAllReleases() {
        adminStopAll.set(false);
    }

    // Stop and resume ticket release for this vendor
    public void stopReleasingTickets() {
        releasingTickets.set(false);
    }

    public void resumeReleasingTickets() {
        releasingTickets.set(true);
    }

    // This method will be used to check status of releasing ability of tickets.
    public boolean checkReleaseStatus() {
        if (adminStopAll.get()) {
            System.out.println("Ticket release paused by admin for all vendors.\n");
            return false;
        } else if (!releasingTickets.get()) {
            System.out.println("Ticket release paused by the vendor: " + vendorId+"\n");
            return false;
        } else {
            System.out.println("Ticket release is active for vendor: " + vendorId+"\n");
            return true;
        }
    }

}
