package com.hkrw2082289.ticketing_system.model;

import com.hkrw2082289.ticketing_system.service.TicketPoolService;
import jakarta.persistence.*;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Data
@Entity
@Table(name = "vendors")
public class Vendor implements Runnable{
    private static final Logger logger = LoggerFactory.getLogger(Vendor.class);
    // AtomicBoolean to control Ticket Release globally
    private static final AtomicBoolean adminStopAllRelease = new AtomicBoolean(false);

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
        int ticketaddedcount =0;
        try {
            for (TicketEntity ticket : ticketBatch) {
                // Check if global stop flag is enabled
                if (adminStopAllRelease.get()) {
                    logger.info("Global stop enabled. Vendor {} thread will terminate. (Thread ID: {})",
                            vendorId, Thread.currentThread().getId());
                    break;
                }
                if (Thread.currentThread().isInterrupted()) {
                    logger.info("Thread for Vendor ID: {} was interrupted. (Thread ID: {})", vendorId, Thread.currentThread().getId());
                    break;
                }
                logger.info("Processing ticket with event name: {} for Vendor: {}", ticket.getEventName(), vendorId);
                // Add ticket to the ticket pool
                boolean added = ticketPoolService.addTicket(ticket);

                if (added) {
                    ticketaddedcount++;
                    logger.info("Successfully added ticket ID: {} of event: {} to the pool. Ticket number in the batch: {}", ticket.getTicketId(),ticket.getEventName(),ticketaddedcount);
                } else {
                    logger.warn("Failed to add ticket ID: {} to the pool. The ticket generated has a duplicate ticket ID.", ticket.getTicketId());
                }
                Thread.sleep((long) ticketReleaseRate); // Simulate ticket processing
            }
            logger.info("Thread:{} for vendor with ID:{} finished executing", Thread.currentThread().getId(), vendorId);
        } catch (InterruptedException e) {
            logger.error("Thread for Vendor {} was interrupted. (Thread ID: {})", vendorId, Thread.currentThread().getId());
            Thread.currentThread().interrupt();  // Preserve the interrupt status
        }
        logger.info("Thread completed for Vendor ID: {} (Thread ID: {})", vendorId, Thread.currentThread().getId());
    }

    // Methods to manage the adminStopAllRelease flag
    public static boolean isAdminStopAllRelease() {
        return adminStopAllRelease.get();
    }


    public static void enableStopAllRelease() {
        adminStopAllRelease.set(true);
        logger.info("Global stop for all Ticket Release enabled.");
    }

    public static void disableStopAllRelease() {
        adminStopAllRelease.set(false);
        logger.info("Global stop for all Ticket Release disabled.");
    }
}
