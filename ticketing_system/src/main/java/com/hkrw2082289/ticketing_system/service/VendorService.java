package com.hkrw2082289.ticketing_system.service;

import com.hkrw2082289.ticketing_system.model.TicketEntity;
import com.hkrw2082289.ticketing_system.model.Vendor;
import com.hkrw2082289.ticketing_system.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class VendorService {

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private ConfigurationService configurationService;

    private final TicketService ticketService;

    private static final String VENDOR_ID_REGEX = "^[a-zA-Z]{4}\\d{3}$";

    private final Map<String, List<Thread>> vendorThreads = new ConcurrentHashMap<>();

    private final TicketPoolService ticketPoolService;

    public VendorService(TicketService ticketService, TicketPoolService ticketPoolService) {
        this.ticketService = ticketService;
        this.ticketPoolService = ticketPoolService;
    }

    public String signUpVendor(String vendorId, String password) {
        if (!vendorId.matches(VENDOR_ID_REGEX)) {
            return "Error: Vendor ID must be in the format of 4 letters followed by 3 digits.";
        }

        if (password.length() < 8 || password.length() > 12) {
            return "Error: Password must be between 8 and 12 characters.";
        }

        if (vendorRepository.existsByVendorId(vendorId)) {
            return "Error: Vendor ID already exists.";
        }

        Vendor vendor = new Vendor();
        vendor.setVendorId(vendorId);
        vendor.setPassword(password);

        vendorRepository.save(vendor);

        return "Sign-up successful.";
    }

    public Map<String, Object> signInVendor(String vendorId, String password) {
        Map<String, Object> response = new HashMap<>();
        Vendor vendor = vendorRepository.findByVendorIdAndPassword(vendorId, password);

        if (vendor != null) {
            response.put("message", "Sign-in successful.");
            response.put("vendor", vendor);
        } else {
            response.put("message", "Error: Invalid vendor ID or password.");
            response.put("vendor", null);
        }

        return response;
    }

    // Start a new thread for the vendor
    public String startVendorThread(String vendorId, Map<String, Object> payload) {
        Optional<Vendor> optionalVendor = vendorRepository.findById(vendorId);

        if (!optionalVendor.isPresent()) {
            return "Error: Vendor ID " + vendorId + " does not exist in the database.";
        }

        // Extract payload values
        String eventName = (String) payload.get("event_Name");
        double price = (Double) payload.get("price");
        String timeDuration = (String) payload.get("time_Duration");
        String date = (String) payload.get("date");
        int batchSize = (Integer) payload.get("batch_Size");

        List<TicketEntity>ticketBatch = ticketService.createTickets(vendorId, eventName, price, timeDuration, date, batchSize);
        Vendor vendor = new Vendor();
        vendor.setVendorId(vendorId);  // Assume vendor ID is set here or fetched from DB
        double ticketReleaseRate = configurationService.viewConfiguration().getTicketReleaseRate();
        vendor.setTicketReleaseRate(ticketReleaseRate);
        vendor.setTicketBatch(ticketBatch);
        vendor.setTicketPoolService(ticketPoolService);

        Thread vendorThread = new Thread(vendor);

        // Add the thread to the list for this vendor
        vendorThreads.computeIfAbsent(vendorId, k -> new ArrayList<>()).add(vendorThread);

        // Start the thread
        vendorThread.start();

        return String.format("Thread started for vendor ID: %s with event '%s' and ticket batch size %d.", vendorId, eventName, batchSize);
    }

    // Stop all threads for a specific vendor
    public String stopAllVendorThreads(String vendorId) {
        List<Thread> threads = vendorThreads.get(vendorId);

        if (threads == null || threads.isEmpty()) {
            return "No active threads found for vendor ID: " + vendorId;
        }

        // Interrupt all threads for this vendor
        for (Thread thread : threads) {
            thread.interrupt();
        }

        // Remove all threads for this vendor from the map
        vendorThreads.remove(vendorId);

        return "All threads for vendor ID: " + vendorId + " have been interrupted.";
    }

//    // For testing: Retrieve all active threads
//    public Map<String, List<Thread>> getVendorThreads() {
//        return vendorThreads;
//    }
}


