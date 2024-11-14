//import java.util.ArrayList;
//import java.util.List;
//
//public class VendorTest {
//    public static void main(String[] args) {
//        Configuration config =new Configuration();
//        config.loadConfiguration("config.json");
//
//        // Create a shared TicketPool instance
//        TicketPool ticketPool = TicketPool.getInstance(10);
//
//        // Create sample tickets for each vendor
//        List<Ticket> vendor1Tickets = createSampleTickets("Vendor1");
//        List<Ticket> vendor2Tickets = createSampleTickets("Vendor2");
//
//        // Create Vendor instances
//        Vendor vendor1 = new Vendor("Vendor1", "password1", ticketPool, vendor1Tickets);
//        Vendor vendor2 = new Vendor("Vendor2", "password2", ticketPool, vendor2Tickets);
//
//        // Create threads for each vendor
//        Thread vendor1Thread = new Thread(vendor1);
//        Thread vendor2Thread = new Thread(vendor2);
//
//        // Start vendor threads
//        vendor1Thread.start();
//        vendor2Thread.start();
//
//        // Allow vendors to release tickets for a short period
//        try {
////            Thread.sleep(4050);
////            System.out.println("Admin stops all releases");
////            Vendor.stopAllReleases(); // Stop all vendors
//            Thread.sleep(4050); // Sleep for at least the release rate
//
//            // Admin stops all releases by interrupting the threads
//            System.out.println("Admin stops all releases");
//            vendor1Thread.interrupt();
//            vendor2Thread.interrupt();
//
//            Thread.sleep(2000);
//            System.out.println("Admin resumes all releases");
//            Vendor.resumeAllReleases(); // Resume all vendors
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//
//    // Sample method to create tickets
//    private static List<Ticket> createSampleTickets(String vendorId) {
//        List<Ticket> tickets = new ArrayList<>();
//        for (int i = 1; i <= 5; i++) {
//            tickets.add(new Ticket(
//                    vendorId + "-Ticket" + i,
//                    "Event" + i,
//                    50.0 + i,
//                    "2 hours",
//                    "2024-12-0" + i,
//                    vendorId,
//                    "Available",
//                    "Not Set"
//            ));
//        }
//        return tickets;
//    }
//}







//import java.util.ArrayList;
//import java.util.List;
//
//public class VendorTest {
//    public static void main(String[] args) {
//        Configuration config = new Configuration();
//        config.loadConfiguration("config.json");
//
//        // Create a shared TicketPool instance with capacity 100
//        TicketPool ticketPool = TicketPool.getInstance(100);
//
//        // Create sample tickets for each vendor using the new createTicketsForVendor method
//        List<Ticket> vendor1Tickets = createTicketsForVendor("Vendor1", 5, "Concert", 50.0, "2 hours", "2024-12-01");
//        List<Ticket> vendor2Tickets = createTicketsForVendor("Vendor2", 5, "Sports Event", 60.0, "3 hours", "2024-12-02");
//
//        // Create Vendor instances
//        Vendor vendor1 = new Vendor("Vendor1", "password1", ticketPool, vendor1Tickets);
//        Vendor vendor2 = new Vendor("Vendor2", "password2", ticketPool, vendor2Tickets);
//
//        // Create threads for each vendor
//        Thread vendor1Thread = new Thread(vendor1);
//        Thread vendor2Thread = new Thread(vendor2);
//
//        // Create multiple threads for each vendor to release tickets
//        int numberOfThreads = 5; // Number of threads to create per vendor
//        Thread[] vendor1Threads = new Thread[numberOfThreads];
//        Thread[] vendor2Threads = new Thread[numberOfThreads];
//
//        // Start vendor threads
//        for (int i = 0; i < numberOfThreads; i++) {
//            vendor1Threads[i] = new Thread(vendor1);
//            vendor2Threads[i] = new Thread(vendor2);
//            vendor1Threads[i].start();
//            vendor2Threads[i].start();
//        }
//
//        // Allow vendors to release tickets for a short period
//        try {
//            System.out.println("Vendors starting to release tickets...");
//            Thread.sleep(2000); // Give vendors time to release some tickets
//
//            // Admin stops all releases
//            System.out.println("Admin stops all releases");
//            Vendor.stopAllReleases(); // Stop all vendors
//            Thread.sleep(2000); // Let vendors react to the stop signal
//            checkReleaseStatus(vendor1, vendor2);
//
//            // Admin resumes all releases
//            System.out.println("Admin resumes all releases");
//            Vendor.resumeAllReleases(); // Resume all vendors
//            Thread.sleep(2000); // Let vendors resume releasing tickets
//            checkReleaseStatus(vendor1, vendor2);
//
//            // Stop vendor1's ticket release
//            System.out.println("Vendor1 stops its own releases");
//            vendor1.stopReleasingTickets(); // Vendor1 stops releasing tickets
//            Thread.sleep(2000); // Let vendor1 stop releasing
//            checkReleaseStatus(vendor1, vendor2);
//
//            // Resume vendor1's ticket release
//            System.out.println("Vendor1 resumes its own releases");
//            vendor1.resumeReleasingTickets(); // Vendor1 resumes releasing tickets
//            Thread.sleep(2000); // Let vendor1 resume releasing
//            checkReleaseStatus(vendor1, vendor2);
//
//            // Interrupt vendor2's threads (to simulate failure or manual interruption)
//            System.out.println("Admin interrupts Vendor2");
//            for (Thread t : vendor2Threads) {
//                t.interrupt();
//            }
//            Thread.sleep(2000); // Allow some time for the interruption to take effect
//
//            // Verify final status
//            checkReleaseStatus(vendor1, vendor2);
//
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//
//    // Method to create tickets for a specific vendor using the provided method
//    private static List<Ticket> createTicketsForVendor(String vendorId, int batchSize, String eventName, double price, String timeDuration, String date) {
//        return Ticket.createTicketsForVendor(vendorId, batchSize, eventName, price, timeDuration, date);
//    }
//
//    // Helper method to print current release status of tickets
//    private static void checkReleaseStatus(Vendor vendor1, Vendor vendor2) {
//        System.out.println("Checking release status...");
//        System.out.println("Vendor1 releasing tickets: " + vendor1.isReleasingTickets());
//        System.out.println("Vendor2 releasing tickets: " + vendor2.isReleasingTickets());
//        System.out.println("Admin stop all releases: " + Vendor.isAdminStopAll());
//    }
//}


//import java.util.List;
//
//public class VendorTest {
//    public static void main(String[] args) {
//        // Step 1: Load Configuration
//        Configuration config = new Configuration();
//        config.loadConfiguration("config.json");
//
//        // Step 2: Initialize shared TicketPool with a capacity of 100
//        TicketPool ticketPool = TicketPool.getInstance(100);
//
//        // Step 3: Create ticket batches for each vendor and start two threads per vendor
//        int batchSize = 10; // Quantity of tickets per batch
//        String[] vendorIds = {"vendor1", "vendor2", "vendor3", "vendor4"};
//
//        // List to hold all threads
//        Thread[] vendorThreads = new Thread[vendorIds.length * 2];
//
//        int threadIndex = 0; // Index to manage threads in the array
//        for (String vendorId : vendorIds) {
//            // Create two batches of tickets for each vendor
//            List<Ticket> batch1 = Ticket.createTicketsForVendor(vendorId, batchSize, "Event1 for " + vendorId, 50.0, "2 hours", "2024-12-01");
//            List<Ticket> batch2 = Ticket.createTicketsForVendor(vendorId, batchSize, "Event2 for " + vendorId, 60.0, "3 hours", "2024-12-02");
//
//            // Step 4: Create separate Vendor instances for each batch
//            Vendor vendorBatch1 = new Vendor(vendorId, "password" + vendorId, ticketPool, batch1);
//            Vendor vendorBatch2 = new Vendor(vendorId, "password" + vendorId, ticketPool, batch2);
//
//            // Step 5: Create threads for each vendor batch and add them to the array
//            vendorThreads[threadIndex++] = new Thread(vendorBatch1);
//            vendorThreads[threadIndex++] = new Thread(vendorBatch2);
//        }
//
//        // Step 6: Start all vendor threads simultaneously
//        for (Thread vendorThread : vendorThreads) {
//            vendorThread.start();
//        }
//
//        ticketPool.countAvailableTicketsByEvent();
//    }
//}


//import java.util.List;
//
//public class VendorTest {
//
//    public static void main(String[] args) {
//        // Step 1: Load Configuration
//        Configuration config = new Configuration();
//        config.loadConfiguration("config.json");
//
//        // Step 2: Initialize shared TicketPool with a capacity of 100
//        TicketPool ticketPool = TicketPool.getInstance(100);
//
//        // Step 3: Create ticket batches for each vendor
//        int batchSize = 10; // Quantity of tickets per batch
//        String[] vendorIds = {"vendor1", "vendor2", "vendor3", "vendor4"};
//
//        Vendor[] vendorInstances = new Vendor[vendorIds.length * 2];
//        Thread[] vendorThreads = new Thread[vendorInstances.length];
//        int threadIndex = 0;
//
//        for (String vendorId : vendorIds) {
//            // Create two batches of tickets for each vendor
//            List<Ticket> batch1 = Ticket.createTicketsForVendor(vendorId, batchSize, "Event1 for " + vendorId, 50.0, "2 hours", "2024-12-01");
//            List<Ticket> batch2 = Ticket.createTicketsForVendor(vendorId, batchSize, "Event2 for " + vendorId, 60.0, "3 hours", "2024-12-02");
//
//            // Initialize Vendor instances for each batch
//            Vendor vendorBatch1 = new Vendor(vendorId, "password" + vendorId, ticketPool, batch1);
//            Vendor vendorBatch2 = new Vendor(vendorId, "password" + vendorId, ticketPool, batch2);
//
//            // Store vendor instances and start threads
//            vendorInstances[threadIndex] = vendorBatch1;
//            vendorInstances[threadIndex + 1] = vendorBatch2;
//
//            vendorThreads[threadIndex] = new Thread(vendorBatch1);
//            vendorThreads[threadIndex + 1] = new Thread(vendorBatch2);
//
//            vendorThreads[threadIndex].start();
//            vendorThreads[threadIndex + 1].start();
//            threadIndex += 2;
//        }
//
//        // Allow time for tickets to release initially
//        try {
//            Thread.sleep(2000);  // Wait to simulate ticket release activity
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        // Step 4: Admin stop all vendors
//        System.out.println("\nAdmin is stopping all vendors...");
//        Vendor.stopAllReleases();
//
//        // Allow some time for all threads to stop
//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        ticketPool.countAvailableTicketsByEvent();
//
//        // Step 5: Admin resumes vendors
//        System.out.println("\nAdmin resumes all vendors. Restarting vendor threads...");
//        Vendor.resumeAllReleases();
//
//        // Restart vendor threads to resume ticket release
//        Thread[] resumedVendorThreads = new Thread[vendorInstances.length];
//        for (int i = 0; i < vendorInstances.length; i++) {
//            resumedVendorThreads[i] = new Thread(vendorInstances[i]);
//            resumedVendorThreads[i].start();
//        }
//
//        // Allow some time for ticket release
//        try {
//            Thread.sleep(2000);  // Wait to simulate resumed activity
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        // Step 6: Stop only vendor1's ticket release
//        System.out.println("\nStopping ticket release for vendor1...");
//        for (Vendor vendor : vendorInstances) {
//            if ("vendor1".equals(vendor.getVendorId())) {
//                vendor.stopReleasingTickets();
//            }
//        }
//
//        ticketPool.countAvailableTicketsByEvent();
//    }
//}
//



//import java.util.List;
//
//public class VendorTest {
//
//    public static void main(String[] args) {
//        // Step 1: Load Configuration
//        Configuration config = new Configuration();
//        config.loadConfiguration("config.json");
//
//        // Step 2: Initialize shared TicketPool with a capacity of 100
//        TicketPool ticketPool = TicketPool.getInstance(100);
//
////        // Step 3: Create ticket batches for each vendor manually
////        int batchSize = 10; // Quantity of tickets per batch
////
////        // Vendor1 setup
////        List<Ticket> vendor1Batch1 = Ticket.createTicketsForVendor("vendor1", batchSize, "Event1 for vendor1", 50.0, "2 hours", "2024-12-01");
////        List<Ticket> vendor1Batch2 = Ticket.createTicketsForVendor("vendor1", batchSize, "Event2 for vendor1", 60.0, "3 hours", "2024-12-02");
////        Vendor vendor1Instance1 = new Vendor("vendor1", "passwordvendor1", ticketPool, vendor1Batch1);
////        Vendor vendor1Instance2 = new Vendor("vendor1", "passwordvendor1", ticketPool, vendor1Batch2);
////        Thread vendor1Thread1 = new Thread(vendor1Instance1);
////        Thread vendor1Thread2 = new Thread(vendor1Instance2);
////        vendor1Thread1.start();
////        vendor1Thread2.start();
////
////        // Vendor2 setup
////        List<Ticket> vendor2Batch1 = Ticket.createTicketsForVendor("vendor2", batchSize, "Event1 for vendor2", 50.0, "2 hours", "2024-12-01");
////        List<Ticket> vendor2Batch2 = Ticket.createTicketsForVendor("vendor2", batchSize, "Event2 for vendor2", 60.0, "3 hours", "2024-12-02");
////        Vendor vendor2Instance1 = new Vendor("vendor2", "passwordvendor2", ticketPool, vendor2Batch1);
////        Vendor vendor2Instance2 = new Vendor("vendor2", "passwordvendor2", ticketPool, vendor2Batch2);
////        Thread vendor2Thread1 = new Thread(vendor2Instance1);
////        Thread vendor2Thread2 = new Thread(vendor2Instance2);
////        vendor2Thread1.start();
////        vendor2Thread2.start();
////
////        // Vendor3 setup
////        List<Ticket> vendor3Batch1 = Ticket.createTicketsForVendor("vendor3", batchSize, "Event1 for vendor3", 50.0, "2 hours", "2024-12-01");
////        List<Ticket> vendor3Batch2 = Ticket.createTicketsForVendor("vendor3", batchSize, "Event2 for vendor3", 60.0, "3 hours", "2024-12-02");
////        Vendor vendor3Instance1 = new Vendor("vendor3", "passwordvendor3", ticketPool, vendor3Batch1);
////        Vendor vendor3Instance2 = new Vendor("vendor3", "passwordvendor3", ticketPool, vendor3Batch2);
////        Thread vendor3Thread1 = new Thread(vendor3Instance1);
////        Thread vendor3Thread2 = new Thread(vendor3Instance2);
////        vendor3Thread1.start();
////        vendor3Thread2.start();
//
////        // Vendor4 setup
////        List<Ticket> vendor4Batch1 = Ticket.createTicketsForVendor("vendor4", batchSize, "Event1 for vendor4", 50.0, "2 hours", "2024-12-01");
////        List<Ticket> vendor4Batch2 = Ticket.createTicketsForVendor("vendor4", batchSize, "Event2 for vendor4", 60.0, "3 hours", "2024-12-02");
////        Vendor vendor4Instance1 = new Vendor("vendor4", "passwordvendor4", ticketPool, vendor4Batch1);
////        Vendor vendor4Instance2 = new Vendor("vendor4", "passwordvendor4", ticketPool, vendor4Batch2);
////        Thread vendor4Thread1 = new Thread(vendor4Instance1);
////        Thread vendor4Thread2 = new Thread(vendor4Instance2);
////        vendor4Thread1.start();
////        vendor4Thread2.start();
////
////        // Allow time for tickets to release initially
////        try {
////            Thread.sleep(2000);  // Wait to simulate ticket release activity
////        } catch (InterruptedException e) {
////            e.printStackTrace();
////        }
////
////        // Step 4: Admin stop all vendors
////        System.out.println("\nAdmin is stopping all vendors...");
////        Vendor.stopAllReleases();
////
////        // Allow some time for all threads to stop
////        try {
////            Thread.sleep(2000);
////        } catch (InterruptedException e) {
////            e.printStackTrace();
////        }
////
////        ticketPool.countAvailableTicketsByEvent();
////
////        // Step 5: Admin resumes vendors
////        System.out.println("\nAdmin resumes all vendors. Restarting vendor threads...");
////        Vendor.resumeAllReleases();
////
////        // Restart vendor threads to resume ticket release
////        Thread vendor1ResumedThread1 = new Thread(vendor1Instance1);
////        Thread vendor1ResumedThread2 = new Thread(vendor1Instance2);
////        vendor1ResumedThread1.start();
////        vendor1ResumedThread2.start();
////
////        Thread vendor2ResumedThread1 = new Thread(vendor2Instance1);
////        Thread vendor2ResumedThread2 = new Thread(vendor2Instance2);
////        vendor2ResumedThread1.start();
////        vendor2ResumedThread2.start();
////
////        Thread vendor3ResumedThread1 = new Thread(vendor3Instance1);
////        Thread vendor3ResumedThread2 = new Thread(vendor3Instance2);
////        vendor3ResumedThread1.start();
////        vendor3ResumedThread2.start();
////
////        Thread vendor4ResumedThread1 = new Thread(vendor4Instance1);
////        Thread vendor4ResumedThread2 = new Thread(vendor4Instance2);
////        vendor4ResumedThread1.start();
////        vendor4ResumedThread2.start();
////
////        // Allow some time for ticket release
////        try {
////            Thread.sleep(2000);  // Wait to simulate resumed activity
////        } catch (InterruptedException e) {
////            e.printStackTrace();
////        }
////
////        // Step 6: Stop only vendor1's ticket release
////        System.out.println("\nStopping ticket release for vendor1...");
////        vendor1Instance1.stopReleasingTickets();
////        vendor1Instance2.stopReleasingTickets();
//
////        ticketPool.countAvailableTicketsByEvent();
//
//            // Test sign-up functionality
//            testVendorSignUp();
//
//            // Test sign-in functionality
//            testVendorSignIn();
//        }

//
//    // Test sign-up functionality
//    private static void testVendorSignUp() {
//        String vendorId = "vendor123";
//        String password = "password123";
//
//        boolean signUpSuccess = Vendor.signUp();
//        if (signUpSuccess) {
//            System.out.println("Sign-up successful for vendor: " + vendorId);
//        } else {
//            System.out.println("Sign-up failed for vendor: " + vendorId + " (ID might already be taken)");
//        }
//    }
//
//    // Test sign-in functionality
//    private static void testVendorSignIn() {
//        String vendorId = "vendor123";
//        String password = "password123";
//
//        Vendor signedInVendor = Vendor.signIn();
//        if (signedInVendor != null) {
//            System.out.println("Sign-in successful for vendor: " + vendorId);
//        } else {
//            System.out.println("Sign-in failed for vendor: " + vendorId + " (Incorrect ID or password)");
//        }
//    }
//}

