package com.ticketSystem.repository;

import com.ticketSystem.model.Event;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing Event entities in the ticket management system.
 * Provides data access operations for Event entities, extending Spring Data JPA's
 * JpaRepository to offer standard CRUD (Create, Read, Update, Delete) functionality.
 */
@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {

    /**
     * Deletes all Event entities from the database.
     */
    void deleteAll();

    /**
     * Finds an Event by its unique name.
     * Searches the database for an event with the specified event name.
     * @param eventName The unique name of the event to search for
     * @return An Optional containing the Event if found, or an empty Optional if no matching event exists
     */
    Optional<Event> findByEventName(String eventName);

    /**
     * Resets the auto-increment counter for the events table to 1.
     * This is typically used after deleting all records to ensure the next
     * inserted record starts with ID 1. Uses a native SQL query to modify the table's auto-increment value.
     */
    @Modifying
    @Transactional
    @Query(value = "ALTER TABLE events AUTO_INCREMENT = 1", nativeQuery = true)
    void resetAutoIncrement();
}
