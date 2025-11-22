package com.metafit.service.impl;

import com.metafit.dto.request.trainer.AssignMemberToTrainerRequest;
import com.metafit.dto.request.trainer.CreateTrainerRequest;
import com.metafit.dto.response.trainer.MemberWithTrainerResponse;
import com.metafit.dto.response.trainer.TrainerResponse;
import com.metafit.entity.Member;
import com.metafit.entity.Trainer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TrainerServiceImpl {

    private final TrainerRepository trainerRepository;
    private final MemberRepository memberRepository;

    /**
     * Create new trainer
     */
    public TrainerResponse createTrainer(CreateTrainerRequest request) {
        log.info("Creating new trainer: {}", request.getFullName());

        Trainer trainer = new Trainer();
        trainer.setFullName(request.getFullName());
        trainer.setPhone(request.getPhone());
        trainer.setEmail(request.getEmail());
        trainer.setSpecialization(request.getSpecialization());
        trainer.setBio(request.getBio());
        trainer.setMaxClients(request.getMaxClients() != null ? request.getMaxClients() : 20);
        trainer.setIsActive(true);

        Trainer saved = trainerRepository.save(trainer);

        log.info("Trainer created: {}", saved.getId());

        int currentClients = memberRepository.countByAssignedTrainerId(saved.getId());
        return TrainerResponse.fromEntity(saved, currentClients);
    }

    /**
     * Get all active trainers
     */
    @Transactional(readOnly = true)
    public List<TrainerResponse> getAllActiveTrainers() {
        log.debug("Fetching all active trainers");

        List<Trainer> trainers = trainerRepository.findByIsActiveTrue();

        return trainers.stream()
                .map(trainer -> {
                    int currentClients = memberRepository.countByAssignedTrainerId(trainer.getId());
                    return TrainerResponse.fromEntity(trainer, currentClients);
                })
                .collect(Collectors.toList());
    }

    /**
     * Assign member to trainer
     */
    public MemberWithTrainerResponse assignMemberToTrainer(AssignMemberToTrainerRequest request) {
        log.info("Assigning member {} to trainer {}", request.getMemberId(), request.getTrainerId());

        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        Trainer trainer = trainerRepository.findById(request.getTrainerId())
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found"));

        if (!trainer.getIsActive()) {
            throw new IllegalArgumentException("Cannot assign to inactive trainer");
        }

        // Check if trainer has capacity
        int currentClients = memberRepository.countByAssignedTrainerId(trainer.getId());
        if (currentClients >= trainer.getMaxClients()) {
            throw new IllegalArgumentException(
                    "Trainer has reached maximum capacity (" + trainer.getMaxClients() + " clients)"
            );
        }

        member.setAssignedTrainer(trainer);
        if (request.getNotes() != null) {
            member.setTrainerNotes(request.getNotes());
        }

        Member updated = memberRepository.save(member);

        log.info("Member assigned successfully");

        return buildMemberWithTrainerResponse(updated);
    }

    /**
     * Unassign member from trainer
     */
    public void unassignMemberFromTrainer(UUID memberId) {
        log.info("Unassigning member {} from trainer", memberId);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        member.setAssignedTrainer(null);
        member.setTrainerNotes(null);

        memberRepository.save(member);

        log.info("Member unassigned successfully");
    }

    /**
     * Get all members assigned to a trainer
     */
    @Transactional(readOnly = true)
    public List<MemberWithTrainerResponse> getTrainerMembers(UUID trainerId) {
        log.debug("Fetching members for trainer: {}", trainerId);

        List<Member> members = memberRepository.findByAssignedTrainerId(trainerId);

        return members.stream()
                .map(this::buildMemberWithTrainerResponse)
                .collect(Collectors.toList());
    }

    /**
     * Update trainer notes for a member
     */
    public void updateTrainerNotes(UUID memberId, String notes, String trainerUsername) {
        log.info("Updating trainer notes for member: {}", memberId);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        // In real app, verify trainer is assigned to this member
        member.setTrainerNotes(notes);
        memberRepository.save(member);

        log.info("Trainer notes updated");
    }

    private MemberWithTrainerResponse buildMemberWithTrainerResponse(Member member) {
        MemberWithTrainerResponse response = new MemberWithTrainerResponse();
        response.setId(member.getId());
        response.setFullName(member.getFullName());
        response.setPhone(member.getPhone());
        response.setStatus(member.getStatus().name());
        response.setTrainerNotes(member.getTrainerNotes());

        if (member.getAssignedTrainer() != null) {
            Trainer trainer = member.getAssignedTrainer();
            MemberWithTrainerResponse.TrainerInfo trainerInfo =
                    new MemberWithTrainerResponse.TrainerInfo();
            trainerInfo.setId(trainer.getId());
            trainerInfo.setName(trainer.getFullName());
            trainerInfo.setPhone(trainer.getPhone());
            trainerInfo.setSpecialization(trainer.getSpecialization());
            response.setAssignedTrainer(trainerInfo);
        }

        return response;
    }
}