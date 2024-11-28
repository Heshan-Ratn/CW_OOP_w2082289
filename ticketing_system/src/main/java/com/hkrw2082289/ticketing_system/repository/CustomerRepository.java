package com.hkrw2082289.ticketing_system.repository;

import com.hkrw2082289.ticketing_system.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {
    boolean existsByCustomerId(String customerId);

    //boolean existsByCustomerIdAndPassword(String customerId, String password);
    Customer findByCustomerIdAndPassword(String customerId, String password);
}

