package com.metafit.dto.response;


import com.metafit.enums.ConnectionType;
import com.metafit.enums.DeviceStatus;
import com.metafit.enums.DeviceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// ==================== DEVICE RESPONSE ====================
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceResponse {

    private Long id;
    private String name;
    private DeviceType deviceType;
    private String manufacturer;
    private String model;
    private String serialNumber;
    private String ipAddress;
    private Integer port;
    private String location;
    private DeviceStatus status;
    private ConnectionType connectionType;
    private Boolean active;
    private LocalDateTime lastPing;
    private LocalDateTime lastCheckIn;
    private Long totalCheckIns;
    private String webhookUrl;
    private Boolean isOnline;
    private LocalDateTime createdAt;
}