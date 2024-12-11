package com.ticketSystem.controller;

import com.ticketSystem.model.Event;
import com.ticketSystem.model.SystemConfiguration;
import com.ticketSystem.repository.EventRepository;
import com.ticketSystem.repository.SystemConfigurationRepository;
import com.ticketSystem.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller responsible for configuring the ticket system's core parameters.
 * This controller provides an endpoint to set up the initial system configuration,
 * including ticket capacity, release and retrieval rates, and event details.
 */
@RestController
@RequestMapping("/api/system-configuration")
@CrossOrigin(origins = "http://localhost: 3000")
public class SystemConfigurationController {
    private static final Logger logger = LoggerFactory.getLogger(SystemConfigurationController.class);
    @Autowired
    private SystemConfigurationRepository configRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    /**
     * Helper method to add validation errors to the error list.
     * This method creates a structured error object and logs the validation issue.
     * @param validationErrors List to which the error will be added
     * @param field The field that failed validation
     * @param message The error message describing the validation failure
     */
    private void addValidationError(List<Map<String, String>> validationErrors, String field, String message) {
        Map<String, String> error = new HashMap<>();
        error.put("field", field);
        error.put("message", message);
        validationErrors.add(error);

        // Log each validation error
        logger.warn("Validation Error - Field: {}, Message: {}", field, message);
    }

    /**
     * Logs detailed system configuration after successful setup.
     * This method provides a comprehensive log of the configured system settings
     * and events to aid in system monitoring and audit trails.
     * @param systemConfig The system configuration object
     * @param savedEvents List of events configured in the system
     */
    private void logSystemConfiguration(SystemConfiguration systemConfig, List<Event> savedEvents) {
        logger.info("<<System Configuration Details>>");
        logger.info("Maximum Ticket Capacity: {}", systemConfig.getMaxCapacity());
        logger.info("Total Tickets: {}", systemConfig.getTotalTickets());
        logger.info("Ticket Release Rate: {}", systemConfig.getReleaseRate());
        logger.info("Ticket Retrieval Rate: {}", systemConfig.getRetrievalRate());

        logger.info("Configured Events:");
        savedEvents.forEach(event -> {
            logger.info("- Event Name: {}", event.getEventName());
            logger.info("  Event Price: ${}", event.getEventPrice());
        });
    }

    /**
     * Configures the entire ticket system with system-wide settings and events.
     * This endpoint allows initial configuration of the ticket system by setting:
     * - Maximum ticket capacity
     * - Total number of tickets
     * - Ticket release and retrieval rates
     * - List of events with their names and prices
     * Comprehensive validation is performed on all input parameters.
     *
     * @param config A map containing system configuration parameters
     * @return ResponseEntity with:
     *         - 200 OK and configuration details if successful
     *         - 400 Bad Request with validation errors
     *         - 500 Internal Server Error for unexpected issues
     */
    @PostMapping("/configure")
    public ResponseEntity<?> configureSystem(@RequestBody Map<String, Object> config) {
        logger.info("Received system configuration request");

        // Validate configuration parameters
        List<Map<String, String>> validationErrors = new ArrayList<>();

        // Validate max capacity
        Integer maxCapacity = (Integer) config.get("maxCapacity");
        if (maxCapacity == null) {
            addValidationError(validationErrors, "maxCapacity", "Maximum ticket capacity must be provided");
        } else if (maxCapacity <= 0) {
            addValidationError(validationErrors, "maxCapacity", "Maximum ticket capacity must be a positive number");
        }

        // Validate release rate
        Integer releaseRate = (Integer) config.get("releaseRate");
        if (releaseRate == null) {
            addValidationError(validationErrors, "releaseRate", "Release rate must be provided");
        } else if (releaseRate < 0) {
            addValidationError(validationErrors, "releaseRate", "Release rate cannot be negative");
        }

        // Validate retrieval rate
        Integer retrievalRate = (Integer) config.get("retrievalRate");
        if (retrievalRate == null) {
            addValidationError(validationErrors, "retrievalRate", "Retrieval rate must be provided");
        } else if (retrievalRate < 0) {
            addValidationError(validationErrors, "retrievalRate", "Retrieval rate cannot be negative");
        }

        // Validate total tickets
        Integer totalTickets = (Integer) config.get("totalTickets");
        if (totalTickets == null) {
            addValidationError(validationErrors, "totalTickets", "Total tickets must be provided");
        } else if (totalTickets <= 0) {
            addValidationError(validationErrors, "totalTickets", "Total tickets must be a positive number");
        }

        // Check if total tickets exceed max capacity
        if (maxCapacity != null && totalTickets != null && totalTickets > maxCapacity) {
            addValidationError(validationErrors, "tickets", "Total tickets cannot exceed maximum ticket capacity");
        }

        // Validate events
        List<Map<String, Object>> events = (List<Map<String, Object>>) config.get("events");
        if (events == null || events.isEmpty()) {
            addValidationError(validationErrors, "events", "At least one event must be configured");
        } else {
            // Validate each event
            for (int i = 0; i < events.size(); i++) {
                Map<String, Object> eventData = events.get(i);

                // Validate event name
                String eventName = (String) eventData.get("name");
                if (!StringUtils.hasText(eventName)) {
                    addValidationError(validationErrors, "events[" + i + "].name", "Event name cannot be empty");
                }

                // Validate event price
                Number eventPrice = (Number) eventData.get("price");
                if (eventPrice == null) {
                    addValidationError(validationErrors, "events[" + i + "].price", "Event price must be provided");
                } else if (eventPrice.doubleValue() <= 0) {
                    addValidationError(validationErrors, "events[" + i + "].price", "Event price must be a positive number");
                }

                // Check for duplicate event names
                long duplicateNameCount = events.stream()
                        .filter(e -> eventName.equals(e.get("name")))
                        .count();
                if (duplicateNameCount > 1) {
                    addValidationError(validationErrors, "events[" + i + "].name", "Duplicate event names are not allowed");
                }
            }
        }

        // If there are validation errors, return them
        if (!validationErrors.isEmpty()) {
            logger.error("System configuration validation failed");
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Configuration validation failed",
                    "details", validationErrors
            ));
        }

        try {
            // Reset existing data

            eventRepository.deleteAll();
            eventRepository.resetAutoIncrement();
            logger.info("Existing data cleared");

            // Save system configuration
            SystemConfiguration systemConfig = new SystemConfiguration();
            systemConfig.setMaxCapacity(maxCapacity);
            systemConfig.setTotalTickets(totalTickets);
            systemConfig.setReleaseRate(releaseRate);
            systemConfig.setRetrievalRate(retrievalRate);
            configRepository.save(systemConfig);
            logger.info("System configuration saved");

            // Save events
            List<Event> savedEvents = new ArrayList<>();
            events.forEach(eventData -> {
                Event event = new Event();
                event.setEventName((String) eventData.get("name"));
                event.setEventPrice(((Number) eventData.get("price")).doubleValue());
                savedEvents.add(eventRepository.save(event));
            });
            logger.info("{} events saved", savedEvents.size());

            // Log detailed system configuration
            logSystemConfiguration(systemConfig, savedEvents);

            // Prepare a success response with configuration details
            Map<String, Object> response = new HashMap<>();
            response.put("message", "System configured successfully");
            response.put("maxCapacity", maxCapacity);
            response.put("totalTickets", totalTickets);
            response.put("releaseRate", releaseRate);
            response.put("retrievalRate", retrievalRate);
            response.put("events", savedEvents.stream().map(Event::getEventName).toList());

            logger.info("System configuration completed successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error during system configuration", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred during configuration"));
        }
    }
}