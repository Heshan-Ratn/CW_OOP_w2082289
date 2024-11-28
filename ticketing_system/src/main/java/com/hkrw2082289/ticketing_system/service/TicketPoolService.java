package com.hkrw2082289.ticketing_system.service;


import com.hkrw2082289.ticketing_system.model.TicketEntity;
import com.hkrw2082289.ticketing_system.repository.TicketRepository;
import com.hkrw2082289.ticketing_system.utils.TicketUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
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

    // Create a logger specific to this class
    private static final Logger logger = LoggerFactory.getLogger(TicketPoolService.class);

    @Autowired
    public TicketPoolService(TicketRepository ticketRepository, ConfigurationService configurationService, TicketUtility ticketUtility) {
        this.ticketRepository = ticketRepository;
        this.configurationService = configurationService;
        this.ticketUtility = ticketUtility;  // Inject TicketUtility
        loadTicketsFromDatabase();
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

    public synchronized boolean addTicket(TicketEntity ticket) {
        ticketLock.lock();
        try {
            while (countAvailableTickets() >= getCurrentMaxCapacity()) {
                vendorCondition.await();
            }
//
//            while (countAvailableTickets() >= getCurrentMaxCapacity()) {
//                boolean signaled = vendorCondition.await(5000, TimeUnit.MILLISECONDS);  // Wait with timeout.
//                if (!signaled) {
//                    // Timeout occurred, recheck the condition
//                    logger.warn("Timeout reached while waiting for ticket availability.");
//                }
//                // After waking up, check the condition again.If the condition is still false (pool is still at max capacity), the thread will go back to waiting
//                if (countAvailableTickets() < getCurrentMaxCapacity()) {
//                    // The condition is now true, we can proceed with adding the ticket
//                    break;  // Exit the loop and proceed with the ticket addition
//                }
//            }
            tickets.add(ticket);
            ticketRepository.save(ticket);
            notifyConsumersForEvent(ticket.getEventName());
            cleanupUnusedConditions();
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } finally {
            ticketLock.unlock();
        }
    }

    public synchronized Object[] removeTicket(String eventName, String customerId) {
        ticketLock.lock();
        try {
            while (!isTicketAvailable(eventName)) {
                waitForSpecificEventTicket(eventName);
            }
            TicketEntity ticket = findAvailableTicket(eventName);
            if (ticket != null) {
                ticket.setTicketStatus("Booked");
                ticket.setCustomerId(customerId);
                ticketRepository.save(ticket);
                vendorCondition.signalAll();
                cleanupUnusedConditions();
                return new Object[]{true, ticket.getTicketId()};
            }
            return new Object[]{false, null};
        } catch (InterruptedException e) {
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

