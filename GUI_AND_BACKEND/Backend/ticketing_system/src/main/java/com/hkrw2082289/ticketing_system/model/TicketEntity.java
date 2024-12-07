package com.hkrw2082289.ticketing_system.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



@Entity
@Table(name = "ticketpool")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment ID generation
    @Column(name = "ticket_id", nullable = false, updatable = false)
    private Long ticketId;

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
            tickets.add(ticket); // No need to generate ticketId, as it will be auto-incremented by the database
        }
        return tickets;
    }
}

