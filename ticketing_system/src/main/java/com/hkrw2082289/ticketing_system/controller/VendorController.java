package com.hkrw2082289.ticketing_system.controller;

import com.hkrw2082289.ticketing_system.service.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/vendors")
public class VendorController {

    @Autowired
    private VendorService vendorService;

    @PostMapping("/signup")
    public ResponseEntity<String> signUpVendor(@RequestBody Map<String, String> payload) {
        String vendorId = payload.get("vendorId");
        String password = payload.get("password");
        String message = vendorService.signUpVendor(vendorId, password);
        return ResponseEntity.ok(message);
    }

    @PostMapping("/signin")
    public ResponseEntity<Map<String, Object>> signInVendor(@RequestBody Map<String, String> payload) {
        String vendorId = payload.get("vendorId");
        String password = payload.get("password");
        Map<String, Object> response = vendorService.signInVendor(vendorId, password);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{vendorId}/start-thread")
    public ResponseEntity<String> startVendorThread(
            @PathVariable String vendorId,
            @RequestBody Map<String, Object> payload) {
        String message = vendorService.startVendorThread(vendorId,payload);
        return ResponseEntity.ok(message);
    }

    @PostMapping("/{vendorId}/stop-thread")
    public ResponseEntity<String> stopVendorThread(@PathVariable String vendorId) {
        String message = vendorService.stopAllThreadsOfVendor(vendorId);
        return ResponseEntity.ok(message);
    }
}

