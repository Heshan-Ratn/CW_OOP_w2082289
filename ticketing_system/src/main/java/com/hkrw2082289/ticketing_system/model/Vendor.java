package com.hkrw2082289.ticketing_system.model;

import com.hkrw2082289.ticketing_system.service.TicketPoolService;
import jakarta.persistence.*;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Data
@Entity
@Table(name = "vendors")
public class Vendor implements Runnable{
    private static final Logger logger = LoggerFactory.getLogger(Vendor.class);

    @Id
    @Column(name = "vendor_id", nullable = false, unique = true, length = 7)
    private String vendorId;

    @Column(nullable = false, length = 12)
    private String password;

    @Transient
    private double ticketReleaseRate;

    @Transient
    private List<TicketEntity> ticketBatch;

    @Transient
    private TicketPoolService ticketPoolService;

    // Example: A field to track what the vendor thread does
    //private transient boolean active = true; // Not persisted in the database

    @Override
    public void run() {
        logger.info("Thread started for Vendor ID: {} with ticket batch size: {} (Thread ID: {})",
                vendorId, ticketBatch != null ? ticketBatch.size() : 0, Thread.currentThread().getId());

        if (ticketPoolService == null) {
            logger.error("TicketPoolService is not set. Cannot add tickets to the pool.");
            return;
        }
        try {
            for (TicketEntity ticket : ticketBatch) {
                if (Thread.currentThread().isInterrupted()) {
                    logger.info("Thread for Vendor ID: {} was interrupted. (Thread ID: {})", vendorId, Thread.currentThread().getId());
                    break;
                }
                logger.info("Processing ticket with ID: {} for Vendor: {}", ticket.getTicketId(), vendorId);
                // Add ticket to the ticket pool
                boolean added = ticketPoolService.addTicket(ticket);

                if (added) {
                    logger.info("Successfully added ticket ID: {} to the pool.", ticket.getTicketId());
                } else {
                    logger.warn("Failed to add ticket ID: {} to the pool.", ticket.getTicketId());
                }
                Thread.sleep((long) ticketReleaseRate); // Simulate ticket processing
            }
        } catch (InterruptedException e) {
            logger.error("Thread for Vendor {} was interrupted. (Thread ID: {})", vendorId, Thread.currentThread().getId());
            Thread.currentThread().interrupt();  // Preserve the interrupt status
        }

        logger.info("Thread completed for Vendor ID: {} (Thread ID: {})", vendorId, Thread.currentThread().getId());
    }
}
