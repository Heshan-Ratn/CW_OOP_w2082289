package com.hkrw2082289.ticketing_system.controller;
import com.hkrw2082289.ticketing_system.model.Customer;
import com.hkrw2082289.ticketing_system.model.Vendor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @PostMapping("/stop-purchases")
    public ResponseEntity<String> stopAllPurchases() {
        Customer.enableStopAllPurchases();
        Vendor.enableStopAllRelease();
        return ResponseEntity.ok("All customer and vendor threads have been stopped.");
    }

    @PostMapping("/resume-purchases")
    public ResponseEntity<String> resumeAllPurchases() {
        Customer.disableStopAllPurchases();
        Vendor.disableStopAllRelease();
        return ResponseEntity.ok("All customer and vendor threads have been resumed.");
    }
}
