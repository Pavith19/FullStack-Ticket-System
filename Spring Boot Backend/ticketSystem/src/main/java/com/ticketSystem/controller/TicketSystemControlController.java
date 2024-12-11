package com.ticketSystem.controller;

import com.ticketSystem.repository.TransactionRepository;
import com.ticketSystem.service.TicketPoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller responsible for managing the lifecycle of the ticket system.
 * Provides endpoints to start, stop, and reset the ticket handling system,
 * with comprehensive state management and error handling.
 */
@RestController
@RequestMapping("/api/ticket-system-control")
@CrossOrigin(origins = "http://localhost: 3000")
public class TicketSystemControlController {

    //Logger for capturing important events and errors in the ticket system control process
    private static final Logger logger = LoggerFactory.getLogger(TicketSystemControlController.class);

    @Autowired
    private TicketPoolService ticketPoolService;

    @Autowired
    private TransactionRepository transactionRepository;

    /**
     * Starts the ticket handling system.
     * This endpoint initiates the ticket system, performing the following checks and actions:
     * - Verifies the system is not already running
     * - Checks that not all tickets have been sold
     * - Clears existing transactions
     * - Initializes ticket handling mechanism
     *
     * @return ResponseEntity with:
     *         - 200 OK if system starts successfully
     *         - 400 Bad Request if system is already running or all tickets are sold
     *         - 500 Internal Server Error for unexpected issues
     */
    @PostMapping("/start")
    public ResponseEntity<?> startTicketSystem() {
        try {
            // Check if the system is already running
            if (ticketPoolService.isRunning()) {
                logger.info("Ticket system is already running. Please reset first.");
                return ResponseEntity.badRequest().body("Ticket system is already running. Please reset first.");
            }

            // Check if all tickets have been sold
            if (ticketPoolService.isAllTicketsSold()) {
                logger.info("All tickets have been sold. System must be reset before restarting.");
                return ResponseEntity.badRequest().body("All tickets have been sold. System must be reset before restarting.");
            }

            // Clear transactions and reset auto-increment
            transactionRepository.deleteAll();
            transactionRepository.resetAutoIncrement();
            logger.info("Transaction table cleared.");

            // Start the ticket handling system
            ticketPoolService.startTicketHandling();
            return ResponseEntity.ok("Ticket system started");
        } catch (IllegalStateException e) {
            // Handle specific illegal state exceptions
            logger.warn("Failed to start ticket system: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error starting ticket system", e);
            return ResponseEntity.internalServerError().body("Failed to start ticket system: " + e.getMessage());
        }
    }

    /**
     * Stops the ticket handling system.
     * This endpoint halts the ongoing ticket system, with the following behaviors:
     * - Checks if the system is currently running
     * - Stops ticket handling if active
     *
     * @return ResponseEntity with:
     *         - 200 OK if system stops successfully
     *         - 400 Bad Request if system is not running
     *         - 500 Internal Server Error for unexpected issues
     */
    @PostMapping("/stop")
    public ResponseEntity<?> stopTicketSystem() {
        try {
            // Prevent stopping an already stopped system
            if (!ticketPoolService.isRunning()) {
                logger.info("Attempted to stop the ticket system, but it is not currently running.");
                return ResponseEntity.badRequest().body("Ticket system is not running.");
            }

            // Stop the ticket handling mechanism
            ticketPoolService.stopTicketHandling();
            logger.info("Ticket system stopped successfully.");
            return ResponseEntity.ok("Ticket system stopped successfully");

        } catch (Exception e) {
            // Handle unexpected errors during system stop
            logger.error("Error stopping ticket system", e);
            return ResponseEntity.internalServerError().body("Failed to stop ticket system: " + e.getMessage());
        }
    }

    /**
     * Resets the ticket handling system to its initial state.
     * This endpoint performs a complete reset of the ticket system:
     * - Stops the system if it's currently running
     * - Resets ticket handling mechanism to initial configuration
     *
     * @return ResponseEntity with:
     *         - 200 OK if system resets successfully
     *         - 500 Internal Server Error for unexpected issues
     */
    @PostMapping("/reset")
    public ResponseEntity<?> resetTicketSystem() {
        try {
            // Stop the system if it's running
            if (ticketPoolService.isRunning()) {
                logger.info("Ticket system is currently running. Attempting to stop it before reset.");
                ticketPoolService.stopTicketHandling();
            }

            // Reset the ticket handling system
            ticketPoolService.resetTicketHandling();
            return ResponseEntity.ok("Ticket system reset successfully");
        } catch (Exception e) {
            // Handle unexpected errors during system reset
            logger.error("Error resetting ticket system", e);
            return ResponseEntity.internalServerError().body("Failed to reset ticket system: " + e.getMessage());
        }
    }
}