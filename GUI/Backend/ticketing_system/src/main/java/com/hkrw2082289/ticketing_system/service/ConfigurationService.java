package com.hkrw2082289.ticketing_system.service;

import com.hkrw2082289.ticketing_system.model.Configuration;
import com.hkrw2082289.ticketing_system.repository.ConfigurationRepository;
import com.hkrw2082289.ticketing_system.utils.ResponseFinder;
import com.hkrw2082289.ticketing_system.utils.TicketUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ConfigurationService {

    @Autowired
    private ConfigurationRepository configurationRepository;

    @Autowired
    private TicketUtility ticketUtility;

    private Configuration getOrCreateDefaultConfiguration() {
        return configurationRepository.findAll()
                .stream()
                .findFirst()
                .orElseGet(() -> {
                    Configuration defaultConfig = new Configuration();
                    defaultConfig.setTicketReleaseRate(1.0);
                    defaultConfig.setCustomerRetrievalRate(1.0);
                    defaultConfig.setMaxTicketCapacity(100);
                    defaultConfig.setConfigAdminUser("admin");
                    defaultConfig.setConfigAdminPassword("admin123");
                    return configurationRepository.save(defaultConfig);
                });
    }

    public Configuration viewConfiguration() {
        Configuration config = getOrCreateDefaultConfiguration();
        int totalAvailableTickets = (int) ticketUtility.countAvailableTickets();
        config.setTotalAvailableTickets(totalAvailableTickets);
        return config;
    }

    public ResponseFinder updateAdminCredentials(String oldUser, String oldPassword, String newUser, String newPassword) {
        Configuration config = getOrCreateDefaultConfiguration();
        if (config.getConfigAdminUser().equals(oldUser) && config.getConfigAdminPassword().equals(oldPassword)) {
            config.setConfigAdminUser(newUser);
            config.setConfigAdminPassword(newPassword);
            configurationRepository.save(config);
            return new ResponseFinder(true,"Admin credentials updated successfully.");
        }
        return new ResponseFinder(false, "Error: Invalid old credentials.");
    }

    public ResponseFinder updateTicketSettings(String adminUser, String adminPassword, Double releaseRate, Double retrievalRate, Integer maxCapacity) {
        Configuration config = getOrCreateDefaultConfiguration();

        // Get total available tickets from TicketPoolService
        int totalAvailableTickets = (int) ticketUtility.countAvailableTickets();

        // Check if the new maxCapacity is valid
        if (maxCapacity < totalAvailableTickets) {
            return new ResponseFinder(false, "Error: Cannot set max capacity lower than the current total available tickets.");
        }

        if (config.getConfigAdminUser().equals(adminUser) && config.getConfigAdminPassword().equals(adminPassword)) {
            config.setTotalAvailableTickets(totalAvailableTickets);
            config.setTicketReleaseRate(releaseRate);
            config.setCustomerRetrievalRate(retrievalRate);
            config.setMaxTicketCapacity(maxCapacity);
            configurationRepository.save(config);

            return new ResponseFinder(true, "Ticket settings updated successfully.");
        }
        return new ResponseFinder(false, "Error: Invalid admin credentials.");
    }
}
