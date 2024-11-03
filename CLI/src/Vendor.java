import java.util.Scanner;

public class Vendor implements Runnable {
    private final String vendorId;
    private final String vendorPassword;
    private  int ticketsPerRelease; //Rate at which tickets will be released.
    private  int releaseInterval; //The interval between tickets.
    private final TicketPool ticketPool;
    private volatile boolean isActive;
    private int quantity;

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


    public void setTicketReleaseParameter(){
        Scanner input = new Scanner(System.in);
        System.out.print("Enter number of tickets per release: ");
        this.releaseInterval = input.nextInt();
        System.out.println("Enter quantity of tickets to be released: ");
        this.quantity = input.nextInt();
    }















    public void run() {
        while (isActive) {
            try {
                for (int i = 1; i <= quantity; i++) {
                    if (ticketPool.isFull()) {

                        break;
                    }
                }
                Thread.sleep(releaseInterval);
            } catch (InterruptedException e) {
                System.out.println("Vendor " + vendorId + " interrupted");
            }
        }
    }

    public void stopSession() {
        isActive = false;
    }
}
