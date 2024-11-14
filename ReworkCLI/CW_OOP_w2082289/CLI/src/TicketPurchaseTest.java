public class TicketPurchaseTest {
    public static void main(String[] args) {
        int maxCapacity = 10;  // Set max capacity of the pool
        TicketPool pool = TicketPool.getInstance(maxCapacity);

        // Add some tickets to the pool for testing
        for (int i = 0; i < 8; i++) {
            Ticket ticket = new Ticket(
                    "T" + i,
                    "Concert",
                    50.0 + i,
                    "2 hours",
                    "2023-12-25",
                    "Vendor1",
                    "Available",
                    "Not Set"
            );
            pool.addTicket(ticket);
        }

        // Define the number of customer threads trying to purchase tickets
        int numCustomers = 5;

        // Creating threads for each customer
        Thread[] customerThreads = new Thread[numCustomers];
        for (int i = 0; i < numCustomers; i++) {
            final String customerId = "Customer" + (i + 1);  // Assign a unique customer ID
            customerThreads[i] = new Thread(() -> {
                // Each customer attempts to purchase 2 tickets for the "Concert"
                int numTicketsToBuy = 2;  // For example, each customer wants to buy 2 tickets
                for (int j = 0; j < numTicketsToBuy; j++) {
                    boolean success = pool.removeTicket("Concert", customerId);
                    System.out.println(customerId + " attempted to book ticket " + (j + 1) + ": " + (success ? "Success" : "Failed"));
                }
            });
        }

        // Start all customer threads
        for (Thread customerThread : customerThreads) {
            customerThread.start();
        }

        // Wait for all threads to finish
        for (Thread customerThread : customerThreads) {
            try {
                customerThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Final count of available tickets in the pool after all bookings
        System.out.println("Final count of available tickets in the pool: " + pool.countAvailableTickets());
    }
}
