import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Ticket {
    private String ticketId;
    private String eventName;
    private double price;
    private String timeDuration;
    private String date;
    private String vendorId;
    private String ticketStatus;
    private String customerId;

    // Using a single instance of Scanner
    private static Scanner scanner = new Scanner(System.in);

    public Ticket(String ticketId, String eventName,
                  double price, String timeDuration,
                  String date, String vendorId,
                  String ticketStatus,
                  String customerId) {
        this.ticketId = ticketId;
        this.eventName = eventName;
        this.price = price;
        this.timeDuration = timeDuration;
        this.date = date;
        this.vendorId = vendorId;
        this.ticketStatus = ticketStatus;
        this.customerId = customerId;
    }

    public String getTicketId() {
        return ticketId;
    }

    public String getTicketStatus() {
        return ticketStatus;
    }

    public String getEventName() {
        return eventName;
    }

    public void setTicketStatus(String ticketStatus) {
        this.ticketStatus = ticketStatus;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getVendorId() {
        return vendorId;
    }

    public String getCustomerId() {
        return customerId;
    }

    // Static method to create a list of tickets for a vendor
    public static List<Ticket> createTicketsForVendor(String vendorId, int batchSize, String eventName, double price, String timeDuration, String date) {
        List<Ticket> tickets = new ArrayList<>();
        for (int i = 1; i <= batchSize; i++) {
            String ticketId = vendorId + "-" + i;  // Generate a unique ticket ID
            tickets.add(new Ticket(ticketId, eventName, price, timeDuration, date, vendorId, "Available", "Not Set"));
        }
        return tickets;
    }

    // Overloaded method to create tickets based on user input
    public static List<Ticket> createTicketsForVendor(String vendorId) {
        // Gather validated inputs
        String eventName = getValidatedStringInput("Enter event name: ", "Event name cannot be empty.");
        double price = getValidatedDoubleInput("Enter ticket price: ", "Price must be greater than zero.");
        String timeDuration = getValidatedStringInput("Enter time duration (e.g., '2 hours'): ", "Time duration cannot be empty.");
        String date = getValidatedDateInput("Enter event date (e.g., '2024-11-10'): ");
        int batchSize = getValidatedIntInput("Enter batch size: ", "Batch size must be greater than zero.");

        // Create a list of tickets for the vendor
        List<Ticket> tickets = new ArrayList<>();
        for (int i = 1; i <= batchSize; i++) {
            String ticketId = vendorId + "-" + i;  // Generate a unique ticket ID
            tickets.add(new Ticket(ticketId, eventName, price, timeDuration, date, vendorId, "Available", "Not Set"));
        }

        return tickets;
    }

    // Helper method to validate and get a non-empty string input
    private static String getValidatedStringInput(String prompt, String errorMessage) {
        String input;
        while (true) {
            System.out.print(prompt);
            input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;  // Return if valid
            }
            System.out.println(errorMessage);  // Print error message and retry
        }
    }

    // Helper method to validate and get a positive double input
    private static double getValidatedDoubleInput(String prompt, String errorMessage) {
        double input;
        while (true) {
            System.out.print(prompt);
            if (scanner.hasNextDouble()) {
                input = scanner.nextDouble();
                if (input > 0) {
                    scanner.nextLine(); // Consume newline left by nextDouble()
                    return input;  // Return if valid
                }
                System.out.println(errorMessage);  // Print error message and retry
            } else {
                System.out.println("Invalid input. Please enter a valid number.");
                scanner.next();  // Consume invalid input
            }
        }
    }

    // Helper method to validate and get a date input in the format 'YYYY-MM-DD'
    private static String getValidatedDateInput(String prompt) {
        String input;
        while (true) {
            System.out.print(prompt);
            input = scanner.nextLine().trim();
            if (isValidDate(input)) {
                return input;  // Return if valid
            }
            System.out.println("Invalid date format. Please enter the date in the format 'YYYY-MM-DD'.");
        }
    }

    // Helper method to validate date format (e.g., "2024-11-10")
    private static boolean isValidDate(String date) {
        try {
            // Parse the date using ISO_LOCAL_DATE format (yyyy-MM-dd)
            LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
            return true;  // Valid format and logical date
        } catch (DateTimeParseException e) {
            return false;  // Invalid date
        }
    }

    // Helper method to validate and get a positive integer input
    private static int getValidatedIntInput(String prompt, String errorMessage) {
        int input = 0;
        while (true) {
            System.out.print(prompt);
            if (scanner.hasNextInt()) {
                input = scanner.nextInt();
                if (input > 0) {
                    scanner.nextLine(); // Consume newline left by nextInt()
                    return input;  // Return if valid
                }
                System.out.println(errorMessage);  // Print error message and retry
            } else {
                System.out.println("Invalid input. Please enter a valid integer.");
                scanner.next();  // Consume invalid input
            }
        }
    }
}
