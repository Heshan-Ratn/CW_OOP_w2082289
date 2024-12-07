package com.hkrw2082289.ticketing_system.utils;

import com.hkrw2082289.ticketing_system.service.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;

@Component
public class VendorSimulation {

    @Autowired
    private VendorService vendorService;

    private static final Logger logger = LoggerFactory.getLogger(VendorSimulation.class);

    // Simulate the process of signing up vendors and starting their threads
    public void simulateVendorsAddingTickets(int numberOfVendors) {
        logger.info("Starting vendor simulation with {} vendors.", numberOfVendors);

        List<Thread> vendorThreads = new ArrayList<>();
        for (int i = 1; i <= numberOfVendors; i++) {
            String vendorId = "VEND" + String.format("%03d", i);
            String password = "password" + i;

            ResponseFinder signUpResult = vendorService.signUpVendor(vendorId, password);
            logger.debug("Sign-up result for {}: {}", vendorId, signUpResult.getMessage());
        }

        for (int i = 1; i <= numberOfVendors; i++) {
            String vendorId = "VEND" + String.format("%03d", i);
            Map<String, Object> payload = generatePayload(vendorId);

            Thread vendorThread = new Thread(() -> {
                ResponseFinder result = vendorService.startVendorThread(vendorId, payload);
                logger.debug("Thread result for {}: {}", vendorId, result.getMessage());
            });

            vendorThreads.add(vendorThread);
            vendorThread.start();
        }

        logger.info("Simulation started. Vendor threads are running concurrently.");
    }

    private Map<String, Object> generatePayload(String vendorId) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("event_Name", "Event_" + vendorId);
        payload.put("price", Math.random() * 100 + 20);
        payload.put("time_Duration", "2 hours");
        payload.put("date", "2024-12-01");
        payload.put("batch_Size", 10);
        logger.trace("Generated payload for {}: {}", vendorId, payload);
        return payload;
    }
}
