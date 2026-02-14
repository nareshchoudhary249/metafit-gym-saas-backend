package com.metafit.dto.request.trainer;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Assign member to trainer
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignMemberToTrainerRequest {

    @NotNull(message = "Member ID is required")
    private Long memberId;

    @NotNull(message = "Trainer ID is required")
    private Long trainerId;

    private String notes; // Initial assignment notes
}