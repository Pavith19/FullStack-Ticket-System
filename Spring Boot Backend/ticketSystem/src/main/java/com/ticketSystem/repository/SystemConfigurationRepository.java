package com.ticketSystem.repository;

import com.ticketSystem.model.SystemConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing SystemConfiguration entities in the ticket management system.
 * Provides data access operations for SystemConfiguration entities, extending Spring Data JPA's
 * JpaRepository to offer standard CRUD (Create, Read, Update, Delete) functionality.
 */
@Repository
public interface SystemConfigurationRepository extends JpaRepository<SystemConfiguration, Long> {

    /**
     * Retrieves the most recently created SystemConfiguration entry.
     * Finds the SystemConfiguration record with the highest ID, which typically
     * represents the most recent configuration settings in the system.
     * @return The most recently created SystemConfiguration entity
     */
    SystemConfiguration findFirstByOrderByIdDesc();
}
