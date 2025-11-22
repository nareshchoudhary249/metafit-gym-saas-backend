package com.metafit.dto.response.attendance;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberAttendanceItemResponse {

    private LocalDateTime date;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private Long durationMinutes;
    private String source;
}