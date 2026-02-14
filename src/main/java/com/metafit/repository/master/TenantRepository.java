package com.metafit.repository.master;

import com.metafit.entity.master.Tenant;
import com.metafit.enums.TenantStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Tenant Repository (Master Database)
 * Handles database operations for tenant records in the master database
 *
 * NOTE: This repository connects to the MASTER database (gym_master_db)
 * NOT the tenant-specific databases
 */
@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {

    /**
     * Find tenant by code (unique identifier)
     * This is the primary way to look up tenants
     */
    Optional<Tenant> findByCode(String code);

    /**
     * Find tenant by database name
     */
    Optional<Tenant> findByDbName(String dbName);

    /**
     * Find tenant by owner email
     */
    Optional<Tenant> findByOwnerEmail(String ownerEmail);

    /**
     * Check if tenant code exists
     */
    boolean existsByCode(String code);

    /**
     * Check if database name exists
     */
    boolean existsByDbName(String dbName);

    /**
     * Check if owner email exists
     */
    boolean existsByOwnerEmail(String ownerEmail);

    /**
     * Find all tenants by status
     */
    List<Tenant> findByStatus(TenantStatus status);

    /**
     * Find all active tenants
     */
    @Query("SELECT t FROM Tenant t WHERE t.status = 'ACTIVE' ORDER BY t.name ASC")
    List<Tenant> findAllActiveTenants();

    /**
     * Find all trial tenants
     */
    @Query("SELECT t FROM Tenant t WHERE t.status = 'TRIAL' ORDER BY t.createdAt DESC")
    List<Tenant> findAllTrialTenants();

    /**
     * Count tenants by status
     */
    long countByStatus(TenantStatus status);

    /**
     * Count all active tenants
     */
    @Query("SELECT COUNT(t) FROM Tenant t WHERE t.status = 'ACTIVE'")
    long countActiveTenants();

    /**
     * Search tenants by name
     */
    @Query("SELECT t FROM Tenant t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Tenant> searchByName(@Param("query") String query);

    /**
     * Find tenants created between dates
     */
    @Query("SELECT t FROM Tenant t WHERE t.createdAt BETWEEN :startDate AND :endDate ORDER BY t.createdAt DESC")
    List<Tenant> findTenantsCreatedBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Find tenants by owner phone
     */
    @Query("SELECT t FROM Tenant t WHERE t.ownerPhone = :phone")
    List<Tenant> findByOwnerPhone(@Param("phone") String phone);

    /**
     * Get tenant count grouped by status
     */
    @Query("SELECT t.status, COUNT(t) FROM Tenant t GROUP BY t.status")
    List<Object[]> countTenantsByStatus();

    /**
     * Find recently created tenants
     */
    @Query("SELECT t FROM Tenant t ORDER BY t.createdAt DESC")
    List<Tenant> findRecentTenants();

    /**
     * Find suspended tenants
     */
    @Query("SELECT t FROM Tenant t WHERE t.status = 'SUSPENDED' ORDER BY t.updatedAt DESC")
    List<Tenant> findSuspendedTenants();

    /**
     * Find cancelled tenants
     */
    @Query("SELECT t FROM Tenant t WHERE t.status = 'CANCELLED' ORDER BY t.updatedAt DESC")
    List<Tenant> findCancelledTenants();

    /**
     * Update tenant status
     */
    @Query("UPDATE Tenant t SET t.status = :status, t.updatedAt = CURRENT_TIMESTAMP WHERE t.id = :id")
    void updateTenantStatus(@Param("id") Long id, @Param("status") TenantStatus status);

    /**
     * Find tenants with specific config property
     * (Searches in JSONB config column)
     */
    @Query(value = "SELECT * FROM tenants WHERE config::text LIKE %:searchTerm%", nativeQuery = true)
    List<Tenant> findByConfigContaining(@Param("searchTerm") String searchTerm);
}