import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;


public class Configuration {
    private  int totalTickets; //static since this is shared in the system.
    private int ticketReleaseRate;
    private int customerRetrievalRate;
    private int maxTicketCapacity;//static since this is shared in the system.

    // Constructor
    public Configuration() {
        // Initialize totalTickets by counting tickets in Tickets.json
        totalTickets = countTicketsFromFile("Tickets.json");
    }

    // Getters and Setters.
    public int getTotalTickets() {
        return totalTickets;
    }

//    public void setTotalTickets(int totalTickets) {
//        this.totalTickets = totalTickets;
//    }

    public int getTicketReleaseRate() {
        return ticketReleaseRate;
    }

    public void setTicketReleaseRate(int ticketReleaseRate) {
        this.ticketReleaseRate = ticketReleaseRate;
    }

    public int getCustomerRetrievalRate() {
        return customerRetrievalRate;
    }

    public void setCustomerRetrievalRate(int customerRetrievalRate) {this.customerRetrievalRate = customerRetrievalRate;}

    public int getMaxTicketCapacity() {
        return maxTicketCapacity;
    }

    public void setMaxTicketCapacity(int maxTicketCapacity) {
        this.maxTicketCapacity = maxTicketCapacity;
    }

    // Method to count ticket objects in Tickets.json
    private int countTicketsFromFile(String ticketFilePath) {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(ticketFilePath)) {
            Type ticketListType = new TypeToken<List<Ticket>>() {}.getType();
            List<Ticket> tickets = gson.fromJson(reader, ticketListType);
            return tickets != null ? tickets.size() : 0;
        } catch (IOException e) {
            e.printStackTrace();
            return 0; // If there's an issue, set count to 0 as a fallback
        }
    }

    // Save configuration to JSON file
    public void saveConfigToFile(String filePath) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(this, writer);
            System.out.println("Configuration saved to " + filePath+"\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load configuration from JSON file
    public static Configuration loadConfigFromFile(String filePath) {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(filePath)) {
            return gson.fromJson(reader, Configuration.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Validation method
    public boolean validateConfig() {
        return totalTickets >= 0 && ticketReleaseRate > 0 && customerRetrievalRate > 0 && maxTicketCapacity >= totalTickets;
    }
}






