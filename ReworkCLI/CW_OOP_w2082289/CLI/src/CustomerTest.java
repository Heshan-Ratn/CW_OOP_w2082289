////import java.util.concurrent.TimeUnit;
////
////public class CustomerTest {
////    public static void main(String[] args) {
////        // Step 1: Load Configuration
////        Configuration config = new Configuration();
////        config.loadConfiguration("config.json");
////
////        // Create TicketPool instance with a given capacity
////        TicketPool ticketPool = TicketPool.getInstance(100);
////
////        // Test sign up
////        System.out.println("Testing sign-up:");
////        boolean isSignedUp = Customer.signUp("customer1", "password123");
////        if (isSignedUp) {
////            System.out.println("Sign-up successful for customer1.");
////        } else {
////            System.out.println("Sign-up failed: Customer ID may already be taken.");
////        }
////
////        // Test sign in
////        System.out.println("\nTesting sign-in:");
////        Customer customer = Customer.signIn("customer1", "password123");
////        if (customer != null) {
////            System.out.println("Sign-in successful for customer1.");
////        } else {
////            System.out.println("Sign-in failed: Incorrect ID or password.");
////        }
////
////        if (customer != null) {
////            // Set TicketPool for customer and request tickets for an event
////            customer = new Customer("customer1", "password123", ticketPool);
////            customer.requestTickets("ConcertEvent", 5);
////
////            // Start ticket purchasing in a separate thread
////            System.out.println("\nStarting ticket purchasing...");
////
////            // Create and start a thread to run the customer ticket purchasing process
////            Thread customerThread = new Thread(customer);
////            customerThread.start();
////
////            // Wait for a short period and then stop purchasing
////            try {
////                TimeUnit.SECONDS.sleep(3);
////            } catch (InterruptedException e) {
////                Thread.currentThread().interrupt();
////            }
////
////            System.out.println("\nStopping ticket purchasing for customer1...");
////            customer.stopPurchasingTickets();
////
////            // Admin stops all purchasing threads (testing admin-level stop functionality)
////            try {
////                TimeUnit.SECONDS.sleep(1); // Ensure some time for previous stop to take effect
////            } catch (InterruptedException e) {
////                Thread.currentThread().interrupt();
////            }
////            System.out.println("\nAdmin stopping all ticket purchasing...");
////            Customer.stopAllPurchases();
////
////            // Wait for customer threads to finish
////            try {
////                customerThread.join();  // Wait for the customer thread to finish
////            } catch (InterruptedException e) {
////                Thread.currentThread().interrupt();
////            }
////
////            // Resume all purchases (admin-level)
////            System.out.println("\nAdmin resuming all ticket purchasing...");
////            Customer.resumeAllPurchases();
////
////            System.out.println("Customer test complete.");
////        }
////    }
////}
//
//
//
//import java.util.concurrent.TimeUnit;
//
//public class CustomerTest {
//    public static void main(String[] args) {
//        // Step 1: Load Configuration
//        Configuration config = new Configuration();
//        config.loadConfiguration("config.json");
//
//        // Create TicketPool instance with a given capacity
//        TicketPool ticketPool = TicketPool.getInstance(100);
//
//        // Test sign up for multiple customers
//        System.out.println("Testing sign-up:");
//        boolean isSignedUp = Customer.signUp("customer1", "password123");
//        if (isSignedUp) {
//            System.out.println("Sign-up successful for customer1.");
//        } else {
//            System.out.println("Sign-up failed: Customer ID may already be taken.");
//        }
//
//        isSignedUp = Customer.signUp("customer2", "password456");
//        if (isSignedUp) {
//            System.out.println("Sign-up successful for customer2.");
//        } else {
//            System.out.println("Sign-up failed: Customer ID may already be taken.");
//        }
//
//        // Test sign in for multiple customers
//        System.out.println("\nTesting sign-in:");
//        Customer customer1 = Customer.signIn("customer1", "password123");
//        Customer customer2 = Customer.signIn("customer2", "password456");
//
//        if (customer1 != null) {
//            System.out.println("Sign-in successful for customer1.");
//        } else {
//            System.out.println("Sign-in failed: Incorrect ID or password.");
//        }
//
//        if (customer2 != null) {
//            System.out.println("Sign-in successful for customer2.");
//        } else {
//            System.out.println("Sign-in failed: Incorrect ID or password.");
//        }
//
//        if (customer1 != null && customer2 != null) {
//            // Set TicketPool for customers and request tickets for an event
//            customer1 = new Customer("customer1", "password123", ticketPool);
//            customer2 = new Customer("customer2", "password456", ticketPool);
//
//            customer1.requestTickets("ConcertEvent", 5);
//            customer2.requestTickets("ConcertEvent", 3);
//
//            // Start ticket purchasing for both customers
//            System.out.println("\nStarting ticket purchasing...");
//
//            // Customer 1 runs two threads (simulating two separate ticket purchasing processes)
//            Thread customer1Thread1 = new Thread(customer1);
//            Thread customer1Thread2 = new Thread(customer1);
//            customer1Thread1.start();
//            customer1Thread2.start();
//
//            // Customer 2 runs one thread for ticket purchasing
//            Thread customer2Thread = new Thread(customer2);
//            customer2Thread.start();
//
//            // Wait for a short period and then stop purchasing
//            try {
//                TimeUnit.SECONDS.sleep(3);
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//            }
//
//            System.out.println("\nStopping ticket purchasing for customers...");
//            customer1.stopPurchasingTickets();
//            customer2.stopPurchasingTickets();
//
//            // Admin stops all purchasing threads (testing admin-level stop functionality)
//            try {
//                TimeUnit.SECONDS.sleep(1); // Ensure some time for previous stop to take effect
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//            }
//            System.out.println("\nAdmin stopping all ticket purchasing...");
//            Customer.stopAllPurchases();
//
//            // Wait for customer threads to finish
//            try {
//                customer1Thread1.join();
//                customer1Thread2.join();
//                customer2Thread.join();  // Wait for all customer threads to finish
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//            }
//
//            // Resume all purchases (admin-level)
//            System.out.println("\nAdmin resuming all ticket purchasing...");
//            Customer.resumeAllPurchases();
//
//            System.out.println("Customer test complete.");
//        }
//    }
//}
//


//
//import java.util.List;
//
//public class CustomerTest {
//
//    public static void main(String[] args) {
//        // Step 1: Load Configuration
//        Configuration config = new Configuration();
//        config.loadConfiguration("config.json");
//
//        // Step 2: Initialize shared TicketPool with a capacity of 100
//        TicketPool ticketPool = TicketPool.getInstance(100);
//
//        // Step 3: Create a list of customers
//        String[] customerIds = {"customer1", "customer2", "customer3", "customer4"};
//        String[] customerPasswords = {"password1", "password2", "password3", "password4"};
//
//        // Create Customer instances and their respective threads
//        Customer[] customerInstances = new Customer[customerIds.length];
//        Thread[] customerThreads = new Thread[customerInstances.length];
//        int threadIndex = 0;
//
//        for (int i = 0; i < customerIds.length; i++) {
//            // Sign up customers
//            boolean isSignedUp = Customer.signUp(customerIds[i], customerPasswords[i]);
//            if (isSignedUp) {
//                System.out.println("Sign-up successful for " + customerIds[i]);
//            } else {
//                System.out.println("Sign-up failed for " + customerIds[i]);
//            }
//
//            // Sign in customers
//            Customer customer = Customer.signIn(customerIds[i], customerPasswords[i]);
//            if (customer != null) {
//                System.out.println("Sign-in successful for " + customerIds[i]);
//            } else {
//                System.out.println("Sign-in failed for " + customerIds[i]);
//                continue;
//            }
//
//            // Request tickets for different events
//            customer.requestTickets("ConcertEvent " + customerIds[i], 5);
//            customer.requestTickets("TheaterEvent " + customerIds[i], 3);
//
//            // Create and start the customer thread
//            customerInstances[threadIndex] = customer;
//            customerThreads[threadIndex] = new Thread(customer);
//            customerThreads[threadIndex].start();
//            threadIndex++;
//        }
//
//        // Allow some time for initial ticket purchasing activity
//        try {
//            Thread.sleep(2000);  // Wait to simulate customer ticket purchasing
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        // Step 4: Admin stops all customers from purchasing tickets
//        System.out.println("\nAdmin is stopping all customer purchases...");
//        Customer.stopAllPurchases();
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
//        // Step 5: Admin resumes customer ticket purchasing
//        System.out.println("\nAdmin resumes all customer purchases. Restarting customer threads...");
//        Customer.resumeAllPurchases();
//
//        // Restart customer threads to resume ticket purchasing
//        Thread[] resumedCustomerThreads = new Thread[customerInstances.length];
//        for (int i = 0; i < customerInstances.length; i++) {
//            resumedCustomerThreads[i] = new Thread(customerInstances[i]);
//            resumedCustomerThreads[i].start();
//        }
//
//        // Allow some time for resumed activity
//        try {
//            Thread.sleep(2000);  // Wait to simulate resumed activity
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        // Step 6: Stop only customer1's ticket purchasing
//        System.out.println("\nStopping ticket purchasing for customer1...");
//        for (Customer customer : customerInstances) {
//            if ("customer1".equals(customer.getCustomerId())) {
//                customer.stopPurchasingTickets();
//            }
//        }
//
//        ticketPool.countAvailableTicketsByEvent();
//    }
//}


public class CustomerTest {
    public static void main(String[] args) {
        // Step 1: Initialize shared TicketPool
        TicketPool ticketPool = TicketPool.getInstance(100);  // Ensure this is initialized

        // Step 2: Create Customer instances with ticketPool passed as a parameter
        Customer customer1 = new Customer("customer1", "password1", ticketPool);
        Customer customer2 = new Customer("customer2", "password2", ticketPool);
        Customer customer3 = new Customer("customer3", "password3", ticketPool);
        Customer customer4 = new Customer("customer4", "password4", ticketPool);

//        // Step 3: Sign-up and sign-in logic for customers
//        System.out.println("Signing up customers...");
//        customer1.signUp();
//        customer2.signUp();
//        customer3.signUp();
//        customer4.signUp();
//
//        // Sign-in customers
//        System.out.println("Signing in customers...");
//        customer1.signIn();
//        customer2.signIn();
//        customer3.signIn();
//        customer4.signIn();

        // Step 4: Request tickets for events
        System.out.println("Customers requesting tickets...");
        customer1.requestTickets("ConcertEvent", 5);
        customer2.requestTickets("ConcertEvent", 3);
        customer3.requestTickets("TheaterEvent", 2);
        customer4.requestTickets("TheaterEvent", 4);

        // Step 5: Create threads to simulate customer actions (multiple threads per customer)
        Thread customerThread1 = new Thread(customer1);
        Thread customerThread2 = new Thread(customer2);
        Thread customerThread3 = new Thread(customer3);
        Thread customerThread4 = new Thread(customer4);

        // Running multiple threads per customer (simulating multiple actions)
        Thread customerThread1a = new Thread(customer1);
        Thread customerThread2a = new Thread(customer2);
        Thread customerThread3a = new Thread(customer3);
        Thread customerThread4a = new Thread(customer4);

        // Start threads
        System.out.println("Starting customer threads...");
        customerThread1.start();
        customerThread2.start();
        customerThread2a.start();
        customerThread3.start();

        try {
            Thread.sleep(5000);  // Simulate wait time for ticket booking
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        customerThread1a.start();
        customerThread4.start();
        customerThread3a.start();
        customerThread4a.start();

        // Step 6: Add appropriate wait time to allow customers to process purchases
        try {
            Thread.sleep(10000);  // Simulate wait time for ticket booking
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Step 7: Admin stops all customer purchases
        System.out.println("\nAdmin is stopping all customer purchases...");
        Customer.stopAllPurchases();  // Admin stop functionality

        // Step 8: Wait for threads to stop
        try {
            Thread.sleep(2000);  // Allow time for all threads to stop
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Show available tickets by event after stopping
        ticketPool.countAvailableTicketsByEvent();

//        // Step 9: Admin resumes all customer purchases
//        System.out.println("\nAdmin is resuming all customer purchases...");
//        Customer.resumeAllPurchases();  // Admin resume functionality
//
//        // Restart customer threads after resume
//        System.out.println("Restarting customer threads...");
//        Thread[] resumedCustomerThreads = new Thread[8];
//        resumedCustomerThreads[0] = new Thread(customer1);
//        resumedCustomerThreads[1] = new Thread(customer2);
//        resumedCustomerThreads[2] = new Thread(customer3);
//        resumedCustomerThreads[3] = new Thread(customer4);
//        resumedCustomerThreads[4] = new Thread(customer1);
//        resumedCustomerThreads[5] = new Thread(customer2);
//        resumedCustomerThreads[6] = new Thread(customer3);
//        resumedCustomerThreads[7] = new Thread(customer4);
//
//        // Start the resumed threads
//        for (Thread thread : resumedCustomerThreads) {
//            thread.start();
//        }
//
//        // Allow time for resumed activity
//        try {
//            Thread.sleep(3000);  // Wait to simulate resumed activity
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        // Step 10: Stopping only a specific customer's purchases (e.g., customer1)
//        System.out.println("\nStopping ticket purchasing for customer1...");
//        customer1.stopPurchasingTickets();  // Only stop customer1
//
//        // Show available tickets by event after stopping customer1
//        ticketPool.countAvailableTicketsByEvent();
//
//        // Wait for customer1's thread to stop
//        try {
//            customerThread1.join();  // Wait for the customer1 thread to finish
//            customerThread1a.join();  // Wait for the second thread for customer1 to finish
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        // Final status of the ticket pool
//        System.out.println("\nFinal available tickets by event:");
//        ticketPool.countAvailableTicketsByEvent();
//
//        System.out.println("Customer test complete.");
        System.out.println(ticketPool.countBookedTicketsByCustomerId("customer1"));
        System.out.println(ticketPool.countBookedTicketsByCustomerId("customer2"));
        System.out.println(ticketPool.countBookedTicketsByCustomerId("customer3"));
        System.out.println(ticketPool.countBookedTicketsByCustomerId("customer4"));
    }
}

