package com.metafit.entity;

import com.metafit.enums.AttendanceSource;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Attendance Entity
 * Tracks member check-ins and check-outs
 */
@Entity
@Table(name = "attendance", indexes = {
        @Index(name = "idx_member_id", columnList = "member_id"),
        @Index(name = "idx_check_in_time", columnList = "check_in_time")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "check_in_time", nullable = false)
    private LocalDateTime checkInTime;

    @Column(name = "check_out_time")
    @Getter
    public LocalDateTime checkOutTime;

    @Column(name = "duration_minutes")
    private Long durationMinutes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AttendanceSource source;

    @Column(length = 500)
    private String notes;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @PrePersist
    protected void onCreate() {
        if (checkInTime == null) {
            checkInTime = LocalDateTime.now();
        }
        if (source == null) {
            source = AttendanceSource.MANUAL;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        // Calculate duration when checkout is done
        if (checkOutTime != null && durationMinutes == null) {
            durationMinutes = ChronoUnit.MINUTES.between(checkInTime, checkOutTime);
        }
    }

    /**
     * Helper method to check if member is currently checked in
     */
    public boolean isCheckedIn() {
        return checkOutTime == null;
    }

    /**
     * Calculate and update duration
     */
    public void calculateDuration() {
        if (checkInTime != null && checkOutTime != null) {
            durationMinutes = ChronoUnit.MINUTES.between(checkInTime, checkOutTime);
        }
    }
}