package com.metafit.dto.response.attendance;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TodayAttendanceSummaryResponse {

    private Long totalCheckIns;
    private Long currentlyInGym;
    private Long checkedOut;
    private LocalDateTime lastCheckIn;
}