package com.hkrw2082289.ticketing_system.utils;

import com.hkrw2082289.ticketing_system.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class CustomerSimulation {

    @Autowired
    private CustomerService customerService;

    private static final Logger logger = LoggerFactory.getLogger(CustomerSimulation.class);

    // Simulate the process of signing up customers and starting their threads
    public void simulateCustomerThreads(int numberOfCustomers) {
        logger.info("Starting customer simulation with {} customers.", numberOfCustomers);

        List<Thread> customerThreads = new ArrayList<>();
        for (int i = 1; i <= numberOfCustomers; i++) {
            String customerId = "cust" + String.format("%03d", i);
            String password = "password" + i;

            ResponseFinder signUpResult = customerService.signUpCustomer(customerId, password);
            logger.debug("Sign-up result for {}: {}", customerId, signUpResult.getMessage());
        }

        for (int i = 1; i <= numberOfCustomers; i++) {
            String customerId = "cust" + String.format("%03d", i);
            Map<String, Object> payload = generatePayload(i,customerId);

            Thread customerThread = new Thread(() -> {
                ResponseFinder result = customerService.startCustomerThread(customerId, payload);
                logger.debug("Thread result for {}: {}", customerId, result.getMessage());
            });

            customerThreads.add(customerThread);
            customerThread.start();
        }

        logger.info("Simulation started. Customer threads are running concurrently.");
    }

    private Map<String, Object> generatePayload(int vendorIndex, String customerId) {
        String vendorId = generateVendorId(vendorIndex);
        Map<String, Object> payload = new HashMap<>();
        payload.put("eventName", "Event_" + vendorId);
        payload.put("ticketToBook", 10);  // All customers will book 10 tickets
        logger.trace("Generated payload for {}: {}", customerId, payload);
        return payload;
    }


    private String generateVendorId(int index) {
        // Format the vendor ID with a prefix "VEND" and a 3-digit zero-padded index
        return "VEND" + String.format("%03d", index);
    }
}
