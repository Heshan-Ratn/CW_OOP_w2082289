package com.hkrw2082289.ticketing_system.controller;

import com.hkrw2082289.ticketing_system.service.CustomerService;
import com.hkrw2082289.ticketing_system.utils.ResponseFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @PostMapping("/signup")
    public ResponseEntity<String> signUpCustomer(@RequestBody Map<String, String> payload) {
        String customerId = payload.get("customerId");
        String password = payload.get("password");
        ResponseFinder message = customerService.signUpCustomer(customerId, password);
        // Use the success field to determine the HTTP status
        if (!message.isSuccess()) {
            // Return error messages with a 400 Bad Request status
            return ResponseEntity.badRequest().body(message.getMessage());
        }
        // Return success message with a 200 OK status
        return ResponseEntity.ok(message.getMessage());
    }

//    @PostMapping("/signin")
//    public ResponseEntity<Map<String, Object>> signInCustomer(@RequestBody Map<String, String> payload) {
//        String customerId = payload.get("customerId");
//        String password = payload.get("password");
//        Map<String, Object> response = customerService.signInCustomer(customerId, password);
//        return ResponseEntity.ok(response);
//    }

    @PostMapping("/signin")
    public ResponseEntity<ResponseFinder> signInCustomer(@RequestBody Map<String, String> payload) {
        String customerId = payload.get("customerId");
        String password = payload.get("password");
        ResponseFinder response = customerService.signInCustomer(customerId, password);
        if (response.isSuccess()) {
            // Successful login
            return ResponseEntity.ok(response); // 200 OK
        } else {
            // Failed login
            return ResponseEntity.badRequest().body(response); // 400 Bad Request
        }
    }

    @PostMapping("/{customerId}/start-thread")
    public ResponseEntity<String> startCustomerThread(
            @PathVariable String customerId,
            @RequestBody Map<String, Object> payload) {
        String message = customerService.startCustomerThread(customerId,payload);
        return ResponseEntity.ok(message);
    }

    @PostMapping("/{customerId}/stop-thread")
    public ResponseEntity<String> stopCustomerThread(@PathVariable String customerId) {
        String message = customerService.stopAllThreadsOfCustomer(customerId);
        return ResponseEntity.ok(message);
    }
}

