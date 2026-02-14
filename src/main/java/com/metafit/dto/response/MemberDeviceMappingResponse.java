package com.metafit.dto.response;

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
public class MemberDeviceMappingResponse {

    private Long id;
    private Long memberId;
    private String memberName;
    private Long deviceId;
    private String deviceName;
    private DeviceType deviceType;
    private String deviceIdentifier;
    private Boolean active;
    private LocalDateTime enrolledAt;
    private LocalDateTime lastUsed;
    private Long usageCount;
}