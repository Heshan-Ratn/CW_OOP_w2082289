package com.hkrw2082289.ticketing_system.service;

import com.hkrw2082289.ticketing_system.model.TicketEntity;
import com.hkrw2082289.ticketing_system.repository.TicketRepository;
import com.hkrw2082289.ticketing_system.utils.TicketUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Scope("singleton")
public class TicketPoolService {

    private final ConcurrentLinkedQueue<TicketEntity> tickets = new ConcurrentLinkedQueue<>();
    private final Lock ticketLock = new ReentrantLock();
    private final Condition vendorCondition = ticketLock.newCondition();
    private final Map<String, Condition> consumerConditions = new HashMap<>();
    private final Map<String, Integer> eventUsageCount = new HashMap<>();

    private final TicketRepository ticketRepository;
    private final ConfigurationService configurationService;
    private final TicketUtility ticketUtility;

    private final SimpMessagingTemplate messagingTemplate;

    // Create a logger specific to this class
    private static final Logger logger = LoggerFactory.getLogger(TicketPoolService.class);

    @Autowired
    public TicketPoolService(TicketRepository ticketRepository,
                             ConfigurationService configurationService,
                             TicketUtility ticketUtility,
                             SimpMessagingTemplate messagingTemplate) {
        this.ticketRepository = ticketRepository;
        this.configurationService = configurationService;
        this.ticketUtility = ticketUtility;  // Inject TicketUtility
        this.messagingTemplate = messagingTemplate;
        loadTicketsFromDatabase();
    }

    //
    // Method to send log messages to the frontend
    private void sendLogMessage(String message) {
        messagingTemplate.convertAndSend("/topic/logs", message); // Send log to WebSocket topic
    }

    private int getCurrentMaxCapacity() {
        // Fetch the max capacity directly from the ConfigurationService, no need to store it here
        return configurationService.viewConfiguration().getMaxTicketCapacity();
    }

    public Set<String> getEventDetails() {
        ticketLock.lock();  // Locking to ensure thread-safe reading
        try {
            return tickets.stream()
                    .map(TicketEntity::getEventName)
                    .collect(Collectors.toSet());
        } finally {
            ticketLock.unlock();  // Unlock after reading
        }
    }

    private void loadTicketsFromDatabase() {
        ticketLock.lock();
        try {
            List<TicketEntity> dbTickets = ticketRepository.findAll();
            tickets.addAll(dbTickets);
        } finally {
            ticketLock.unlock();
        }
    }

    public boolean addTicket(TicketEntity ticket) {
        ticketLock.lock();
        try {
            while (countAvailableTickets() >= getCurrentMaxCapacity()) {
                sendLogMessage("Waiting to add ticket, pool is full..."+ Thread.currentThread().getId());
                logger.info("Waiting to add ticket, pool is full...");
                vendorCondition.await();
            }
            // Save the ticket to the database (ticketId will be auto-generated)
            TicketEntity savedTicket = ticketRepository.save(ticket);
            tickets.add(savedTicket);

            // Notify clients
            messagingTemplate.convertAndSend("/topic/ticketpool", tickets);
            sendLogMessage("Added ticket for event: " + ticket.getEventName()+" with ID: "+ticket.getTicketId());

            logger.info("Added ticket for event: {}", ticket.getEventName());
            notifyConsumersForEvent(ticket.getEventName());
            logger.info("Thread {} notified consumers for event: {}",
                    Thread.currentThread().getId(), ticket.getEventName());
            cleanupUnusedConditions();
            return true;
        } catch (InterruptedException e) {
            sendLogMessage("Thread " + Thread.currentThread().getId() +" for vendor: "+ ticket.getVendorId() + " interrupted while adding ticket");
            logger.error("Thread {} interrupted while adding ticket", Thread.currentThread().getId());
            Thread.currentThread().interrupt();
            return false;
        } finally {
            ticketLock.unlock();
        }
    }

    public Object[] removeTicket(String eventName, String customerId) {
        ticketLock.lock();
        try {
            while (!isTicketAvailable(eventName)) {
                sendLogMessage("Thread " + Thread.currentThread().getId() + " waiting for tickets to become available for event: " + eventName);
                logger.info("Thread {} waiting for tickets to become available for event: {}",
                        Thread.currentThread().getId(), eventName);
                waitForSpecificEventTicket(eventName);
            }
            TicketEntity ticket = findAvailableTicket(eventName);
            if (ticket != null) {
                // Update ticket status and customer information
                ticket.setTicketStatus("Booked");
                ticket.setCustomerId(customerId);

                // Persist the updated ticket
                TicketEntity updatedTicket = ticketRepository.save(ticket);

                // Notify clients
                messagingTemplate.convertAndSend("/topic/ticketpool", tickets);
                sendLogMessage("Thread " + Thread.currentThread().getId() + " booked ticket " + updatedTicket.getTicketId() + " for event: " + eventName + " by customer: " + customerId);

                logger.info("Thread {} booked ticket {} for event: {} by customer: {}",
                        Thread.currentThread().getId(),  updatedTicket.getTicketId(), eventName, customerId);
                vendorCondition.signalAll();
                logger.info("Thread {} signaled vendors for more capacity", Thread.currentThread().getId());
                cleanupUnusedConditions();
                return new Object[]{true, ticket.getTicketId()};
            }
            sendLogMessage("Thread " + Thread.currentThread().getId() + " found no tickets available for event: " + eventName);
            logger.info("Thread {} found no tickets available for event: {}",
                    Thread.currentThread().getId(), eventName);
            return new Object[]{false, null};
        } catch (InterruptedException e) {
            sendLogMessage("Thread " + Thread.currentThread().getId() + " interrupted while booking ticket");
            logger.error("Thread {} interrupted while booking ticket", Thread.currentThread().getId());
            Thread.currentThread().interrupt();
            return new Object[]{false, null};
        } finally {
            ticketLock.unlock();
        }
    }

    private boolean isTicketAvailable(String eventName) {
        ticketLock.lock();
        try {
            return tickets.stream().anyMatch(
                    t -> "Available".equals(t.getTicketStatus()) && eventName.equals(t.getEventName()));
        } finally {
            ticketLock.unlock();
        }
    }

    public int countAvailableTickets() {
        ticketLock.lock();
        try {
            return (int) ticketUtility.countAvailableTickets();
        } finally {
            ticketLock.unlock();
        }
    }


    private TicketEntity findAvailableTicket(String eventName) {
        ticketLock.lock();
        try {
            return tickets.stream()
                    .filter(t -> "Available".equals(t.getTicketStatus()) && eventName.equals(t.getEventName()))
                    .findFirst()
                    .orElse(null);
        } finally {
            ticketLock.unlock();
        }
    }

    private void waitForSpecificEventTicket(String eventName) throws InterruptedException {
        ticketLock.lock();
        try {
            consumerConditions.putIfAbsent(eventName, ticketLock.newCondition());
            eventUsageCount.put(eventName, eventUsageCount.getOrDefault(eventName, 0) + 1);
            consumerConditions.get(eventName).await();
        } finally {
            ticketLock.unlock();
        }
    }

    private void notifyConsumersForEvent(String eventName) {
        ticketLock.lock();
        try {
            Condition condition = consumerConditions.get(eventName);
            if (condition != null) {
                condition.signalAll();
            }
            // Cleanup the event condition if there are no more waiting consumers
            Integer usageCount = eventUsageCount.get(eventName);
            if (usageCount != null && usageCount <= 0) {
                // No more consumers are waiting for this event, clean up the resources
                consumerConditions.remove(eventName);
                eventUsageCount.remove(eventName);
            }
        } finally {
            ticketLock.unlock();
        }
    }

    private void cleanupUnusedConditions() {
        ticketLock.lock();
        try {
            Iterator<Map.Entry<String, Integer>> iterator = eventUsageCount.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Integer> entry = iterator.next();
                if (entry.getValue() <= 0) {
                    consumerConditions.remove(entry.getKey());
                    iterator.remove();
                }
            }
        } finally {
            ticketLock.unlock();
        }
    }

    // Exposed Methods
    public Map<String, Integer> viewAvailableTicketCountsByVendor(String vendorId) {
        ticketLock.lock();
        try {
            return tickets.stream()
                    .filter(ticket -> vendorId.equals(ticket.getVendorId()) && "Available".equals(ticket.getTicketStatus()))
                    .collect(Collectors.groupingBy(TicketEntity::getEventName, Collectors.summingInt(ticket -> 1)));
        } finally {
            ticketLock.unlock();
        }
    }

    public Map<String, Integer> countAvailableTicketsByEvent() {
        ticketLock.lock();
        try {
            return tickets.stream()
                    .filter(ticket -> "Available".equals(ticket.getTicketStatus()))
                    .collect(Collectors.groupingBy(TicketEntity::getEventName, Collectors.summingInt(ticket -> 1)));
        } finally {
            ticketLock.unlock();
        }
    }

    public Map<String, Integer> countBookedTicketsByEvent() {
        ticketLock.lock();
        try {
            return tickets.stream()
                    .filter(ticket -> "Booked".equals(ticket.getTicketStatus()))
                    .collect(Collectors.groupingBy(TicketEntity::getEventName, Collectors.summingInt(ticket -> 1)));
        } finally {
            ticketLock.unlock();
        }
    }

    public Map<String, Long> countBookedTicketsByCustomerId(String customerId) {
        ticketLock.lock();
        try {
            return tickets.stream()
                    .filter(ticket -> "Booked".equals(ticket.getTicketStatus()) && customerId.equals(ticket.getCustomerId()))
                    .collect(Collectors.groupingBy(TicketEntity::getEventName, Collectors.counting()));
        } finally {
            ticketLock.unlock();
        }
    }
}

