package com.hkrw2082289.ticketing_system.controller;

import com.hkrw2082289.ticketing_system.service.TicketPoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/ticket-pool")
public class TicketPoolController {

    private final TicketPoolService ticketPool;

    @Autowired
    public TicketPoolController(TicketPoolService ticketPool) {
        this.ticketPool = ticketPool;
    }

    @GetMapping("/available-tickets/vendor/{vendorId}")
    public ResponseEntity<?> viewAvailableTicketCountsByVendor(@PathVariable String vendorId) {
        return ResponseEntity.ok(ticketPool.viewAvailableTicketCountsByVendor(vendorId));
    }

    @GetMapping("/available-tickets/event")
    public ResponseEntity<?> countAvailableTicketsByEvent() {
        return ResponseEntity.ok(ticketPool.countAvailableTicketsByEvent());
    }

    @GetMapping("/booked-tickets/event")
    public ResponseEntity<?> countBookedTicketsByEvent() {
        return ResponseEntity.ok(ticketPool.countBookedTicketsByEvent());
    }

    @GetMapping("/booked-tickets/customer/{customerId}")
    public ResponseEntity<?> countBookedTicketsByCustomerId(@PathVariable String customerId) {
        return ResponseEntity.ok(ticketPool.countBookedTicketsByCustomerId(customerId));
    }
}

