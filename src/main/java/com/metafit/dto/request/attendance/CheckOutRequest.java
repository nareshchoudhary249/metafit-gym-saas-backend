package com.metafit.dto.request.attendance;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckOutRequest {

    @NotNull(message = "Attendance ID is required")
    private UUID attendanceId;
}