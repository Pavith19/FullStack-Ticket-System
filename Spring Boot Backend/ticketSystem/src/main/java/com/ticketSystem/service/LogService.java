package com.ticketSystem.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Service responsible for logging system events and communications.
 * This service provides comprehensive logging capabilities for the ticket system,
 * supporting both backend logging via SLF4J and frontend notifications via WebSocket.
 * It captures critical events such as ticket additions, purchases, system state changes,
 * and thread interruptions.
 * Key Features:
 * - Logs events to console/log files using SLF4J
 * - Sends real-time updates to frontend via WebSocket
 * - Tracks system events with detailed contextual information
 */
@Service
public class LogService {
    private static final Logger logger = LoggerFactory.getLogger(LogService.class);

    /**
     * Spring WebSocket messaging template for sending real-time updates to the frontend.
     * Autowired by Spring dependency injection.
     */
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Logs and broadcasts details of ticket addition by a vendor.
     * Records the vendor's ticket addition to both backend logs and frontend notification system.
     * Provides comprehensive details about the ticket addition event.
     * @param vendorId Unique identifier of the vendor adding tickets
     * @param eventName Name of the event for which tickets are being added
     * @param ticketsAdded Number of tickets added
     * @param price Price of the added tickets
     * @param currentTickets Current total number of tickets after addition
     */
    public void logVendorTicketAddition(int vendorId, String eventName, int ticketsAdded, double price, int currentTickets) {
        String logMessage = String.format("Vendor %d added %d tickets for event %s at price $%.2f",
                vendorId, ticketsAdded, eventName, price);
        logger.info(logMessage);
        sendLogToFrontend("VENDOR_TICKET_ADD", logMessage, Map.of(
                "vendorId", vendorId,
                "eventName", eventName,
                "ticketsAdded", ticketsAdded,
                "price", price,
                "currentTickets", currentTickets
        ));
    }

    /**
     * Logs and broadcasts details of a ticket purchase transaction.
     * Records the customer's ticket purchase to both backend logs and frontend notification system.
     * Captures comprehensive details about the purchase event.
     * @param customerId Unique identifier of the customer making the purchase
     * @param ticketsBought Number of tickets purchased
     * @param eventNames List of event names for the purchased tickets
     * @param totalPrice Total price of the ticket purchase
     * @param currentTickets Current total number of tickets after purchase
     */
    public void logTicketPurchase(int customerId, int ticketsBought,
                                  List<String> eventNames, double totalPrice, int currentTickets) {
        String logMessage = String.format("Customer %d purchased %d tickets for events %s | Total Price: $%.2f",
                customerId, ticketsBought, eventNames, totalPrice);
        logger.info(logMessage);
        sendLogToFrontend("TICKET_PURCHASE", logMessage, Map.of(
                "customerId", customerId,
                "ticketsBought", ticketsBought,
                "events", eventNames,
                "totalPrice", totalPrice,
                "currentTickets", currentTickets
        ));
    }

    /**
     * Logs the system startup event.
     * Records when the ticket handling system is initialized and starts running.
     * Sends a notification to the frontend about system startup.
     */
    public void logSystemStart() {
        String logMessage = "Ticket handling system started";
        logger.info(logMessage);
        sendLogToFrontend("SYSTEM_START", logMessage, Map.of(
                "timestamp", LocalDateTime.now().toString()
        ));
    }

    /**
     * Logs the system shutdown event.
     * Records system termination details, including total tickets added and sold.
     * Provides a summary of system activity before shutdown.
     * @param ticketsAdded Total number of tickets added during system runtime
     * @param ticketsSold Total number of tickets sold during system runtime
     */
    public void logSystemStop(int ticketsAdded, int ticketsSold) {
        String logMessage = String.format("Ticket system stopped. Total tickets added: %d, Total tickets sold: %d",
                ticketsAdded, ticketsSold);
        logger.info(logMessage);
        sendLogToFrontend("SYSTEM_STOP", logMessage, Map.of(
                "ticketsAdded", ticketsAdded,
                "ticketsSold", ticketsSold,
                "timestamp", LocalDateTime.now().toString()
        ));
    }

    /**
     * Logs the system reset event.
     * Indicates that the ticket handling system has been reset and is ready to restart.
     * Notifies both backend logs and frontend about the system reset.
     */
    public void logSystemReset() {
        String logMessage = "Ticket handling system has been reset and is ready to start again.";
        logger.info(logMessage);
        sendLogToFrontend("SYSTEM_RESET", logMessage, Map.of(
                "timestamp", LocalDateTime.now().toString()
        ));
    }

    /**
     * Logs vendor thread interruption events.
     * Records when a vendor's processing thread is unexpectedly interrupted.
     * Sends error notification to both logs and frontend.
     * @param vendorId Unique identifier of the vendor whose thread was interrupted
     */
    public void logVendorThreadInterruption(int vendorId) {
        String logMessage = String.format("Vendor thread for vendor %d was interrupted", vendorId);
        logger.error(logMessage);
        sendLogToFrontend("VENDOR_THREAD_INTERRUPT", logMessage, Map.of(
                "vendorId", vendorId
        ));
    }

    /**
     * Logs customer ticket purchase interruption events.
     * Records when a customer's ticket purchase process is unexpectedly interrupted.
     * Sends error notification to both logs and frontend.
     * @param customerId Unique identifier of the customer experiencing purchase interruption
     */
    public void logCustomerTicketPurchaseInterruption(int customerId) {
        String logMessage = String.format("Ticket purchase was interrupted for customer %d", customerId);
        logger.error(logMessage);
        sendLogToFrontend("CUSTOMER_PURCHASE_INTERRUPT", logMessage, Map.of(
                "customerId", customerId
        ));
    }

    /**
     * Logs general system status messages.
     * Provides a flexible method for logging and broadcasting system status updates.
     * @param status Descriptive status message about the system's current state
     */
    public void logSystemStatus(String status) {
        logger.info(status);
        sendLogToFrontend("SYSTEM_STATUS", status, Map.of(
                "timestamp", LocalDateTime.now().toString()
        ));
    }

    /**
     * Internal method to send log messages to frontend via WebSocket.
     * Converts log information into a structured message and broadcasts it
     * to the frontend through a predefined WebSocket topic.
     * @param type Categorization type of the log message
     * @param message Human-readable log message
     * @param details Additional contextual details about the event
     */
    private void sendLogToFrontend(String type, String message, Map<String, Object> details) {
        try {
            messagingTemplate.convertAndSend("/topic/system-updates", Map.of(
                    "type", type,
                    "message", message,
                    "details", details,
                    "timestamp", LocalDateTime.now().toString()
            ));
        } catch (Exception e) {
            logger.error("Failed to send log to frontend", e);
        }
    }
}