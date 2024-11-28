package com.hkrw2082289.ticketing_system.service;

import com.hkrw2082289.ticketing_system.model.Customer;
import com.hkrw2082289.ticketing_system.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    private static final String CUSTOMER_ID_REGEX = "^[a-zA-Z]{4}\\d{3}$";

    public String signUpCustomer(String customerId, String password) {
        if (!customerId.matches(CUSTOMER_ID_REGEX)) {
            return "Error: Customer ID must be in the format of 4 letters followed by 3 digits.";
        }

        if (password.length() < 8 || password.length() > 12) {
            return "Error: Password must be between 8 and 12 characters.";
        }

        if (customerRepository.existsByCustomerId(customerId)) {
            return "Error: Customer ID already exists.";
        }

        Customer customer = new Customer();
        customer.setCustomerId(customerId);
        customer.setPassword(password);

        customerRepository.save(customer);
        return "Sign-up successful.";
    }

    public Map<String, String> signInCustomer(String customerId, String password) {
        Map<String, String> response = new HashMap<>();
        boolean isValid = customerRepository.existsByCustomerIdAndPassword(customerId, password);

        if (isValid) {
            response.put("message", "Sign-in successful.");
            response.put("customerId", customerId);
        } else {
            response.put("message", "Error: Invalid customer ID or password.");
            response.put("customerId", null);
        }

        return response;
    }
}
