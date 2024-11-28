package com.hkrw2082289.ticketing_system.service;

import com.hkrw2082289.ticketing_system.model.TicketEntity;
import com.hkrw2082289.ticketing_system.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TicketService {

    private final TicketPoolService ticketPoolService;
    private final TicketRepository ticketRepository;

    @Autowired
    public TicketService(TicketPoolService ticketPoolService, TicketRepository ticketRepository) {
        this.ticketPoolService = ticketPoolService;
        this.ticketRepository = ticketRepository;
    }

    public List<TicketEntity> createTickets(String vendorId, String eventName, double price, String timeDuration, String date, int batchSize) {
        // Generate the list of tickets using the static method from TicketEntity
        List<TicketEntity> ticketEntities = TicketEntity.generateTicketBatch(vendorId, eventName, price, timeDuration, date, batchSize);

        // Add each ticket to the queue and save it to the DB
        for (TicketEntity ticketEntity : ticketEntities) {
            ticketPoolService.addTicket(ticketEntity);
        }

        return ticketEntities;
    }
}

