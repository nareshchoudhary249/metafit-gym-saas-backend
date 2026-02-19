package com.metafit.dto.request.trainer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request to update trainer notes for a member.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTrainerNotesRequest {

    @NotNull(message = "Trainer ID is required")
    private Long trainerId;

    @NotBlank(message = "Notes are required")
    private String notes;
}
