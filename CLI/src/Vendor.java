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
        System.out.print("Enter ticket price: ");
        this.ticketPrice = input.nextDouble();
        input.nextLine(); // Consume the newline
        System.out.print("Enter quantity of tickets to be released: ");
        this.quantity = input.nextInt();
        System.out.print("Enter interval between releases (ms): ");
        this.releaseInterval = input.nextInt();
        input.nextLine(); // Consume the newline
        System.out.println("Enter the Name of the event for which tickets are released: ");
        this.eventDetails = input.nextLine();
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
