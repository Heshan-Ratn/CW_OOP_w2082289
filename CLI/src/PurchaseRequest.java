import java.util.Scanner;
import java.util.Set;

public class PurchaseRequest {
    private final String eventName;
    private final int ticketsToBook;

    public PurchaseRequest(String eventName, int ticketsToBook) {
        this.eventName = eventName;
        this.ticketsToBook = ticketsToBook;
    }

    public String getEventName() {
        return eventName;
    }

    public int getTicketsToBook() {
        return ticketsToBook;
    }

    public static PurchaseRequest requestTickets(Set<String> eventDetails) {
        Scanner scanner = new Scanner(System.in);

        String eventName = promptForEventName(scanner, eventDetails);
        if (eventName == null) {
            System.out.println("Too many invalid attempts for event name. Request cancelled.");
            return null;
        }

        int ticketsToBook = promptForTicketQuantity(scanner);
        if (ticketsToBook == -1) {
            System.out.println("Too many invalid attempts for ticket quantity. Request cancelled.");
            return null;
        }

        System.out.println("Request successfully recorded: Event = " + eventName +
                ", Tickets to Book = " + ticketsToBook + "\n");

        return new PurchaseRequest(eventName, ticketsToBook);
    }

    // Helper method to prompt for a valid event name
    private static String promptForEventName(Scanner scanner, Set<String> eventDetails) {
        for (int attempts = 3; attempts > 0; attempts--) {
            System.out.print("Enter the event name: ");
            String inputEventName = scanner.nextLine().trim();

            if (eventDetails.contains(inputEventName)) {
                return inputEventName;
            } else {
                System.out.println("Invalid event name. Please choose from the following events:");
                eventDetails.forEach(System.out::println);
            }
        }
        return null;  // Return null if all attempts are exhausted
    }

    // Helper method to prompt for a valid ticket quantity
    private static int promptForTicketQuantity(Scanner scanner) {
        for (int attempts = 3; attempts > 0; attempts--) {
            System.out.print("Enter the number of tickets to book: ");
            try {
                int inputTicketsToBook = Integer.parseInt(scanner.nextLine().trim());

                if (inputTicketsToBook > 0) {
                    return inputTicketsToBook;
                } else {
                    System.out.println("Invalid quantity. Number of tickets must be greater than zero.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
        return -1;  // Return -1 if all attempts are exhausted
    }
}
