package com.hkrw2082289.ticketing_system.model;

import com.hkrw2082289.ticketing_system.helper.PurchaseRequest;
import com.hkrw2082289.ticketing_system.service.TicketPoolService;
import jakarta.persistence.*;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

@Data
@Entity
@Table(name = "customers")
public class Customer implements Runnable{
    private static final Logger logger = LoggerFactory.getLogger(Customer.class);

    // AtomicBoolean to control purchases globally
    private static final AtomicBoolean adminStopAllPurchases = new AtomicBoolean(false);

    @Id
    @Column(name = "customer_id", nullable = false, unique = true, length = 7)
    private String customerId;

    @Column(nullable = false, length = 12)
    private String password;

    @Transient
    private double customerRetrievalRate;

    @Transient
    private PurchaseRequest purchaseRequest;

    @Transient
    private TicketPoolService ticketPoolService;

    @Override
    public void run() {
        logger.info("Thread started for Customer ID: {} with purchase batch size: {} (Thread ID: {})",
                customerId, purchaseRequest != null ? purchaseRequest.getTicketToBook(): 0, Thread.currentThread().getId());

        if (ticketPoolService == null) {
            logger.error("TicketPoolService is not set. Cannot book tickets to the pool.");
            return;
        }
        try{
            String eventName = purchaseRequest.getEventName();
            int ticketsToBook = purchaseRequest.getTicketToBook();

            int ticketbooked = 0;
            for (int i = 0; i < ticketsToBook; i++) {
                // Check if global stop flag is enabled
                if (adminStopAllPurchases.get()) {
                    logger.info("Global stop enabled. Customer {} thread will terminate. (Thread ID: {})",
                            customerId, Thread.currentThread().getId());
                    break;
                }

                if (Thread.currentThread().isInterrupted()) {
                    logger.info("Thread for Customer ID: {} was interrupted. (Thread ID: {})", customerId, Thread.currentThread().getId());
                    break;
                }
                Object[] booked = ticketPoolService.removeTicket(eventName, customerId);
                if ((boolean)booked[0]) {
                    ticketbooked++;
                    logger.info("Successfully booked ticket ID: {} to the pool. Ticket No. of ticked booked in batch:{} ", booked[1],ticketbooked);
                } else {
                    logger.warn("Failed to book ticket ID: {} to the pool.", booked[1]);
                }
                Thread.sleep((long) customerRetrievalRate); // Simulate ticket processing
            }
            logger.info("Thread:{} for customer with ID:{} finished executing", Thread.currentThread().getId(), customerId);
        }
        catch (InterruptedException e) {
            logger.error("Thread for Customer {} was interrupted. (Thread ID: {})", customerId, Thread.currentThread().getId());
            Thread.currentThread().interrupt();  // Preserve the interrupt status
        }
        logger.info("Thread completed for Vendor ID: {} (Thread ID: {})", customerId, Thread.currentThread().getId());
    }

    // Methods to manage the adminStopAllPurchases flag
    public static boolean isAdminStopAllPurchases() {
        return adminStopAllPurchases.get();
    }

    public static void enableStopAllPurchases() {
        adminStopAllPurchases.set(true);
        logger.info("Global stop for all purchases enabled.");
    }

    public static void disableStopAllPurchases() {
        adminStopAllPurchases.set(false);
        logger.info("Global stop for all purchases disabled.");
    }
}
