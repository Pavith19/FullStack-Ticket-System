package com.ticketSystem.model;

import jakarta.persistence.*;

/**
 * Represents system-wide configuration parameters for the ticket management system.
 * This class stores global settings that control ticket distribution, system capacity,
 * and ticket release/retrieval rates. It is mapped to the 'system_config' database table
 * using JPA annotations for ORM (Object-Relational Mapping).
 */
@Entity
@Table(name = "system_config")
public class SystemConfiguration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "total_tickets")
    private Integer totalTickets;

    @Column(name = "release_rate")
    private Integer releaseRate;

    @Column(name = "retrieval_rate")
    private Integer retrievalRate;

    @Column(name = "max_capacity")
    private Integer maxCapacity;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getTotalTickets() {
        return totalTickets;
    }

    public void setTotalTickets(Integer totalTickets) {
        this.totalTickets = totalTickets;
    }

    public Integer getReleaseRate() {
        return releaseRate;
    }

    public void setReleaseRate(Integer releaseRate) {
        this.releaseRate = releaseRate;
    }

    public Integer getRetrievalRate() {
        return retrievalRate;
    }

    public void setRetrievalRate(Integer retrievalRate) {
        this.retrievalRate = retrievalRate;
    }

    public Integer getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(Integer maxCapacity) {
        this.maxCapacity = maxCapacity;
    }
}

