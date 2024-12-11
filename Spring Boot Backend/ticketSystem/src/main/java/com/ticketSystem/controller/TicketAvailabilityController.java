package com.ticketSystem.controller;

import com.ticketSystem.service.TicketPoolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller responsible for providing real-time ticket availability information.
 * Offers an endpoint to retrieve current ticket status, including:
 * - Ticket availability by event
 * - Total tickets added
 * - Current available tickets
 * - Total tickets sold
 */
@RestController
@RequestMapping("/api/ticket-availability")
@CrossOrigin(origins = "http://localhost: 3000")
public class TicketAvailabilityController {
    private static final Logger logger = LoggerFactory.getLogger(TicketAvailabilityController.class);

    @Autowired
    private TicketPoolService ticketPoolService;

    /**
     * Retrieves the current ticket availability status.
     * This endpoint provides comprehensive information about ticket availability:
     * - If the system is not running, returns empty availability with zero counts
     * - When running, returns:
     *   - Current ticket availability by event
     *   - Total tickets added to the system
     *   - Current available tickets
     *   - Total tickets sold
     *
     * @return ResponseEntity with:
     *         - 200 OK with ticket availability details
     *         - 500 Internal Server Error for unexpected issues
     */
    @GetMapping
    public ResponseEntity<?> getTicketAvailability() {
        try {

            // Prepare response map for ticket availability
            Map<String, Object> response = new HashMap<>();

            // Handle scenario when ticket system is not running
            if (!ticketPoolService.isRunning()) {
                response.put("availability", new HashMap<>());
                response.put("ticketsAdded", 0);
                response.put("currentTickets", 0);
                response.put("ticketsSold", 0);
                return ResponseEntity.ok(response);
            }

            // Populate response with current ticket system metrics
            response.put("availability", ticketPoolService.getCurrentTicketsByEvent());
            response.put("ticketsAdded", ticketPoolService.getTicketsAdded());
            response.put("currentTickets", ticketPoolService.getCurrentTickets());
            response.put("ticketsSold", ticketPoolService.getTicketsSold());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Handle and log any unexpected errors during availability retrieval
            logger.error("An unexpected error occurred while fetching ticket availability.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "Internal server error",
                            "message", "An unexpected error occurred. Please try again later."
                    ));
        }
    }

}