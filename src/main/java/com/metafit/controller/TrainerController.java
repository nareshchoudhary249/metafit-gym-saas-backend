package com.metafit.controller;

import com.metafit.dto.request.trainer.AssignMemberToTrainerRequest;
import com.metafit.dto.request.trainer.CreateTrainerRequest;
import com.metafit.dto.response.trainer.TrainerResponse;
import com.metafit.dto.request.trainer.UpdateTrainerNotesRequest;
import com.metafit.dto.response.member.MemberResponse;
import com.metafit.service.TrainerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

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

        String username = getCurrentUsername();
        TrainerResponse response = trainerService.createTrainer(request, username);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get all active trainers
     * GET /api/trainers
     */
    @GetMapping
    public ResponseEntity<List<TrainerResponse>> getAllTrainers() {
        log.info("GET /api/trainers - Fetching all trainers");

        List<TrainerResponse> trainers = trainerService.getActiveTrainers();

        return ResponseEntity.ok(trainers);
    }

    /**
     * Assign member to trainer
     * POST /api/trainers/assign
     */
    @PostMapping("/assign")
    public ResponseEntity<Void> assignMemberToTrainer(
            @Valid @RequestBody AssignMemberToTrainerRequest request) {

        log.info("POST /api/trainers/assign - Assigning member {} to trainer {}",
                request.getMemberId(), request.getTrainerId());

        trainerService.assignMemberToTrainer(request);

        return ResponseEntity.noContent().build();
    }

    /**
     * Unassign member from trainer
     * DELETE /api/trainers/assign/{memberId}
     */
    @DeleteMapping("/assign/{memberId}")
    public ResponseEntity<Void> unassignMemberFromTrainer(@PathVariable Long memberId) {
        log.info("DELETE /api/trainers/assign/{} - Unassigning member", memberId);

        trainerService.unassignMemberFromTrainer(memberId);

        return ResponseEntity.noContent().build();
    }

    /**
     * Get all members assigned to a trainer
     * GET /api/trainers/{trainerId}/members
     */
    @GetMapping("/{trainerId}/members")
    public ResponseEntity<List<MemberResponse>> getTrainerMembers(
            @PathVariable Long trainerId) {

        log.info("GET /api/trainers/{}/members", trainerId);

        List<MemberResponse> members = trainerService.getTrainerMembers(trainerId);

        return ResponseEntity.ok(members);
    }

    /**
     * Update trainer notes for a member
     * PUT /api/trainers/members/{memberId}/notes
     */
    @PutMapping("/members/{memberId}/notes")
    public ResponseEntity<Void> updateTrainerNotes(
            @PathVariable Long memberId,
            @Valid @RequestBody UpdateTrainerNotesRequest request) {

        log.info("PUT /api/trainers/members/{}/notes", memberId);

        trainerService.updateMemberNotes(memberId, request, request.getTrainerId());

        return ResponseEntity.noContent().build();
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : "system";
    }
}
