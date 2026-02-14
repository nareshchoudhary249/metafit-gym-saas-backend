package com.metafit.entity;

import com.metafit.enums.DeviceType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Member Device Mapping Entity
 * Maps members to their RFID cards, biometric data, QR codes, etc.
 * A member can have multiple devices (RFID + Biometric)
 */
@Entity
@Table(name = "member_device_mappings",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"device_id", "device_identifier"})
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDeviceMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Member reference
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    /**
     * Device reference
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    /**
     * Device-specific identifier
     * - For RFID: Card number (e.g., "1234567890")
     * - For Biometric: User ID in device (e.g., "1001")
     * - For QR Code: Unique code (e.g., "QR-MEMBER-001")
     */
    @Column(name = "device_identifier", nullable = false, length = 100)
    private String deviceIdentifier;

    /**
     * Device type for quick lookup
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "device_type", nullable = false, length = 20)
    private DeviceType deviceType;

    /**
     * Is this mapping active
     */
    @Column(nullable = false)
    private Boolean active;

    /**
     * Enrollment date (when member was registered on device)
     */
    @Column(name = "enrolled_at")
    private LocalDateTime enrolledAt;

    /**
     * Last used timestamp
     */
    @Column(name = "last_used")
    private LocalDateTime lastUsed;

    /**
     * Total times used
     */
    @Column(name = "usage_count")
    private Long usageCount;

    /**
     * Additional metadata (JSON)
     * Example: {"fingerIndex": 1, "quality": "high"}
     */
    @Column(columnDefinition = "TEXT")
    private String metadata;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        enrolledAt = LocalDateTime.now();

        if (active == null) {
            active = true;
        }
        if (usageCount == null) {
            usageCount = 0L;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Update last used timestamp and increment counter
     */
    public void recordUsage() {
        this.lastUsed = LocalDateTime.now();
        if (this.usageCount == null) {
            this.usageCount = 0L;
        }
        this.usageCount++;
    }
}