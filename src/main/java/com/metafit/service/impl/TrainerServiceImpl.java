package com.metafit.service.impl;


import com.metafit.dto.request.trainer.AssignMemberToTrainerRequest;
import com.metafit.dto.request.trainer.CreateTrainerRequest;
import com.metafit.dto.request.trainer.UpdateTrainerNotesRequest;
import com.metafit.dto.response.member.MemberResponse;
import com.metafit.dto.response.trainer.TrainerDetailResponse;
import com.metafit.dto.response.trainer.TrainerResponse;
import com.metafit.entity.Member;
import com.metafit.entity.Trainer;
import com.metafit.exception.DuplicateResourceException;
import com.metafit.exception.ResourceNotFoundException;
import com.metafit.repository.MemberRepository;
import com.metafit.repository.TrainerRepository;
import com.metafit.service.TrainerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Trainer Service Implementation
 * Implements all trainer-related business logic
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TrainerServiceImpl implements TrainerService {

    private final TrainerRepository trainerRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public TrainerResponse createTrainer(CreateTrainerRequest request, String createdBy) {
        log.info("Creating new trainer: {}", request.getFullName());

        // Check for duplicate email
        if (request.getEmail() != null && !request.getEmail().isEmpty()
                && trainerRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists: " + request.getEmail());
        }

        // Check for duplicate phone
        if (trainerRepository.existsByPhone(request.getPhone())) {
            throw new DuplicateResourceException("Phone number already exists: " + request.getPhone());
        }

        // Create trainer entity
        Trainer trainer = Trainer.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .specialization(request.getSpecialization())
                .bio(request.getBio())
                .maxClients(request.getMaxClients() != null ? request.getMaxClients() : 20)
                .active(true)
                .createdBy(createdBy)
                .build();

        trainer = trainerRepository.save(trainer);
        log.info("Trainer created successfully with ID: {}", trainer.getId());

        return convertToResponse(trainer);
    }

    @Override
    @Transactional(readOnly = true)
    public TrainerDetailResponse getTrainerById(Long id) {
        log.debug("Fetching trainer details for ID: {}", id);

        Trainer trainer = trainerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with ID: " + id));

        return convertToDetailResponse(trainer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainerResponse> getAllTrainers() {
        log.debug("Fetching all trainers");
        return trainerRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainerResponse> getActiveTrainers() {
        log.debug("Fetching active trainers");
        return trainerRepository.findByActiveTrue().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TrainerResponse updateTrainer(Long id, CreateTrainerRequest request, String updatedBy) {
        log.info("Updating trainer with ID: {}", id);

        Trainer trainer = trainerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with ID: " + id));

        // Update fields if provided
        if (request.getFullName() != null) {
            trainer.setFullName(request.getFullName());
        }

        if (request.getEmail() != null) {
            // Check if new email is different and not already taken
            if (!request.getEmail().equals(trainer.getEmail())
                    && trainerRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateResourceException("Email already exists: " + request.getEmail());
            }
            trainer.setEmail(request.getEmail());
        }

        if (request.getPhone() != null) {
            // Check if new phone is different and not already taken
            if (!request.getPhone().equals(trainer.getPhone())
                    && trainerRepository.existsByPhone(request.getPhone())) {
                throw new DuplicateResourceException("Phone number already exists: " + request.getPhone());
            }
            trainer.setPhone(request.getPhone());
        }

        if (request.getSpecialization() != null) {
            trainer.setSpecialization(request.getSpecialization());
        }

        if (request.getBio() != null) {
            trainer.setBio(request.getBio());
        }

        if (request.getMaxClients() != null) {
            trainer.setMaxClients(request.getMaxClients());
        }

        trainer = trainerRepository.save(trainer);
        log.info("Trainer updated successfully: {}", id);

        return convertToResponse(trainer);
    }

    @Override
    @Transactional
    public void updateTrainerStatus(Long id, boolean active) {
        log.info("Updating status for trainer ID: {} to {}", id, active);

        Trainer trainer = trainerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with ID: " + id));

        trainer.setActive(active);
        trainerRepository.save(trainer);
    }

    @Override
    @Transactional
    public void assignMemberToTrainer(AssignMemberToTrainerRequest request) {
        log.info("Assigning member {} to trainer {}", request.getMemberId(), request.getTrainerId());

        // Validate trainer exists and is active
        Trainer trainer = trainerRepository.findById(request.getTrainerId())
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with ID: " + request.getTrainerId()));

        if (!trainer.getActive()) {
            throw new IllegalStateException("Cannot assign to inactive trainer");
        }

        // Check if trainer has capacity
        if (isTrainerAtCapacity(request.getTrainerId())) {
            throw new IllegalStateException("Trainer has reached maximum capacity of " + trainer.getMaxClients() + " clients");
        }

        // Validate member exists
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with ID: " + request.getMemberId()));

        // Assign trainer to member
        member.setAssignedTrainerId(request.getTrainerId());
        memberRepository.save(member);

        log.info("Member {} successfully assigned to trainer {}", request.getMemberId(), request.getTrainerId());
    }

    @Override
    @Transactional
    public void unassignMemberFromTrainer(Long memberId) {
        log.info("Unassigning member {} from trainer", memberId);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with ID: " + memberId));

        member.setAssignedTrainerId(null);
        member.setTrainerNotes(null);
        memberRepository.save(member);

        log.info("Member {} unassigned from trainer", memberId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MemberResponse> getTrainerMembers(Long trainerId) {
        log.debug("Fetching members for trainer ID: {}", trainerId);

        // Validate trainer exists
        trainerRepository.findById(trainerId)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with ID: " + trainerId));

        return memberRepository.findByAssignedTrainerId(trainerId).stream()
                .map(this::convertMemberToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateMemberNotes(Long memberId, UpdateTrainerNotesRequest request, Long trainerId) {
        log.info("Updating notes for member {} by trainer {}", memberId, trainerId);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with ID: " + memberId));

        // Verify member is assigned to this trainer
        if (member.getAssignedTrainerId() == null || !member.getAssignedTrainerId().equals(trainerId)) {
            throw new IllegalStateException("Member is not assigned to this trainer");
        }

        member.setTrainerNotes(request.getNotes());
        memberRepository.save(member);

        log.info("Notes updated for member {}", memberId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getAssignedMemberCount(Long trainerId) {
        log.debug("Getting assigned member count for trainer ID: {}", trainerId);

        // Validate trainer exists
        trainerRepository.findById(trainerId)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with ID: " + trainerId));

        return memberRepository.countByAssignedTrainerId(trainerId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isTrainerAtCapacity(Long trainerId) {
        log.debug("Checking capacity for trainer ID: {}", trainerId);

        Trainer trainer = trainerRepository.findById(trainerId)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with ID: " + trainerId));

        long currentClients = memberRepository.countByAssignedTrainerId(trainerId);

        return currentClients >= trainer.getMaxClients();
    }

    @Override
    @Transactional
    public void deleteTrainer(Long id) {
        log.info("Deleting trainer with ID: {}", id);

        Trainer trainer = trainerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with ID: " + id));

        // Check if trainer has assigned members
        if (trainerRepository.hasAssignedMembers(id)) {
            throw new IllegalStateException("Cannot delete trainer with assigned members. Please unassign all members first.");
        }

        // Soft delete by setting active to false
        trainer.setActive(false);
        trainerRepository.save(trainer);

        log.info("Trainer soft deleted: {}", id);
    }

    // ============ Additional Helper Methods Using Repository Methods ============

    /**
     * Get trainers with available capacity
     */
    public List<TrainerResponse> getTrainersWithCapacity() {
        log.debug("Fetching trainers with available capacity");
        return trainerRepository.findTrainersWithCapacity().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get trainers at full capacity
     */
    public List<TrainerResponse> getTrainersAtCapacity() {
        log.debug("Fetching trainers at full capacity");
        return trainerRepository.findTrainersAtCapacity().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Search trainers by name
     */
    public List<TrainerResponse> searchTrainersByName(String query) {
        log.debug("Searching trainers by name: {}", query);
        return trainerRepository.searchByName(query).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get trainers by specialization
     */
    public List<TrainerResponse> getTrainersBySpecialization(String specialization) {
        log.debug("Fetching trainers by specialization: {}", specialization);
        return trainerRepository.findBySpecialization(specialization).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get active trainer count
     */
    public long getActiveTrainerCount() {
        return trainerRepository.countByActiveTrue();
    }

    // ============ Helper Methods for Conversion ============

    private TrainerResponse convertToResponse(Trainer trainer) {
        long assignedMembers = memberRepository.countByAssignedTrainerId(trainer.getId());

        return TrainerResponse.builder()
                .id(trainer.getId())
                .fullName(trainer.getFullName())
                .email(trainer.getEmail())
                .phone(trainer.getPhone())
                .specialization(trainer.getSpecialization())
                .bio(trainer.getBio())
                .maxClients(trainer.getMaxClients())
                .currentClients((int) assignedMembers)
                .active(trainer.getActive())
                .build();
    }

    private TrainerDetailResponse convertToDetailResponse(Trainer trainer) {
        long assignedMembers = memberRepository.countByAssignedTrainerId(trainer.getId());
        List<MemberResponse> members = getTrainerMembers(trainer.getId());

        return TrainerDetailResponse.builder()
                .id(trainer.getId())
                .fullName(trainer.getFullName())
                .email(trainer.getEmail())
                .phone(trainer.getPhone())
                .specialization(trainer.getSpecialization())
                .bio(trainer.getBio())
                .maxClients(trainer.getMaxClients())
                .currentClients((int) assignedMembers)
                .active(trainer.getActive())
                .hasCapacity(assignedMembers < trainer.getMaxClients())
                .assignedMembers(members)
                .createdAt(trainer.getCreatedAt())
                .updatedAt(trainer.getUpdatedAt())
                .createdBy(trainer.getCreatedBy())
                .build();
    }

    private MemberResponse convertMemberToResponse(Member member) {
        return MemberResponse.builder()
                .id(member.getId())
                .fullName(member.getFullName())
                .phone(member.getPhone())
                .email(member.getEmail())
                .gender(member.getGender())
                .status(member.getStatus())
                .membershipStartDate(member.getMembershipStartDate())
                .membershipEndDate(member.getMembershipEndDate())
                .membershipPlan(member.getMembershipPlan())
                .assignedTrainerId(member.getAssignedTrainerId())
                .isExpiringSoon(member.isExpiringSoon())
                .isExpired(member.isExpired())
                .createdAt(member.getCreatedAt())
                .build();
    }
}
