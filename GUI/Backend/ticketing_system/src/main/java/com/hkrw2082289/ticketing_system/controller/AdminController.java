package com.hkrw2082289.ticketing_system.controller;
import com.hkrw2082289.ticketing_system.service.CustomerService;
import com.hkrw2082289.ticketing_system.service.VendorService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @PostMapping("/stop-all-activity")
    public ResponseEntity<String> stopAllActivity() {
        CustomerService.enableStopAllPurchases();
        VendorService.enableStopAllRelease();
        return ResponseEntity.ok("All customer and vendor threads have been stopped.");
    }

    @PostMapping("/resume-all-activity")
    public ResponseEntity<String> resumeAllActivity() {
        CustomerService.disableStopAllPurchases();
        VendorService.disableStopAllRelease();
        return ResponseEntity.ok("All customer and vendor threads have been resumed.");
    }
}
