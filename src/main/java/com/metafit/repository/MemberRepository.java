package com.metafit.repository;

import com.metafit.entity.Attendance;
import com.metafit.entity.Member;
import com.metafit.enums.MemberStatus;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    // Find by phone (unique)
    Optional<Member> findByPhone(String phone);

    // Find by email (unique)
    Optional<Member> findByEmail(String email);

    // Check if phone exists (for validation)
    boolean existsByPhone(String phone);

    // Check if email exists (for validation)
    boolean existsByEmail(String email);

    // Find all active members
    List<Member> findByStatus(MemberStatus status);

    // Count active members
    long countByStatus(MemberStatus status);

    // Search members by name or phone
    @Query("SELECT m FROM Member m WHERE " +
            "LOWER(m.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "m.phone LIKE CONCAT('%', :query, '%')")
    List<Member> searchByNameOrPhone(@Param("query") String query);

    // Find members with paginated results
    Page<Member> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // Find members by status with pagination
    Page<Member> findByStatusOrderByCreatedAtDesc(MemberStatus status, Pageable pageable);

    // Find expiring members (membership ending within given date range)
    @Query("SELECT m FROM Member m WHERE " +
            "m.status = 'ACTIVE' AND " +
            "m.membershipEndDate BETWEEN :startDate AND :endDate " +
            "ORDER BY m.membershipEndDate ASC")
    List<Member> findExpiringMembers(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // Find expired members (membership ended before today)
    @Query("SELECT m FROM Member m WHERE " +
            "m.status = 'ACTIVE' AND " +
            "m.membershipEndDate < :today " +
            "ORDER BY m.membershipEndDate DESC")
    List<Member> findExpiredMembers(@Param("today") LocalDate today);

    // Find members assigned to a trainer
    List<Member> findByAssignedTrainerId(Long trainerId);

    // Count members assigned to a trainer
    long countByAssignedTrainerId(Long trainerId);

    // Find members by membership plan
    List<Member> findByMembershipPlan(String plan);

    // Get all members with membership ending today
    @Query("SELECT m FROM Member m WHERE " +
            "m.status = 'ACTIVE' AND " +
            "m.membershipEndDate = :today")
    List<Member> findMembersExpiringToday(@Param("today") LocalDate today);

    // Custom query to get member count by status
    @Query("SELECT m.status, COUNT(m) FROM Member m GROUP BY m.status")
    List<Object[]> countMembersByStatus();

    // Find members created in date range
    @Query("SELECT m FROM Member m WHERE " +
            "m.createdAt BETWEEN :startDate AND :endDate " +
            "ORDER BY m.createdAt DESC")
    List<Member> findMembersCreatedBetween(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    Optional<Attendance> findById(@NotNull(message = "Member ID is required") UUID memberId);
}