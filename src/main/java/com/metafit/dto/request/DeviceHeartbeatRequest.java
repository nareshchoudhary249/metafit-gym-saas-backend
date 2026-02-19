package com.metafit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceHeartbeatRequest {

    @NotBlank(message = "Device API key is required")
    private String apiKey;

    private String status;
    private String firmwareVersion;
    private Integer batteryLevel;
    private String additionalInfo;
}
