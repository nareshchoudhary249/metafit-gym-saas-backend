package com.metafit.dto.response.attendance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO representing a single attendance record for a member's history
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberAttendanceItem {

    private Long attendanceId;
    private LocalDate date; // Attendance date
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private String source; // MANUAL, BIOMETRIC, RFID, etc.
    private Long durationMinutes; // Total duration in minutes
    private String durationFormatted; // "1h 30m"

    /**
     * Calculate and set formatted duration
     */
    public void calculateDuration() {
        if (checkIn != null && checkOut != null) {
            long minutes = java.time.Duration.between(checkIn, checkOut).toMinutes();
            this.durationMinutes = minutes;
            this.durationFormatted = formatDuration(minutes);
        }
    }

    /**
     * Format duration as "Xh Ym"
     */
    private String formatDuration(long totalMinutes) {
        if (totalMinutes < 0) return "Invalid";

        long hours = totalMinutes / 60;
        long minutes = totalMinutes % 60;

        if (hours > 0) {
            return String.format("%dh %dm", hours, minutes);
        } else {
            return String.format("%dm", minutes);
        }
    }

    /**
     * Check if member is still inside (no check-out)
     */
    public boolean isStillInside() {
        return checkIn != null && checkOut == null;
    }
}