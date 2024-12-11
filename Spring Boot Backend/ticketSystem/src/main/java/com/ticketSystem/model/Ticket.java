package com.ticketSystem.model;

/**
 * Represents an individual ticket in the ticket management system.
 * This immutable class encapsulates ticket details such as event name, price,and vendor ID.
 * Tickets are created with all necessary information at instantiation
 * and cannot be modified afterward.
 */
public class Ticket {
    private final String eventName;
    private final double price;
    private final int vendorId;

    /**
     * Constructs a new Ticket with complete ticket information.
     * @param eventName Name of the event for the ticket
     * @param price Price of the ticket
     * @param vendorId Unique identifier of the ticket vendor
     */
    public Ticket(String eventName, double price, int vendorId) {
        this.eventName = eventName;
        this.price = price;
        this.vendorId = vendorId;
    }

    // Getters
    public String getEventName() {
        return eventName;
    }

    public double getPrice() {
        return price;
    }

    public int getVendorId() {
        return vendorId;
    }
}
