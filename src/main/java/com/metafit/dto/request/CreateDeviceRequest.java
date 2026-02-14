package com.metafit.dto.request;

import com.metafit.enums.ConnectionType;
import com.metafit.enums.DeviceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// ==================== CREATE DEVICE REQUEST ====================
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateDeviceRequest {

    @NotBlank(message = "Device name is required")
    private String name;

    @NotNull(message = "Device type is required")
    private DeviceType deviceType;

    private String manufacturer;
    private String model;
    private String serialNumber;
    private String ipAddress;
    private Integer port;
    private String macAddress;
    private String location;
    private ConnectionType connectionType;
    private String configuration;
    private String notes;
}