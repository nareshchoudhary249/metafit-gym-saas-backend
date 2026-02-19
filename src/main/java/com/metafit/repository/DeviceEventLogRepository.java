package com.metafit.repository;

import com.metafit.entity.DeviceEventLog;
import com.metafit.enums.DeviceEventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceEventLogRepository extends JpaRepository<DeviceEventLog, Long> {

    List<DeviceEventLog> findByDeviceIdOrderByEventTimeDesc(Long deviceId);

    List<DeviceEventLog> findByMemberIdOrderByEventTimeDesc(Long memberId);

    @Query("SELECT e FROM DeviceEventLog e WHERE " +
            "e.device.id = :deviceId AND " +
            "e.eventTime BETWEEN :startTime AND :endTime " +
            "ORDER BY e.eventTime DESC")
    List<DeviceEventLog> findByDeviceAndTimeRange(
            @Param("deviceId") Long deviceId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    @Query("SELECT e FROM DeviceEventLog e WHERE " +
            "e.eventType = :eventType AND " +
            "e.eventTime BETWEEN :startTime AND :endTime " +
            "ORDER BY e.eventTime DESC")
    List<DeviceEventLog> findByEventTypeAndTimeRange(
            @Param("eventType") DeviceEventType eventType,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    @Query("SELECT e FROM DeviceEventLog e WHERE " +
            "e.success = false AND " +
            "e.eventTime > :since " +
            "ORDER BY e.eventTime DESC")
    List<DeviceEventLog> findRecentErrors(@Param("since") LocalDateTime since);

    @Query("SELECT COUNT(e) FROM DeviceEventLog e WHERE " +
            "e.device.id = :deviceId AND " +
            "e.eventType = 'CHECK_IN' AND " +
            "e.eventTime BETWEEN :startTime AND :endTime")
    long countTodayCheckInsByDevice(
            @Param("deviceId") Long deviceId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    @Query("SELECT e.eventType, COUNT(e) FROM DeviceEventLog e WHERE " +
            "e.device.id = :deviceId AND " +
            "e.eventTime BETWEEN :startTime AND :endTime " +
            "GROUP BY e.eventType")
    List<Object[]> getEventStatsByDevice(
            @Param("deviceId") Long deviceId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
}
