package com.hkrw2082289.ticketing_system.service;

import com.hkrw2082289.ticketing_system.helper.PurchaseRequest;
import com.hkrw2082289.ticketing_system.model.Customer;
import com.hkrw2082289.ticketing_system.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    private final Map<String, List<Thread>> customerThreads = new ConcurrentHashMap<>();

    private final TicketPoolService ticketPoolService;

    private static final String CUSTOMER_ID_REGEX = "^[a-zA-Z]{4}\\d{3}$";

    @Autowired
    private ConfigurationService configurationService;

    public CustomerService(TicketPoolService ticketPoolService) {
        this.ticketPoolService = ticketPoolService;
    }

    public String signUpCustomer(String customerId, String password) {
        if (!customerId.matches(CUSTOMER_ID_REGEX)) {
            return "Error: Customer ID must be in the format of 4 letters followed by 3 digits.";
        }

        if (password.length() < 8 || password.length() > 12) {
            return "Error: Password must be between 8 and 12 characters.";
        }

        if (customerRepository.existsByCustomerId(customerId)) {
            return "Error: Customer ID already exists.";
        }

        Customer customer = new Customer();
        customer.setCustomerId(customerId);
        customer.setPassword(password);

        customerRepository.save(customer);
        return "Sign-up successful.";
    }

    public Map<String, Object> signInCustomer(String customerId, String password) {
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

    public String startCustomerThread(String customerId, Map<String, Object> payload) {
        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);

        if (!optionalCustomer.isPresent()) {
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

        return String.format("Thread started for CustomerID: %s with event '%s' and purchase request batch size %d. ", customerId, eventName,ticketToBook);
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
}
