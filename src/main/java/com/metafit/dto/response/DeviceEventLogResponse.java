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
public class DeviceEventLogResponse {

    private Long id;
    private Long deviceId;
    private String deviceName;
    private String eventType;
    private String deviceIdentifier;
    private Long memberId;
    private String memberName;
    private Boolean success;
    private String message;
    private String errorCode;
    private Long attendanceId;
    private LocalDateTime eventTime;
}
