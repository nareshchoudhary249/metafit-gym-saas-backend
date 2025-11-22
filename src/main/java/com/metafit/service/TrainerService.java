package com.metafit.service;

import com.metafit.dto.request.UpdateTrainerNotesRequest;
import com.metafit.dto.request.trainer.AssignMemberToTrainerRequest;
import com.metafit.dto.request.trainer.CreateTrainerRequest;
import com.metafit.dto.response.TrainerDetailResponse;
import com.metafit.dto.response.trainer.TrainerResponse;

import java.util.List;

/**
 * Trainer Service Interface
 * Handles trainer management and member assignments
 */
public interface TrainerService {

    /**
     * Create a new trainer profile
     * @param request Trainer details (name, email, phone, specialization, bio)
     * @param createdBy Username of creator
     * @return Created trainer response
     */
    TrainerResponse createTrainer(CreateTrainerRequest request, String createdBy);

    /**
     * Get trainer by ID with full details
     * @param id Trainer ID
     * @return Trainer detail response with assigned members
     */
    TrainerDetailResponse getTrainerById(Long id);

    /**
     * Get all trainers
     * @return List of all trainers
     */
    List<TrainerResponse> getAllTrainers();

    /**
     * Get all active trainers
     * @return List of active trainers
     */
    List<TrainerResponse> getActiveTrainers();

    /**
     * Update trainer information
     * @param id Trainer ID
     * @param request Update details
     * @param updatedBy Username of updater
     * @return Updated trainer response
     */
    TrainerResponse updateTrainer(Long id, CreateTrainerRequest request, String updatedBy);

    /**
     * Activate or deactivate trainer
     * @param id Trainer ID
     * @param active New status
     */
    void updateTrainerStatus(Long id, boolean active);

    /**
     * Assign a member to a trainer
     * @param request Assignment details (memberId, trainerId)
     */
    void assignMemberToTrainer(AssignMemberToTrainerRequest request);

    /**
     * Unassign a member from their trainer
     * @param memberId Member ID
     */
    void unassignMemberFromTrainer(Long memberId);

    /**
     * Get all members assigned to a trainer
     * @param trainerId Trainer ID
     * @return List of assigned members
     */
    List<Object> getTrainerMembers(Long trainerId);

    /**
     * Update trainer's notes for a member
     * @param memberId Member ID
     * @param request Notes update request
     * @param trainerId Trainer ID making the update
     */
    void updateMemberNotes(Long memberId, UpdateTrainerNotesRequest request, Long trainerId);

    /**
     * Get count of members assigned to trainer
     * @param trainerId Trainer ID
     * @return Count of assigned members
     */
    Long getAssignedMemberCount(Long trainerId);

    /**
     * Check if trainer has reached max capacity
     * @param trainerId Trainer ID
     * @return true if at capacity, false otherwise
     */
    boolean isTrainerAtCapacity(Long trainerId);

    /**
     * Delete trainer (soft delete)
     * @param id Trainer ID
     */
    void deleteTrainer(Long id);
}