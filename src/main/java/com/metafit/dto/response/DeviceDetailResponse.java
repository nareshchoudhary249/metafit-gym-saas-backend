package com.metafit.dto.response;

import com.metafit.enums.ConnectionType;
import com.metafit.enums.DeviceStatus;
import com.metafit.enums.DeviceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceDetailResponse {

    private Long id;
    private String name;
    private DeviceType deviceType;
    private String manufacturer;
    private String model;
    private String serialNumber;
    private String ipAddress;
    private Integer port;
    private String macAddress;
    private String location;
    private DeviceStatus status;
    private ConnectionType connectionType;
    private Boolean active;
    private String configuration;
    private String notes;
    private LocalDateTime lastPing;
    private LocalDateTime lastCheckIn;
    private Long totalCheckIns;
    private Long enrolledMembers;
    private Long todayCheckIns;
    private String webhookUrl;
    private String apiKey;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
}
