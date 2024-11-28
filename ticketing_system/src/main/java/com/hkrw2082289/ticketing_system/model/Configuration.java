package com.hkrw2082289.ticketing_system.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "configuration")
public class Configuration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "config_id")
    private Integer configId;

    @Column(name = "total_available_tickets", nullable = false)
    private Integer totalAvailableTickets = 0;

    @Column(name = "ticket_release_rate", nullable = false)
    private Double ticketReleaseRate;

    @Column(name = "customer_retrieval_rate", nullable = false)
    private Double customerRetrievalRate;

    @Column(name = "max_ticket_capacity", nullable = false)
    private Integer maxTicketCapacity;

    @Column(name = "config_admin_user", nullable = false, length = 50)
    private String configAdminUser;

    @Column(name = "config_admin_password", nullable = false, length = 50)
    private String configAdminPassword;
}

