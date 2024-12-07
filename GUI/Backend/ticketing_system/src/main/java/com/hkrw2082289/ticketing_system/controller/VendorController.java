package com.hkrw2082289.ticketing_system.controller;

import com.hkrw2082289.ticketing_system.service.VendorService;
import com.hkrw2082289.ticketing_system.utils.ResponseFinder;
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
        ResponseFinder message = vendorService.signUpVendor(vendorId, password);
        // Use the success field to determine the HTTP status
        if (!message.isSuccess()) {
            // Return error messages with a 400 Bad Request status
            return ResponseEntity.badRequest().body(message.getMessage());
        }
        // Return success message with a 200 OK status
        return ResponseEntity.ok(message.getMessage());
    }


    @PostMapping("/signin")
    public ResponseEntity<ResponseFinder> signInVendor(@RequestBody Map<String, String> payload) {
        String vendorId = payload.get("vendorId");
        String password = payload.get("password");
        ResponseFinder response = vendorService.signInVendor(vendorId, password);

        if (response.isSuccess()) {
            // Successful login
            return ResponseEntity.ok(response); // 200 OK
        } else {
            // Failed login
            return ResponseEntity.badRequest().body(response); // 400 Bad Request
        }
    }


    @PostMapping("/{vendorId}/start-thread")
    public ResponseEntity<String> startVendorThread(
            @PathVariable String vendorId,
            @RequestBody Map<String, Object> payload) {
        ResponseFinder message = vendorService.startVendorThread(vendorId,payload);
        if (message.isSuccess()) {
            // Successful login
            return ResponseEntity.ok(message.getMessage()); // 200 OK
        } else {
            // Failed login
            return ResponseEntity.badRequest().body(message.getMessage()); // 400 Bad Request
        }
    }

    @PostMapping("/{vendorId}/stop-thread")
    public ResponseEntity<String> stopVendorThread(@PathVariable String vendorId) {
        ResponseFinder message = vendorService.stopAllThreadsOfVendor(vendorId);
        if (message.isSuccess()) {
            // Successful login
            return ResponseEntity.ok(message.getMessage()); // 200 OK
        } else {
            // Failed login
            return ResponseEntity.badRequest().body(message.getMessage()); // 400 Bad Request
        }
    }
}

