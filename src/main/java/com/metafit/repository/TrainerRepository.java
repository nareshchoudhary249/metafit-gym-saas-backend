package com.metafit.repository;

import com.metafit.entity.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Trainer Repository
 * Handles database operations for trainer records
 */
@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Long> {

    /**
     * Find trainer by email
     */
    Optional<Trainer> findByEmail(String email);

    /**
     * Find trainer by phone
     */
    Optional<Trainer> findByPhone(String phone);

    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);

    /**
     * Check if phone exists
     */
    boolean existsByPhone(String phone);

    /**
     * Find all active trainers
     */
    List<Trainer> findByActiveTrue();

    /**
     * Find all active trainers ordered by name
     */
    @Query("SELECT t FROM Trainer t WHERE t.active = true ORDER BY t.name ASC")
    List<Trainer> findActiveTrainersOrderedByName();

    /**
     * Count active trainers
     */
    long countByActiveTrue();

    /**
     * Find trainers by specialization
     */
    @Query("SELECT t FROM Trainer t WHERE t.active = true AND " +
            "LOWER(t.specialization) LIKE LOWER(CONCAT('%', :specialization, '%'))")
    List<Trainer> findBySpecialization(@Param("specialization") String specialization);

    /**
     * Search trainers by name
     */
    @Query("SELECT t FROM Trainer t WHERE " +
            "LOWER(t.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Trainer> searchByName(@Param("query") String query);

    /**
     * Find trainers with capacity available
     */
    @Query("SELECT t FROM Trainer t WHERE t.active = true AND " +
            "(SELECT COUNT(m) FROM Member m WHERE m.assignedTrainerId = t.id) < t.maxClients")
    List<Trainer> findTrainersWithCapacity();

    /**
     * Get trainer with current client count
     */
    @Query("SELECT t.id, t.name, COUNT(m.id) as clientCount " +
            "FROM Trainer t LEFT JOIN Member m ON m.assignedTrainerId = t.id " +
            "WHERE t.id = :trainerId " +
            "GROUP BY t.id, t.name")
    Object[] getTrainerWithClientCount(@Param("trainerId") Long trainerId);

    /**
     * Get all trainers with their client counts
     */
    @Query("SELECT t.id, t.name, t.email, t.phone, t.specialization, t.maxClients, " +
            "COUNT(m.id) as clientCount " +
            "FROM Trainer t LEFT JOIN Member m ON m.assignedTrainerId = t.id " +
            "WHERE t.active = true " +
            "GROUP BY t.id, t.name, t.email, t.phone, t.specialization, t.maxClients " +
            "ORDER BY t.name")
    List<Object[]> getAllTrainersWithClientCounts();

    /**
     * Find trainers at or over capacity
     */
    @Query("SELECT t FROM Trainer t WHERE t.active = true AND " +
            "(SELECT COUNT(m) FROM Member m WHERE m.assignedTrainerId = t.id) >= t.maxClients")
    List<Trainer> findTrainersAtCapacity();

    /**
     * Get trainer statistics
     */
    @Query("SELECT " +
            "COUNT(DISTINCT t.id) as totalTrainers, " +
            "COUNT(DISTINCT CASE WHEN t.active = true THEN t.id END) as activeTrainers, " +
            "COUNT(DISTINCT m.id) as totalAssignedMembers, " +
            "AVG(CAST((SELECT COUNT(mem) FROM Member mem WHERE mem.assignedTrainerId = t.id) AS double)) as avgClientsPerTrainer " +
            "FROM Trainer t LEFT JOIN Member m ON m.assignedTrainerId = t.id")
    Object[] getTrainerStatistics();

    /**
     * Find trainers created by specific user
     */
    List<Trainer> findByCreatedBy(String createdBy);

    /**
     * Check if trainer has any assigned members
     */
    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END " +
            "FROM Member m WHERE m.assignedTrainerId = :trainerId")
    boolean hasAssignedMembers(@Param("trainerId") Long trainerId);
}