package com.hkrw2082289.ticketing_system.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @Column(name = "customer_id", nullable = false, unique = true, length = 7)
    private String customerId;

    @Column(nullable = false, length = 12)
    private String password;

}
