package com.hkrw2082289.ticketing_system.controller;

import com.hkrw2082289.ticketing_system.model.Configuration;
import com.hkrw2082289.ticketing_system.service.ConfigurationService;
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
        String message = configurationService.updateAdminCredentials(oldUser, oldPassword, newUser, newPassword);
        return ResponseEntity.ok(message);
    }

    @PutMapping("/update-ticket-settings")
    public ResponseEntity<String> updateTicketSettings(@RequestBody Map<String, Object> payload) {
        String adminUser = (String) payload.get("configAdminUser");
        String adminPassword = (String) payload.get("configAdminPassword");
        Double releaseRate = Double.valueOf(payload.get("ticketReleaseRate").toString());
        Double retrievalRate = Double.valueOf(payload.get("customerRetrievalRate").toString());
        Integer maxCapacity = (Integer) payload.get("maxTicketCapacity");
        String message = configurationService.updateTicketSettings(adminUser, adminPassword, releaseRate, retrievalRate, maxCapacity);
        return ResponseEntity.ok(message);
    }
}

