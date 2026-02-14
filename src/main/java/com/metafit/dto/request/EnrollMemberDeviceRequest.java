package com.metafit.dto.request;

import com.metafit.entity.ConnectionType;
import com.metafit.entity.DeviceType;
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
public class EnrollMemberDeviceRequest {

    @NotNull(message = "Member ID is required")
    private Long memberId;

    @NotNull(message = "Device ID is required")
    private Long deviceId;

    @NotBlank(message = "Device identifier is required")
    private String deviceIdentifier;

    private String metadata;
}