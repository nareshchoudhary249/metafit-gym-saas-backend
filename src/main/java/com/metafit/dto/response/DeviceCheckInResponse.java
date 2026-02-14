package com.metafit.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceCheckInResponse {

    private Boolean success;
    private String message;
    private Long memberId;
    private String memberName;
    private String membershipStatus;
    private LocalDateTime checkInTime;
    private Long attendanceId;
    private String errorCode;
}