package com.metafit.repository;

import com.metafit.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Attendance Repository
 * Handles database operations for attendance records
 */
@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    /**
     * Find today's attendance records
     */
    @Query("SELECT a FROM Attendance a WHERE a.checkInTime BETWEEN :startDate AND :endDate ORDER BY a.checkInTime DESC")
    List<Attendance> findTodayAttendance(@Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate);

    /**
     * Count today's check-ins
     */
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.checkInTime BETWEEN :startDate AND :endDate")
    Long countTodayAttendance(@Param("startDate") LocalDateTime startDate,
                              @Param("endDate") LocalDateTime endDate);

    /**
     * Find attendance by member ID
     */
    List<Attendance> findByMemberIdOrderByCheckInTimeDesc(Long memberId);

    /**
     * Find attendance by member ID within date range
     */
    @Query("SELECT a FROM Attendance a WHERE " + "a.member.id = :memberId AND " + "a.checkInTime BETWEEN :startDate AND :endDate " + "ORDER BY a.checkInTime DESC")
    List<Attendance> findByMemberIdAndDateRange(@Param("memberId") Long memberId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Check if member is currently checked in (no checkout time)
     */
    @Query("SELECT a FROM Attendance a WHERE a.member.id = :memberId AND a.checkOutTime IS NULL " +
            "AND a.checkInTime BETWEEN :startDate AND :endDate")
    Optional<Attendance> findTodayActiveCheckIn(@Param("memberId") Long memberId,
                                                @Param("startDate") LocalDateTime startDate,
                                                @Param("endDate") LocalDateTime endDate);

    /**
     * Find attendance records between dates
     */
    @Query("SELECT a FROM Attendance a WHERE " + "a.checkInTime BETWEEN :startDate AND :endDate " + "ORDER BY a.checkInTime DESC")
    List<Attendance> findAttendanceBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Count total attendance for a member
     */
    Long countByMemberId(Long memberId);

    /**
     * Count attendance for member within date range
     */
    @Query("SELECT COUNT(a) FROM Attendance a WHERE " + "a.member.id = :memberId AND " + "a.checkInTime BETWEEN :startDate AND :endDate")
    Long countByMemberIdAndCheckInTimeBetween(@Param("memberId") Long memberId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Find last check-in for a member
     */
    Optional<Attendance> findTopByMemberIdOrderByCheckInTimeDesc(Long memberId);

    /**
     * Get attendance count by date (for analytics)
     */
    @Query("SELECT CAST(a.checkInTime AS date) as date, COUNT(a) as count " +
            "FROM Attendance a WHERE " +
            "a.checkInTime BETWEEN :startDate AND :endDate " +
            "GROUP BY CAST(a.checkInTime AS date) " +
            "ORDER BY CAST(a.checkInTime AS date)")
    List<Object[]> getAttendanceCountByDate(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Get average duration for a member
     */
    @Query("SELECT AVG(a.durationMinutes) FROM Attendance a WHERE " + "a.member.id = :memberId AND " + "a.durationMinutes IS NOT NULL")
    Double getAverageDurationByMemberId(@Param("memberId") Long memberId);

    /**
     * Count active check-ins (not yet checked out)
     */
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.checkInTime BETWEEN :startDate AND :endDate " +
            "AND a.checkOutTime IS NULL")
    Long countActiveCheckIns(@Param("startDate") LocalDateTime startDate,
                             @Param("endDate") LocalDateTime endDate);

    List<Attendance> findByMemberIdAndCheckInTimeBetween(Long memberId, LocalDateTime startDate, LocalDateTime endDate);

    List<Attendance> findByCheckInTimeBetweenOrderByCheckInTimeDesc(LocalDateTime startDate, LocalDateTime endDate);

    // ADDED: Count check-ins between date-time range
    Long countByCheckInTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
}
