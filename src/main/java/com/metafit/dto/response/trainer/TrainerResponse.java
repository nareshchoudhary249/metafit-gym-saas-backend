package com.metafit.dto.response.trainer;

import com.metafit.entity.Trainer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainerResponse {
    private Long id;
    private String fullName;
    private String phone;
    private String email;
    private String specialization;
    private String bio;
    private Boolean active;
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
        response.setActive(trainer.getActive());
        response.setMaxClients(trainer.getMaxClients());
        response.setCurrentClients(currentClients);
        return response;
    }
}
