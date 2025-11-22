package com.metafit.dto.request.trainer;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTrainerRequest {

    @NotNull
    private String fullName;

    @NotNull
    private String phone;

    private String email;
    private String specialization;
    private String bio;
    private Integer maxClients;
}
