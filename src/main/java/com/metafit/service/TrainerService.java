package com.metafit.service;

import com.metafit.dto.request.trainer.AssignMemberToTrainerRequest;
import com.metafit.dto.request.trainer.CreateTrainerRequest;
import com.metafit.dto.request.trainer.UpdateTrainerNotesRequest;
import com.metafit.dto.response.member.MemberResponse;
import com.metafit.dto.response.trainer.TrainerDetailResponse;
import com.metafit.dto.response.trainer.TrainerResponse;

import java.util.List;

/**
 * Trainer Service Interface
 */
public interface TrainerService {

    TrainerResponse createTrainer(CreateTrainerRequest request, String createdBy);

    TrainerDetailResponse getTrainerById(Long id);

    List<TrainerResponse> getAllTrainers();

    List<TrainerResponse> getActiveTrainers();

    TrainerResponse updateTrainer(Long id, CreateTrainerRequest request, String updatedBy);

    void updateTrainerStatus(Long id, boolean active);

    void assignMemberToTrainer(AssignMemberToTrainerRequest request);

    void unassignMemberFromTrainer(Long memberId);

    List<MemberResponse> getTrainerMembers(Long trainerId);

    void updateMemberNotes(Long memberId, UpdateTrainerNotesRequest request, Long trainerId);

    Long getAssignedMemberCount(Long trainerId);

    boolean isTrainerAtCapacity(Long trainerId);

    void deleteTrainer(Long id);
}
