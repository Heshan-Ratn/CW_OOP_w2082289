package com.hkrw2082289.ticketing_system.model;

import jakarta.persistence.*;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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


    // Example: A field to track what the vendor thread does
    private transient boolean active = true; // Not persisted in the database

    @Override
    public void run() {
        logger.info("Thread started for Vendor ID: {} (Thread ID: {})", vendorId, Thread.currentThread().getId());

        try {
            for (int i = 0; i < 5; i++) {
                if (Thread.currentThread().isInterrupted()) {
                    logger.info("Thread for Vendor ID: {} was interrupted. (Thread ID: {})", vendorId, Thread.currentThread().getId());
                    break;
                }
                Thread.sleep(3000);  // Simulate work
                logger.info("Vendor {} is working... (Thread ID: {})", vendorId, Thread.currentThread().getId());
            }
        } catch (InterruptedException e) {
            logger.error("Thread for Vendor {} was interrupted. (Thread ID: {})", vendorId, Thread.currentThread().getId());
            Thread.currentThread().interrupt();  // Preserve the interrupt status
        }

        logger.info("Thread completed for Vendor ID: {} (Thread ID: {})", vendorId, Thread.currentThread().getId());
    }
}
