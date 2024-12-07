package com.hkrw2082289.ticketing_system.service;

import com.hkrw2082289.ticketing_system.model.TicketEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketService {


    public List<TicketEntity> createTickets(String vendorId, String eventName, double price, String timeDuration, String date, int batchSize) {
        // Generate the list of tickets using the static method from TicketEntity
        List<TicketEntity> ticketEntities = TicketEntity.generateTicketBatch(vendorId, eventName, price, timeDuration, date, batchSize);
        return ticketEntities;
    }
}

