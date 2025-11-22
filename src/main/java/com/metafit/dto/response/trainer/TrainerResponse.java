package com.metafit.dto.response.trainer;

import com.metafit.entity.Trainer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainerResponse {
    private UUID id;
    private String fullName;
    private String phone;
    private String email;
    private String specialization;
    private String bio;
    private Boolean isActive;
    private Integer maxClients;
    private Integer currentClients; // Count of assigned members

    public static TrainerResponse fromEntity(Trainer trainer, int currentClients) {
        TrainerResponse response = new TrainerResponse();
        response.setId(trainer.getId());
        response.setFullName(trainer.getFullName());
        response.setPhone(trainer.getPhone());
        response.setEmail(trainer.getEmail());
        response.setSpecialization(trainer.getSpecialization());
        response.setBio(trainer.getBio());
        response.setIsActive(trainer.getIsActive());
        response.setMaxClients(trainer.getMaxClients());
        response.setCurrentClients(currentClients);
        return response;
    }
}