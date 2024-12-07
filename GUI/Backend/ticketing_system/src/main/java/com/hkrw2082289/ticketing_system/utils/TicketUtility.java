package com.hkrw2082289.ticketing_system.utils;


import com.hkrw2082289.ticketing_system.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TicketUtility {

    private final TicketRepository ticketRepository;

    @Autowired
    public TicketUtility(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    // Count available tickets
    public long countAvailableTickets() {
        return ticketRepository.findAll().stream()
                .filter(ticket -> "Available".equals(ticket.getTicketStatus()))
                .count();
    }

}

