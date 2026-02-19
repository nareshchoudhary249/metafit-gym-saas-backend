package com.metafit.dto.response.trainer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberWithTrainerResponse {
    private Long id;
    private String fullName;
    private String phone;
    private String status;
    private TrainerInfo assignedTrainer;
    private String trainerNotes;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrainerInfo {
        private Long id;
        private String fullName;
        private String phone;
        private String specialization;
    }
}
