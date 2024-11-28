package com.hkrw2082289.ticketing_system.controller;

import com.hkrw2082289.ticketing_system.model.TicketEntity;
import com.hkrw2082289.ticketing_system.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @PostMapping("/generate")
    public ResponseEntity<List<TicketEntity>> generateTickets(@RequestBody Map<String, Object> payload) {
        String vendorId = (String) payload.get("vendor_Id");
        String eventName = (String) payload.get("event_Name");
        double price = (double) payload.get("price");
        String timeDuration = (String) payload.get("time_Duration");
        String date = (String) payload.get("date");
        int batchSize = (int) payload.get("batch_Size");

        List<TicketEntity> tickets = ticketService.createTickets(vendorId, eventName, price, timeDuration, date, batchSize);
        return ResponseEntity.ok(tickets);
    }
}
