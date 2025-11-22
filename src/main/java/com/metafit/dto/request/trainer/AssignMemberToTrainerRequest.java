package com.metafit.dto.request.trainer;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

// Assign member to trainer
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignMemberToTrainerRequest {

    @NotNull(message = "Member ID is required")
    private UUID memberId;

    @NotNull(message = "Trainer ID is required")
    private UUID trainerId;

    private String notes; // Initial assignment notes
}