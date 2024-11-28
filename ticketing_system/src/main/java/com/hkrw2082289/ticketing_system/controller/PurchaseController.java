package com.hkrw2082289.ticketing_system.controller;
import com.hkrw2082289.ticketing_system.helper.PurchaseRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/purchases")
public class PurchaseController {

    @PostMapping("/create")
    public ResponseEntity<PurchaseRequest> createPurchaseRequest(@RequestBody Map<String, Object> payload) {
        String customerId = (String) payload.get("customer_id");
        int ticketToBook = (int) payload.get("ticketToBook");
        String eventName = (String) payload.get("event_Name");

        // Create a PurchaseRequest object
        PurchaseRequest purchaseRequest = new PurchaseRequest(customerId, ticketToBook, eventName);

        // Return the object as a response
        return ResponseEntity.ok(purchaseRequest);
    }
}
