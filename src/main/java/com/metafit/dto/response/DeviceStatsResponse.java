package com.metafit.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceStatsResponse {

    private Long totalDevices;
    private Long onlineDevices;
    private Long offlineDevices;
    private Long activeDevices;
    private Long totalCheckInsToday;
    private Long totalEnrolledMembers;
    private Long errorCount;
}