package com.ticketSystem.model;

import jakarta.persistence.*;

/**
 * Represents an event in the ticket management system.
 * This class defines the structure of events, storing essential information
 * such as event ID, name, and price. It is mapped to the 'events' database table
 * using JPA annotations for ORM (Object-Relational Mapping).
 */

@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer eventId;

    @Column(name = "event_name", unique = true)
    private String eventName;

    @Column(name = "event_price")
    private Double eventPrice;

    // Getters and Setters
    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Double getEventPrice() {
        return eventPrice;
    }

    public void setEventPrice(Double eventPrice) {
        this.eventPrice = eventPrice;
    }
}
