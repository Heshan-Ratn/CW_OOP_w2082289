package com.hkrw2082289.ticketing_system.service;

import com.hkrw2082289.ticketing_system.helper.PurchaseRequest;
import com.hkrw2082289.ticketing_system.model.Customer;
import com.hkrw2082289.ticketing_system.repository.CustomerRepository;
import com.hkrw2082289.ticketing_system.utils.ResponseFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    private final Map<String, List<Thread>> customerThreads = new ConcurrentHashMap<>();

    private final TicketPoolService ticketPoolService;

    private static final String CUSTOMER_ID_REGEX = "^[a-zA-Z]{4}\\d{3}$";

    @Autowired
    private ConfigurationService configurationService;

    // Lock instance for controlling access
    private static final ReentrantLock customerLock = new ReentrantLock();

    public CustomerService(TicketPoolService ticketPoolService) {
        this.ticketPoolService = ticketPoolService;
    }

    public ResponseFinder signUpCustomer(String customerId, String password) {
        customerLock.lock();
        try {
            if (!customerId.matches(CUSTOMER_ID_REGEX)) {
                return new ResponseFinder(false,"Error: Customer ID must be in the format of 4 letters followed by 3 digits.");
            }

            if (password.length() < 8 || password.length() > 12) {
                return new ResponseFinder(false,"Error: Password must be between 8 and 12 characters.");
            }

            if (customerRepository.existsByCustomerId(customerId)) {
                return new ResponseFinder(false,"Error: Customer ID already exists.");
            }
            Customer customer = new Customer();
            customer.setCustomerId(customerId);
            customer.setPassword(password);

            customerRepository.save(customer);
            return new ResponseFinder(true,String.format("Sign-up was successful, Here is your Vendor ID: '%s'.", customerId));
        }
        finally {
            customerLock.unlock();
        }
    }

    public Map<String, Object> signInCustomer(String customerId, String password) {
        customerLock.lock();
        try {
            Map<String, Object> response = new HashMap<>();
            //boolean isValid = customerRepository.existsByCustomerIdAndPassword(customerId, password);
            Customer customer = customerRepository.findByCustomerIdAndPassword(customerId, password);
            if (customer != null) {
                response.put("message", "Sign-in successful.");
                response.put("customerId", customerId);
            } else {
                response.put("message", "Error: Invalid customer ID or password.");
                response.put("customerId", null);
            }

            return response;
        }
        finally {
            customerLock.unlock();
        }
    }

    public String startCustomerThread(String customerId, Map<String, Object> payload) {
        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);

        if (optionalCustomer.isEmpty()) {
            return "Error: Vendor ID " + customerId + " does not exist in the database.";
        }

        String eventName = (String) payload.get("eventName");
        int ticketToBook = (int) payload.get("ticketToBook");

        PurchaseRequest purchaseRequest = new PurchaseRequest(customerId,ticketToBook,eventName);
        double customerRetrievalRate = configurationService.viewConfiguration().getCustomerRetrievalRate();
        Customer customer = new Customer();
        customer.setCustomerId(customerId);
        customer.setPurchaseRequest(purchaseRequest);
        customer.setCustomerRetrievalRate(customerRetrievalRate);
        customer.setTicketPoolService(ticketPoolService);

        Thread customerThread = new Thread(customer);

        customerThreads.computeIfAbsent(customerId, k -> new ArrayList<>()).add(customerThread);

        customerThread.start();

        if(isAdminStopAllPurchases()) {
            return String.format("System has been stopped by Admin, Sorry, your ticket purchase request for '%s' has been denied",eventName);
        }
        else{
            return String.format("Thread started for CustomerID: %s with event '%s' and purchase request batch size %d. ", customerId, eventName,ticketToBook);
        }
    }

    public String stopAllThreadsOfCustomer(String customerId){
        List<Thread> threads = customerThreads.get(customerId);

        if (threads == null || threads.isEmpty()) {
            return "No active threads found for vendor ID: " + customerId;
        }
        // Interrupt all threads for this customer
        for (Thread thread : threads) {
            thread.interrupt();
        }
        customerThreads.remove(customerId);
        return "All threads for customer ID: " + customerId + " have been interrupted.";
    }

    // Method to check if global stop is enabled
    public  boolean isAdminStopAllPurchases() {
        customerLock.lock();  // Acquire the lock to synchronize this block
        try {
            return Customer.isAdminStopAllPurchases(); // Accessing the method from Customer class
        } finally {
            customerLock.unlock();  // Ensure the lock is released after execution
        }
    }

    // Method to enable the global stop flag
    public static void enableStopAllPurchases() {
        customerLock.lock();  // Acquire the lock to synchronize this block
        try {
            Customer.enableStopAllPurchases(); // Accessing the method from Customer class
        } finally {
            customerLock.unlock();  // Ensure the lock is released after execution
        }
    }

    // Method to disable the global stop flag
    public static void disableStopAllPurchases() {
        customerLock.lock();  // Acquire the lock to synchronize this block
        try {
            Customer.disableStopAllPurchases(); // Accessing the method from Customer class
        } finally {
            customerLock.unlock();  // Ensure the lock is released after execution
        }
    }
}
