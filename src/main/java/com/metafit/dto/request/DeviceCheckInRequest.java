
package com.metafit.dto.request;

import com.metafit.enums.DeviceType;
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
public class DeviceCheckInRequest {

    @NotBlank(message = "Device API key is required")
    private String apiKey;

    @NotBlank(message = "Device identifier is required")
    private String deviceIdentifier;

    private DeviceType deviceType;
    private String rawData;
    private Long timestamp;
}
