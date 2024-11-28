package com.hkrw2082289.ticketing_system.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;


@Entity
@Table(name = "ticketpool")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketEntity {
    @Id
    @Column(name = "ticket_id", length = 20)
    private String ticketId;

    @Column(name = "event_name", length = 50, nullable = false)
    private String eventName;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "time_duration", length = 50, nullable = false)
    private String timeDuration;

    @Column(name = "date", nullable = false)
    private Date date;

    @Column(name = "vendor_id", length = 7, nullable = false)
    private String vendorId;

    @Column(name = "ticket_status", length = 50, nullable = false)
    private String ticketStatus = "Available";

    @Column(name = "customer_id", length = 7)
    private String customerId = null;

    // Method to generate a ticket ID
    public void generateTicketId() {
        Random random = new Random();
        int randomDigits = random.nextInt(900) + 100; // Generate a 3-digit number
        String prefix = vendorId.substring(0, 4);     // Extract first 4 characters of vendorId
        this.ticketId = prefix + "T" + randomDigits;
    }

    // Static method to generate a batch of tickets
    public static List<TicketEntity> generateTicketBatch(String vendorId, String eventName, double price, String timeDuration, String date, int batchSize) {
        List<TicketEntity> tickets = new ArrayList<>();
        for (int i = 0; i < batchSize; i++) {
            TicketEntity ticket = new TicketEntity();
            ticket.setVendorId(vendorId);
            ticket.setEventName(eventName);
            ticket.setPrice(BigDecimal.valueOf(price));
            ticket.setTimeDuration(timeDuration);
            ticket.setDate(java.sql.Date.valueOf(date)); // Assuming date is in 'yyyy-MM-dd' format
            ticket.generateTicketId();  // Generate unique ticket ID
            tickets.add(ticket);
        }
        return tickets;
    }
}

