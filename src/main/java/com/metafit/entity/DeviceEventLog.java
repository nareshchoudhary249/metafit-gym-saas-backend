package com.metafit.entity;

import com.metafit.enums.DeviceEventType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Device Event Log Entity
 * Logs all events from devices (check-ins, errors, status changes)
 */
@Entity
@Table(name = "device_event_logs", indexes = {
        @Index(name = "idx_device_id", columnList = "device_id"),
        @Index(name = "idx_event_time", columnList = "event_time")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceEventLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Device that generated the event
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    /**
     * Event type
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 30)
    private DeviceEventType eventType;

    /**
     * Device identifier used (card number, user ID, etc.)
     */
    @Column(name = "device_identifier", length = 100)
    private String deviceIdentifier;

    /**
     * Member ID (if identified)
     */
    @Column(name = "member_id")
    private Long memberId;

    /**
     * Was the event successful
     */
    @Column(nullable = false)
    private Boolean success;

    /**
     * Event message
     */
    @Column(columnDefinition = "TEXT")
    private String message;

    /**
     * Error code (if failed)
     */
    @Column(name = "error_code", length = 50)
    private String errorCode;

    /**
     * Raw event data (JSON)
     */
    @Column(name = "raw_data", columnDefinition = "TEXT")
    private String rawData;

    /**
     * Attendance record created (if check-in)
     */
    @Column(name = "attendance_id")
    private Long attendanceId;

    /**
     * Event timestamp
     */
    @Column(name = "event_time", nullable = false)
    private LocalDateTime eventTime;

    /**
     * Processing timestamp
     */
    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @PrePersist
    protected void onCreate() {
        if (eventTime == null) {
            eventTime = LocalDateTime.now();
        }
        processedAt = LocalDateTime.now();
    }
}

/**
 * Device Event Type Enum
 */
//public enum DeviceEventType {
//    CHECK_IN,               // Successful check-in
//    CHECK_OUT,              // Successful check-out
//    ACCESS_DENIED,          // Access denied (expired membership, etc.)
//    DEVICE_NOT_FOUND,       // Device identifier not found
//    MEMBER_NOT_FOUND,       // Member not registered
//    DUPLICATE_CHECK_IN,     // Member already checked in
//    DEVICE_ONLINE,          // Device came online
//    DEVICE_OFFLINE,         // Device went offline
//    DEVICE_ERROR,           // Device reported error
//    ENROLLMENT,             // New device enrollment
//    UNENROLLMENT,           // Device unenrollment
//    HEARTBEAT,              // Device heartbeat/ping
//    CONFIGURATION_CHANGE,   // Configuration updated
//    FIRMWARE_UPDATE,        // Firmware update event
//    TAMPERING_DETECTED,     // Security: Device tampering
//    LOW_BATTERY,            // Battery warning
//    DOOR_OPEN,              // Door sensor (if applicable)
//    DOOR_CLOSED             // Door sensor (if applicable)
//}