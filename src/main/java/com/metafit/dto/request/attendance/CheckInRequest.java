package com.metafit.dto.request.attendance;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

// ============= CHECK-IN REQUEST =============

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckInRequest {

    @NotNull(message = "Member ID is required")
    private UUID memberId;

    private String source = "MANUAL"; // MANUAL, RFID, BIOMETRIC

    private String notes;
}