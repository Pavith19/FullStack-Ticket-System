package com.ticketSystem.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Represents a transaction in the ticket management system.
 * This class captures details of a ticket purchase, including event information,
 * ticket price, vendor and customer IDs, ticket count, and transaction timestamp.
 * It is mapped to the 'transactions' database table using JPA annotations for ORM.
 */
@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_name")
    private String eventName;

    @Column(name = "ticket_price")
    private Double ticketPrice;

    @Column(name = "vendor_id")
    private Integer vendorId;

    @Column(name = "customer_id")
    private Integer customerId;

    @Column(name = "ticket_count")
    private Integer ticketCount;

    @Column(name = "transaction_timestamp")
    private LocalDateTime transactionTimestamp = LocalDateTime.now();

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Double getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(Double ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    public Integer getVendorId() {
        return vendorId;
    }

    public void setVendorId(Integer vendorId) {
        this.vendorId = vendorId;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Integer getTicketCount() {
        return ticketCount;
    }

    public void setTicketCount(Integer ticketCount) {
        this.ticketCount = ticketCount;
    }

    public LocalDateTime getTransactionTimestamp() {
        return transactionTimestamp;
    }

    public void setTransactionTimestamp(LocalDateTime transactionTimestamp) {
        this.transactionTimestamp = transactionTimestamp;
    }
}
