import java.util.Scanner;

public class Vendor implements Runnable {
    private final String vendorId;
    private final String vendorPassword;
    private  int ticketsPerRelease; //Rate at which tickets will be released.
    private  int releaseInterval; //The interval between tickets.
    private final TicketPool ticketPool;
    protected volatile boolean isActive;
    private int quantity;
    private double ticketPrice;
    private String eventDetails;

    public Vendor (String vendorId, String vendorPassword, int ticketsPerRelease){
        this.vendorId = vendorId;
        this.vendorPassword = vendorPassword;
        this.ticketsPerRelease = ticketsPerRelease;
        this.ticketPool = new TicketPool();
        this.isActive = true;
    }

    public String getVendorId() {
        return vendorId;
    }


public void setTicketReleaseParameters() {
    Scanner input = new Scanner(System.in);
    // Validate ticket price
    this.ticketPrice = getValidDoubleInput(input, "Enter ticket price (must be positive): ");
    // Validate quantity
    this.quantity = getValidIntInput(input, "Enter quantity of tickets to be released (must be positive): ");
    // Validate release interval (allow zero)
    this.releaseInterval = getValidIntInput(input, "Enter interval between releases (ms, must be non-negative): ");
    // Get event details
    System.out.print("Enter the Name of the event for which tickets are released: ");
    this.eventDetails = input.nextLine();
    System.out.println();
}

    private double getValidDoubleInput(Scanner input, String message) {
        double value;
        while (true) {
            System.out.print(message);
            if (input.hasNextDouble()) {
                value = input.nextDouble();
                if (value > 0) {  // Check for positive values
                    input.nextLine(); // Consume the newline
                    return value; // Valid input
                } else {
                    System.out.println("Error: Value must be positive.");
                }
            } else {
                System.out.println("Error: Please enter a valid number.");
                input.next(); // Clear invalid input
            }
        }
    }

    private int getValidIntInput(Scanner input, String message) {
        int value;
        while (true) {
            System.out.print(message);
            if (input.hasNextInt()) {
                value = input.nextInt();
                if (value >= 0) {  // Check for non-negative values
                    input.nextLine(); // Consume the newline
                    return value; // Valid input
                } else {
                    System.out.println("Error: Value must be non-negative.");
                }
            } else {
                System.out.println("Error: Please enter a valid integer.");
                input.next(); // Clear invalid input
            }
        }
    }



    public void run() {
        int remainingQuantity = quantity;

        while (isActive && remainingQuantity > 0) {
            int ticketsToRelease = Math.min(ticketsPerRelease, remainingQuantity);

            // Try adding tickets to TicketPool, ensuring thread safety
            boolean added = ticketPool.addTickets(vendorId, eventDetails, ticketPrice, ticketsToRelease, this);
            if (added) {
                remainingQuantity -= ticketsToRelease;
            }

            // Pause the thread between sub-batch releases
            try {
                Thread.sleep(releaseInterval);
            } catch (InterruptedException e) {
                System.out.println("Vendor " + vendorId + " interrupted.");
                Thread.currentThread().interrupt();
            }
        }
    }

    public void stopSession() {
        isActive = !isActive; // Toggle the value of isActive
        if (isActive) {
            System.out.println("Resuming ticket release for vendor: " + vendorId+"\n");
        } else {
            System.out.println("Pausing ticket release for vendor: " + vendorId +"\n");
        }
    }
    public boolean CheckReleaseStatus() {
        if (isActive) {
            System.out.println("Ticket release for vendor: " + vendorId+ " is Active.\n");
            return true;
        } else {
            System.out.println("Ticket release for vendor: " + vendorId+" has stopped\n");
            return false;
        }
    }
}
