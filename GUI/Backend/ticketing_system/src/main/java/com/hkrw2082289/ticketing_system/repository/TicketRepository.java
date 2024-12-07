package com.hkrw2082289.ticketing_system.repository;

import com.hkrw2082289.ticketing_system.model.TicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TicketRepository extends JpaRepository<TicketEntity, String> {
}