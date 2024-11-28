package com.hkrw2082289.ticketing_system.repository;

import com.hkrw2082289.ticketing_system.model.TicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<TicketEntity, String> {
//    List<Ticket> findByVendorIdAndTicketStatus(String vendorId, String ticketStatus);
//    List<Ticket> findByTicketStatus(String ticketStatus);
//    List<Ticket> findByTicketStatusAndEventName(String ticketStatus, String eventName);
//    List<Ticket> findByTicketStatusAndCustomerId(String ticketStatus, String customerId);
}