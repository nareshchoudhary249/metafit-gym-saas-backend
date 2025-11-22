package com.metafit.repository;

import com.metafit.entity.MembershipPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Membership Plan Repository
 * Handles database operations for membership plans
 */
@Repository
public interface MembershipPlanRepository extends JpaRepository<MembershipPlan, Long> {

    /**
     * Find plan by name
     */
    Optional<MembershipPlan> findByName(String name);

    /**
     * Check if plan name exists
     */
    boolean existsByName(String name);

    /**
     * Find all active plans
     */
    List<MembershipPlan> findByActiveTrue();

    /**
     * Find all active plans ordered by display order
     */
    @Query("SELECT m FROM MembershipPlan m WHERE m.active = true " +
            "ORDER BY m.displayOrder ASC, m.price ASC")
    List<MembershipPlan> findActivePlansOrderedByDisplayOrder();

    /**
     * Find plans by duration (in months)
     */
    List<MembershipPlan> findByDurationMonthsAndActiveTrue(Integer durationMonths);

    /**
     * Find plans within price range
     */
    @Query("SELECT m FROM MembershipPlan m WHERE " +
            "m.active = true AND " +
            "m.discountedPrice BETWEEN :minPrice AND :maxPrice " +
            "ORDER BY m.discountedPrice ASC")
    List<MembershipPlan> findPlansByPriceRange(Double minPrice, Double maxPrice);

    /**
     * Find plans with personal training
     */
    @Query("SELECT m FROM MembershipPlan m WHERE " +
            "m.active = true AND " +
            "m.personalTrainingSessions > 0 " +
            "ORDER BY m.personalTrainingSessions DESC")
    List<MembershipPlan> findPlansWithPersonalTraining();

    /**
     * Find plans with diet plan included
     */
    List<MembershipPlan> findByDietPlanIncludedTrueAndActiveTrue();

    /**
     * Count active plans
     */
    Long countByActiveTrue();

    /**
     * Get most popular plan (used by most members)
     */
    @Query("SELECT m.membershipPlan, COUNT(m) as count " +
            "FROM Member m WHERE m.status = 'ACTIVE' " +
            "GROUP BY m.membershipPlan " +
            "ORDER BY count DESC")
    List<Object[]> getMostPopularPlans();
}