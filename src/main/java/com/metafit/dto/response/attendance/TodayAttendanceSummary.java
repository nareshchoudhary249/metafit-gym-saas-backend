package com.metafit.dto.response.attendance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodayAttendanceSummary {

    private Long totalCheckIns;
    private Long currentlyInGym;
    private Long checkedOut;
    private LocalDateTime lastCheckIn;
    private Long totalCheckOuts; // Completed sessions

    // Breakdown by source
    private Map<String, Long> bySource; // {"MANUAL": 10, "BIOMETRIC": 45, "RFID": 30}

    // Time statistics
    private String peakHour; // "6:00 PM - 7:00 PM"
    private Double averageDurationMinutes;
    private String averageDurationFormatted; // "1h 25m"

    // Capacity metrics
    private Long totalMemberCapacity; // Total active members
    private Double attendancePercentage; // (totalPresent / totalActive) * 100

    public TodayAttendanceSummary(long totalCheckIns, long currentlyInGym, long checkedOut, LocalDateTime lastCheckIn) {
    }

    /**
     * Calculate attendance percentage
     */
    public void calculatePercentage() {
        if (totalMemberCapacity != null && totalMemberCapacity > 0) {
            this.attendancePercentage =
                    (totalCheckIns.doubleValue() / totalMemberCapacity) * 100;
        }
    }

    /**
     * Format average duration
     */
    public void formatAverageDuration() {
        if (averageDurationMinutes != null) {
            long hours = averageDurationMinutes.longValue() / 60;
            long minutes = averageDurationMinutes.longValue() % 60;

            if (hours > 0) {
                this.averageDurationFormatted = String.format("%dh %dm", hours, minutes);
            } else {
                this.averageDurationFormatted = String.format("%dm", minutes);
            }
        }
    }
}