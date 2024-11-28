package com.hkrw2082289.ticketing_system.controller;

import com.hkrw2082289.ticketing_system.service.CustomerService;
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
        String message = customerService.signUpCustomer(customerId, password);
        return ResponseEntity.ok(message);
    }

    @PostMapping("/signin")
    public ResponseEntity<Map<String, Object>> signInCustomer(@RequestBody Map<String, String> payload) {
        String customerId = payload.get("customerId");
        String password = payload.get("password");
        Map<String, Object> response = customerService.signInCustomer(customerId, password);
        return ResponseEntity.ok(response);
    }
}

