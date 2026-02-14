package com.metafit.controller;

import com.metafit.dto.request.trainer.AssignMemberToTrainerRequest;
import com.metafit.dto.request.trainer.CreateTrainerRequest;
import com.metafit.dto.response.trainer.MemberWithTrainerResponse;
import com.metafit.dto.response.trainer.TrainerResponse;
import com.metafit.service.TrainerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

/**
 * REST controller for trainer management
 */
@Slf4j
@RestController
@RequestMapping("/api/trainers")
@RequiredArgsConstructor
public class TrainerController {

    private final TrainerService trainerService;

    /**
     * Create new trainer (Owner/Admin only)
     * POST /api/trainers
     */
    @PostMapping
    public ResponseEntity<TrainerResponse> createTrainer(
            @Valid @RequestBody CreateTrainerRequest request) {

        log.info("POST /api/trainers - Creating trainer: {}", request.getFullName());

        TrainerResponse response = trainerService.createTrainer(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get all active trainers
     * GET /api/trainers
     */
    @GetMapping
    public ResponseEntity<List<TrainerResponse>> getAllTrainers() {
        log.info("GET /api/trainers - Fetching all trainers");

        List<TrainerResponse> trainers = trainerService.getAllActiveTrainers();

        return ResponseEntity.ok(trainers);
    }

    /**
     * Assign member to trainer
     * POST /api/trainers/assign
     */
    @PostMapping("/assign")
    public ResponseEntity<MemberWithTrainerResponse> assignMemberToTrainer(
            @Valid @RequestBody AssignMemberToTrainerRequest request) {

        log.info("POST /api/trainers/assign - Assigning member {} to trainer {}",
                request.getMemberId(), request.getTrainerId());

        MemberWithTrainerResponse response = trainerService.assignMemberToTrainer(request);

        return ResponseEntity.ok(response);
    }

    /**
     * Unassign member from trainer
     * DELETE /api/trainers/assign/{memberId}
     */
    @DeleteMapping("/assign/{memberId}")
    public ResponseEntity<Void> unassignMemberFromTrainer(@PathVariable UUID memberId) {
        log.info("DELETE /api/trainers/assign/{} - Unassigning member", memberId);

        trainerService.unassignMemberFromTrainer(memberId);

        return ResponseEntity.noContent().build();
    }

    /**
     * Get all members assigned to a trainer
     * GET /api/trainers/{trainerId}/members
     */
    @GetMapping("/{trainerId}/members")
    public ResponseEntity<List<MemberWithTrainerResponse>> getTrainerMembers(
            @PathVariable UUID trainerId) {

        log.info("GET /api/trainers/{}/members", trainerId);

        List<MemberWithTrainerResponse> members = trainerService.getTrainerMembers(trainerId);

        return ResponseEntity.ok(members);
    }

    /**
     * Update trainer notes for a member
     * PUT /api/trainers/members/{memberId}/notes
     */
    @PutMapping("/members/{memberId}/notes")
    public ResponseEntity<Void> updateTrainerNotes(
            @PathVariable UUID memberId,
            @RequestBody String notes,
            Authentication authentication) {

        log.info("PUT /api/trainers/members/{}/notes", memberId);

        String trainerUsername = authentication.getName();
        trainerService.updateTrainerNotes(memberId, notes, trainerUsername);

        return ResponseEntity.noContent().build();
    }
}