public class TicketPoolTest {
    public static void main(String[] args) {
        int maxCapacity = 10;  // Set a low max capacity for testing
        TicketPool pool = TicketPool.getInstance(maxCapacity);

        // Define the number of threads (vendors) trying to add tickets
        int numVendors = 5;

        // Creating threads for each vendor
        Thread[] vendorThreads = new Thread[numVendors];
        for (int i = 0; i < numVendors; i++) {
            final int vendorId = i + 1;  // Assign a unique vendor ID
            vendorThreads[i] = new Thread(() -> {
                for (int j = 0; j < 5; j++) {  // Each vendor tries to add 5 tickets
                    Ticket newTicket = new Ticket(
                            "T" + vendorId + "-" + j,
                            "Event " + vendorId,
                            20.0 + j,
                            "2 hours",
                            "2023-12-25",
                            "Vendor" + vendorId,
                            "Available",
                            "Not Set"
                    );
                    boolean added = pool.addTicket(newTicket);
                    System.out.println("Vendor " + vendorId + " attempted to add ticket " + newTicket.getTicketId()
                            + ": " + (added ? "Success" : "Failed (Pool Full)"));
                }
            });
        }

        // Start all vendor threads
        for (Thread vendorThread : vendorThreads) {
            vendorThread.start();
        }

        // Wait for all threads to finish
        for (Thread vendorThread : vendorThreads) {
            try {
                vendorThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Save the tickets to the JSON file after all threads have finished
        pool.saveTickets();  // This will persist the tickets at the end of the process

        System.out.println("Final count of available tickets in the pool: " + pool.countAvailableTickets());
    }
}
