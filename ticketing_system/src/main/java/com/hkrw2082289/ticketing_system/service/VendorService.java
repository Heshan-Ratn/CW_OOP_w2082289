package com.hkrw2082289.ticketing_system.service;

import com.hkrw2082289.ticketing_system.model.TicketEntity;
import com.hkrw2082289.ticketing_system.model.Vendor;
import com.hkrw2082289.ticketing_system.repository.VendorRepository;
import com.hkrw2082289.ticketing_system.utils.ResponseFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

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

    // Lock instance for controlling access
    private static final ReentrantLock vendorLock = new ReentrantLock();

    public VendorService(TicketService ticketService, TicketPoolService ticketPoolService) {
        this.ticketService = ticketService;
        this.ticketPoolService = ticketPoolService;
    }

    public ResponseFinder signUpVendor(String vendorId, String password) {
        vendorLock.lock();  // Acquire the lock to synchronize this block
        try {
            if (!vendorId.matches(VENDOR_ID_REGEX)) {
                return new ResponseFinder(false, "Error: Vendor ID must be in the format of 4 letters followed by 3 digits.");
            }
            if (password.length() < 8 || password.length() > 12) {
                return new ResponseFinder(false,"Error: Password must be between 8 and 12 characters.");
            }
            if (vendorRepository.existsByVendorId(vendorId)) {
                return new ResponseFinder(false,"Error: Vendor ID already exists.");
            }
            Vendor vendor = new Vendor();
            vendor.setVendorId(vendorId);
            vendor.setPassword(password);
            vendorRepository.save(vendor);
            return new ResponseFinder(true,String.format("Sign-up was successful, Here is your Vendor ID: '%s'.", vendorId));
        } finally {
            vendorLock.unlock();  // Ensure the lock is released after execution
        }
    }

//    public Map<String, Object> signInVendor(String vendorId, String password) {
//        vendorLock.lock();  // Acquire the lock to synchronize this block
//        try {
//            Map<String, Object> response = new HashMap<>();
//            Vendor vendor = vendorRepository.findByVendorIdAndPassword(vendorId, password);
//            if (vendor != null) {
//                response.put("message", "Sign-in successful.");
//                response.put("vendor", vendor);
//            } else {
//                response.put("message", "Error: Invalid vendor ID or password.");
//                response.put("vendor", null);
//            }
//            return response;
//        } finally {
//            vendorLock.unlock();  // Ensure the lock is released after execution
//        }
//    }

    public ResponseFinder signInVendor(String vendorId, String password) {
        vendorLock.lock();
        try {
            Vendor vendor = vendorRepository.findByVendorIdAndPassword(vendorId, password);
            if (vendor != null) {
                return new ResponseFinder(true, String.format("Success: Sign-in successful, VendorID found: '%s'.",vendor.getVendorId()), vendor);
            } else {
                return new ResponseFinder(false, "Error: Invalid vendor ID or password.", null);
            }
        } finally {
            vendorLock.unlock();
        }
    }


    // Start a new thread for the vendor
    public String startVendorThread(String vendorId, Map<String, Object> payload) {
        Optional<Vendor> optionalVendor = vendorRepository.findById(vendorId);

        if (optionalVendor.isEmpty()) {
            return "Error: Vendor ID " + vendorId + " does not exist in the database.";
        }

        // Extract payload values
        String eventName = (String) payload.get("event_Name");
        double price = (Double) payload.get("price");
        String timeDuration = (String) payload.get("time_Duration");
        String date = (String) payload.get("date");
        int batchSize = (Integer) payload.get("batch_Size");

        List<TicketEntity> ticketBatch = ticketService.createTickets(vendorId, eventName, price, timeDuration, date, batchSize);
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

        if (isAdminStopAllRelease()) {
            return String.format("System has been stopped by Admin, Sorry, your ticket release request for '%s' has been denied", eventName);
        } else {
            return String.format("Thread started for vendor ID: %s with event '%s' and ticket batch size %d.", vendorId, eventName, batchSize);
        }
    }

    // Stop all threads for a specific vendor
    public String stopAllThreadsOfVendor(String vendorId) {
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

    // Method to check if global stop is enabled
    public  boolean isAdminStopAllRelease() {
        vendorLock.lock();  // Acquire the lock to synchronize this block
        try {
            return Vendor.isAdminStopAllRelease(); // Accessing the method from Vendor class
        } finally {
            vendorLock.unlock();  // Ensure the lock is released after execution
        }
    }

    // Method to enable the global stop flag
    public static void enableStopAllRelease() {
        vendorLock.lock();  // Acquire the lock to synchronize this block
        try {
            Vendor.enableStopAllRelease(); // Accessing the method from Vendor class
        } finally {
            vendorLock.unlock();  // Ensure the lock is released after execution
        }
    }

    // Method to disable the global stop flag
    public static void disableStopAllRelease() {
        vendorLock.lock();  // Acquire the lock to synchronize this block
        try {
            Vendor.disableStopAllRelease(); // Accessing the method from Vendor class
        } finally {
            vendorLock.unlock();  // Ensure the lock is released after execution
        }
    }
}


