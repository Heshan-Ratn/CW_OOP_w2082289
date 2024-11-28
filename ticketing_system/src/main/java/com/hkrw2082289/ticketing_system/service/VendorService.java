package com.hkrw2082289.ticketing_system.service;

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

    private static final String VENDOR_ID_REGEX = "^[a-zA-Z]{4}\\d{3}$";

    private final Map<String, List<Thread>> vendorThreads = new ConcurrentHashMap<>();

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

//    public Vendor findVendorById(String vendorId) {
//        return vendorRepository.findById(vendorId).orElse(null);
//    }

    // Start a new thread for the vendor
    public String startVendorThread(String vendorId) {
        Optional<Vendor> optionalVendor = vendorRepository.findById(vendorId);

        if (!optionalVendor.isPresent()) {
            return "Error: Vendor ID " + vendorId + " does not exist in the database.";
        }

        Vendor vendor = new Vendor();
        vendor.setVendorId(vendorId);  // Assume vendor ID is set here or fetched from DB

        Thread vendorThread = new Thread(vendor);

        // Add the thread to the list for this vendor
        vendorThreads.computeIfAbsent(vendorId, k -> new ArrayList<>()).add(vendorThread);

        // Start the thread
        vendorThread.start();

        return "Thread started for vendor ID: " + vendorId;
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

    // For testing: Retrieve all active threads
    public Map<String, List<Thread>> getVendorThreads() {
        return vendorThreads;
    }
}

