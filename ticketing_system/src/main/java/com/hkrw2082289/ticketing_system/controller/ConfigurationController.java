package com.hkrw2082289.ticketing_system.controller;

import com.hkrw2082289.ticketing_system.model.Configuration;
import com.hkrw2082289.ticketing_system.service.ConfigurationService;
import com.hkrw2082289.ticketing_system.utils.ResponseFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/configuration")
public class ConfigurationController {

    @Autowired
    private ConfigurationService configurationService;

    @GetMapping("/view-configuration")
    public ResponseEntity<Configuration> viewConfiguration() {
        return ResponseEntity.ok(configurationService.viewConfiguration());
    }

    @PutMapping("/update-admin-credentials")
    public ResponseEntity<String> updateAdminCredentials(@RequestBody Map<String, String> payload) {
        String oldUser = payload.get("oldConfigAdminUser");
        String oldPassword = payload.get("oldConfigAdminPassword");
        String newUser = payload.get("newConfigAdminUser");
        String newPassword = payload.get("newConfigAdminPassword");
        ResponseFinder message = configurationService.updateAdminCredentials(oldUser, oldPassword, newUser, newPassword);
        // Use the success field to determine the HTTP status
        if (!message.isSuccess()) {
            // Return error messages with a 400 Bad Request status
            return ResponseEntity.badRequest().body(message.getMessage());
        }
        // Return success message with a 200 OK status
        return ResponseEntity.ok(message.getMessage());
        
    }

    @PutMapping("/update-ticket-settings")
    public ResponseEntity<String> updateTicketSettings(@RequestBody Map<String, Object> payload) {
        String adminUser = (String) payload.get("configAdminUser");
        String adminPassword = (String) payload.get("configAdminPassword");
        Double releaseRate = Double.valueOf(payload.get("ticketReleaseRate").toString());
        Double retrievalRate = Double.valueOf(payload.get("customerRetrievalRate").toString());
        Integer maxCapacity = (Integer) payload.get("maxTicketCapacity");
        ResponseFinder message = configurationService.updateTicketSettings(adminUser, adminPassword, releaseRate, retrievalRate, maxCapacity);
        // Use the success field to determine the HTTP status
        if (!message.isSuccess()) {
            return ResponseEntity.badRequest().body(message.getMessage());
        }
        // Return the success message
        return ResponseEntity.ok(message.getMessage());
    }
}

