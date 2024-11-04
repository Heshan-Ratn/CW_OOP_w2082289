import java.io.*;
import java.nio.file.*;
import java.util.*;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

public class VendorSignUp {
    private static final String VENDOR_FILE = "Vendors.json";

    public void signUp() {
        Scanner scanner = new Scanner(System.in);
        String username = getUsername(scanner);
        String password = getPassword(scanner);
        String vendorId = generateUniqueVendorId(username);

        if (vendorId != null) {
            saveVendorData(vendorId, password);
            System.out.println("Sign-up successful! Your vendor ID is: " + vendorId);
        } else {
            System.out.println("Failed to generate a unique vendor ID. Please try again.");
        }
    }

    private String getUsername(Scanner scanner) {
        String username;
        while (true) {
            System.out.print("Enter a username (letters only): ");
            username = scanner.nextLine().toLowerCase();
            if (username.matches("[a-z]+")) break;
            System.out.println("Invalid username. Please use letters only.");
        }
        return username;
    }

    private String getPassword(Scanner scanner) {
        System.out.print("Enter a password: ");
        return scanner.nextLine();
    }

    private String generateUniqueVendorId(String username) {
        Set<String> existingIds = getExistingVendorIds();
        String vendorId;

        do {
            vendorId = username + String.format("%03d", new Random().nextInt(1000)).toLowerCase();
        } while (existingIds.contains(vendorId));

        return vendorId;
    }

    private Set<String> getExistingVendorIds() {
        Set<String> vendorIds = new HashSet<>();
        try (Reader reader = Files.newBufferedReader(Paths.get(VENDOR_FILE))) {
            List<VendorData> vendors = new Gson().fromJson(reader, new TypeToken<List<VendorData>>(){}.getType());
            for (VendorData vendor : vendors) vendorIds.add(vendor.getVendorId());
        } catch (IOException e) {
            System.out.println("Error reading vendor data: " + e.getMessage());
        }
        return vendorIds;
    }

    private void saveVendorData(String vendorId, String password) {
        List<VendorData> vendors = new ArrayList<>(getExistingVendors());
        vendors.add(new VendorData(vendorId, password));
        Gson gson = new GsonBuilder().setPrettyPrinting().create();//
        try (Writer writer = Files.newBufferedWriter(Paths.get(VENDOR_FILE))) {
//            new Gson().toJson(vendors, writer);
            gson.toJson(vendors, writer);
        } catch (IOException e) {
            System.out.println("Error writing vendor data: " + e.getMessage());
        }
    }

    private List<VendorData> getExistingVendors() {
        List<VendorData> vendors = new ArrayList<>();
        try (Reader reader = Files.newBufferedReader(Paths.get(VENDOR_FILE))) {
            vendors = new Gson().fromJson(reader, new TypeToken<List<VendorData>>(){}.getType());
        } catch (IOException e) {
            System.out.println("Error reading vendor data: " + e.getMessage());
        }
        return vendors;
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
    }
}

