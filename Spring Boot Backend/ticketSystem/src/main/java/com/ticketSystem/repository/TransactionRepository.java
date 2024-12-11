package com.ticketSystem.repository;

import com.ticketSystem.model.Transaction;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing Transaction entities in the ticket management system.
 * Provides data access operations for Transaction entities, extending Spring Data JPA's
 * JpaRepository to offer standard CRUD (Create, Read, Update, Delete) functionality.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Deletes all Transaction entities from the database.
     */
    void deleteAll();

    /**
     * Resets the auto-increment counter for the transactions table to 1.
     * This is typically used after deleting all records to ensure the next
     * inserted record starts with ID 1. Uses a native SQL query to modify
     * the table's auto-increment value.
     */
    @Modifying
    @Transactional
    @Query(value = "ALTER TABLE transactions AUTO_INCREMENT = 1", nativeQuery = true)
    void resetAutoIncrement();
}
