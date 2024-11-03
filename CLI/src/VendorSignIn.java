import java.io.*;
import java.nio.file.*;
import java.util.*;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

public class VendorSignIn {
    private static final String VENDOR_FILE = "Vendors.json";


    public Vendor signIn(Configuration config) {
        Scanner scanner = new Scanner(System.in);
        String vendorId, password;
        Configuration configuration = config;

        // Prompt for vendor ID
        while (true) {
            System.out.print("Enter your vendor ID (format: letters+3digits): ");
            vendorId = scanner.nextLine().toLowerCase();
            if (vendorId.matches("^[a-z]+\\d{3}$")) break;
            System.out.println("Invalid vendor ID. Please use the correct format.");
        }

        // Prompt for password
        System.out.print("Enter your password: ");
        password = scanner.nextLine();

        // Return the Vendor object if credentials are valid
        return checkCredentials(vendorId, password,config.getTicketReleaseRate());
    }

    public boolean isVendorFound(Vendor vendor) {
        return vendor != null; // If vendor is not null, it was found
    }

    private Vendor checkCredentials(String vendorId, String password, int ticketReleaseRate) {
        try (Reader reader = Files.newBufferedReader(Paths.get(VENDOR_FILE))) {
            List<VendorData> vendors = new Gson().fromJson(reader, new TypeToken<List<VendorData>>(){}.getType());
            for (VendorData vendor : vendors) {
                if (vendor.getVendorId().equals(vendorId) && vendor.getPassword().equals(password)) {
                    // Return a new Vendor object if credentials are valid
                    return new Vendor(vendorId, password,ticketReleaseRate); // Adjust the parameters as necessary
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading vendor data: " + e.getMessage());
        }
        return null; // Return null if no matching vendor found
    }

    private class VendorData {
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
}

