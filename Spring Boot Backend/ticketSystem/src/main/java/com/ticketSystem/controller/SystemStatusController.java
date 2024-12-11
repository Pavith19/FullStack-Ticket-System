package com.ticketSystem.controller;

import com.ticketSystem.model.Event;
import com.ticketSystem.model.SystemConfiguration;
import com.ticketSystem.repository.EventRepository;
import com.ticketSystem.repository.SystemConfigurationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controller responsible for retrieving the current system status and configuration.
 * This controller provides an endpoint to fetch the current ticket system configuration,
 * including total tickets, release and retrieval rates, maximum capacity, and event details.
 */
@RestController
@RequestMapping("/api/system-status")
@CrossOrigin(origins = "http://localhost: 3000")
public class SystemStatusController {
    private static final Logger logger = LoggerFactory.getLogger(SystemStatusController.class);

    @Autowired
    private SystemConfigurationRepository configRepository;

    @Autowired
    private EventRepository eventRepository;

    /**
     * Retrieves the current system status and configuration.
     * This endpoint provides comprehensive information about the ticket system's current state,
     * including:
     * - Total number of tickets
     * - Ticket release rate
     * - Ticket retrieval rate
     * - Maximum ticket capacity
     * - List of configured events with their names and prices
     * @return ResponseEntity containing:
     *         - 200 OK with system status details if successful
     *         - 404 Not Found if no configuration or events are available
     *         - 500 Internal Server Error for unexpected errors
     */
    @GetMapping
    public ResponseEntity<?> getSystemStatus() {
        try {
            // Retrieve the latest system configuration
            SystemConfiguration currentConfig = configRepository.findFirstByOrderByIdDesc();

            // Validate system configuration exists
            if (currentConfig == null) {
                logger.warn("No system configuration found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "No system configuration available"));
            }

            // Retrieve all events
            List<Event> events = eventRepository.findAll();

            if (events.isEmpty()) {
                logger.warn("No events found in the system");
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "No events available"));
            }

            // Prepare a comprehensive status response
            Map<String, Object> statusResponse = new HashMap<>();

            // Add configuration details to the response
            statusResponse.put("totalTickets", currentConfig.getTotalTickets());
            statusResponse.put("releaseRate", currentConfig.getReleaseRate());
            statusResponse.put("retrievalRate", currentConfig.getRetrievalRate());
            statusResponse.put("maxCapacity", currentConfig.getMaxCapacity());

            logger.info("System configuration details added to the response.");

            // Transform event details for the response
            List<Map<String, Object>> eventDetails = events.stream().map(event -> {
                Map<String, Object> eventInfo = new HashMap<>();
                eventInfo.put("name", event.getEventName());
                eventInfo.put("price", event.getEventPrice());
                return eventInfo;
            }).collect(Collectors.toList());

            statusResponse.put("events", eventDetails);

            // Return successful response with system status
            return ResponseEntity.ok(statusResponse);
        } catch (Exception e) {
            // Log and handle any unexpected errors
            logger.error("Error retrieving system status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Unable to retrieve system status",
                            "details", e.getMessage()));
        }
    }

}