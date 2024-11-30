package com.hkrw2082289.ticketing_system.controller;
import com.hkrw2082289.ticketing_system.utils.CustomerSimulation;
import com.hkrw2082289.ticketing_system.utils.VendorSimulation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/simulation")
public class SimulationController {

    @Autowired
    private VendorSimulation vendorSimulation;

    @Autowired
    private CustomerSimulation customerSimulation;

    @PostMapping("/start-vendor")
    public ResponseEntity<String> startSimulationForVendor(@RequestParam int numberOfVendors) {
        //http://localhost:8080/api/simulation/start-vendor?numberOfVendors=5
        vendorSimulation.simulateVendorsAddingTickets(numberOfVendors);
        return ResponseEntity.ok("Simulation started for " + numberOfVendors + " vendors.");
    }

    @PostMapping("/start-customer")
    public ResponseEntity<String> startSimulationForCustomer(@RequestParam int numberOfCustomers) {
        //http://localhost:8080/api/simulation/start-customer?numberOfCustomers=5
        customerSimulation.simulateCustomerThreads(numberOfCustomers);
        return ResponseEntity.ok("Customer simulation started for " + numberOfCustomers + " customers.");
    }

    @PostMapping("/start")
    public ResponseEntity<String> startSimulation(@RequestParam int numberOfUsers) {
        //http://localhost:8080/api/simulation/start?numberOfUsers=5
        vendorSimulation.simulateVendorsAddingTickets(numberOfUsers);
        customerSimulation.simulateCustomerThreads(numberOfUsers);
        return ResponseEntity.ok("Customer simulation started for " + numberOfUsers + " customers and vendors.");
    }
}
