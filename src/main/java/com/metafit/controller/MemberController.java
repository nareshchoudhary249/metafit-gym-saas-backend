package com.metafit.controller;


import com.metafit.dto.request.member.CreateMemberRequest;
import com.metafit.dto.request.member.RenewMembershipRequest;
import com.metafit.dto.request.member.UpdateMemberRequest;
import com.metafit.dto.response.member.MemberDetailResponse;
import com.metafit.dto.response.member.MemberResponse;
import com.metafit.enums.MemberStatus;
import com.metafit.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${app.cors.allowed-origins}")
public class MemberController {

    private final MemberService memberService;

    /**
     * Create a new member
     * POST /api/members
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'RECEPTION')")
    public ResponseEntity<MemberResponse> createMember(
            @Valid @RequestBody CreateMemberRequest request,
            Authentication authentication) {

        log.info("Creating new member: {}", request.getFullName());
        String username = authentication.getName();
        MemberResponse response = memberService.createMember(request, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get all members with pagination
     * GET /api/members?page=0&size=10
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'RECEPTION', 'TRAINER')")
    public ResponseEntity<Page<MemberResponse>> getAllMembers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.debug("Fetching members - page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<MemberResponse> members = memberService.getAllMembers(pageable);
        return ResponseEntity.ok(members);
    }

    /**
     * Get member by ID
     * GET /api/members/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'RECEPTION', 'TRAINER')")
    public ResponseEntity<MemberDetailResponse> getMemberById(@PathVariable Long id) {
        log.debug("Fetching member with ID: {}", id);
        MemberDetailResponse response = memberService.getMemberById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Search members by name or phone
     * GET /api/members/search?q=john
     */
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'RECEPTION', 'TRAINER')")
    public ResponseEntity<List<MemberResponse>> searchMembers(
            @RequestParam("q") String query) {

        log.debug("Searching members with query: {}", query);
        List<MemberResponse> members = memberService.searchMembers(query);
        return ResponseEntity.ok(members);
    }

    /**
     * Get expiring members (within 7 days)
     * GET /api/members/expiring
     */
    @GetMapping("/expiring")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'RECEPTION')")
    public ResponseEntity<List<MemberResponse>> getExpiringMembers() {
        log.debug("Fetching expiring members");
        List<MemberResponse> members = memberService.getExpiringMembers();
        return ResponseEntity.ok(members);
    }

    /**
     * Get expired members
     * GET /api/members/expired
     */
    @GetMapping("/expired")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<List<MemberResponse>> getExpiredMembers() {
        log.debug("Fetching expired members");
        List<MemberResponse> members = memberService.getExpiredMembers();
        return ResponseEntity.ok(members);
    }

    /**
     * Get active member count
     * GET /api/members/count/active
     */
    @GetMapping("/count/active")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'RECEPTION')")
    public ResponseEntity<Map<String, Long>> getActiveMemberCount() {
        log.debug("Fetching active member count");
        long count = memberService.getActiveMemberCount();
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    /**
     * Update member
     * PUT /api/members/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'RECEPTION')")
    public ResponseEntity<MemberResponse> updateMember(
            @PathVariable Long id,
            @Valid @RequestBody UpdateMemberRequest request,
            Authentication authentication) {

        log.info("Updating member with ID: {}", id);
        String username = authentication.getName();
        MemberResponse response = memberService.updateMember(id, request, username);
        return ResponseEntity.ok(response);
    }

    /**
     * Renew membership
     * POST /api/members/renew
     */
    @PostMapping("/renew")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'RECEPTION')")
    public ResponseEntity<MemberResponse> renewMembership(
            @Valid @RequestBody RenewMembershipRequest request,
            Authentication authentication) {

        log.info("Renewing membership for member ID: {}", request.getMemberId());
        String username = authentication.getName();
        MemberResponse response = memberService.renewMembership(request, username);
        return ResponseEntity.ok(response);
    }

    /**
     * Update member status
     * PATCH /api/members/{id}/status
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<Map<String, String>> updateMemberStatus(
            @PathVariable Long id,
            @RequestParam MemberStatus status) {

        log.info("Updating status for member ID: {} to {}", id, status);
        memberService.updateMemberStatus(id, status);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Member status updated successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * Delete member (soft delete)
     * DELETE /api/members/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Map<String, String>> deleteMember(@PathVariable Long id) {
        log.info("Deleting member with ID: {}", id);
        memberService.deleteMember(id);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Member deleted successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * Get members by status with pagination
     * GET /api/members/status/{status}?page=0&size=10
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'RECEPTION')")
    public ResponseEntity<Page<MemberResponse>> getMembersByStatus(
            @PathVariable MemberStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.debug("Fetching members with status: {}", status);
        Pageable pageable = PageRequest.of(page, size);
        Page<MemberResponse> members = memberService.getMembersByStatus(status, pageable);
        return ResponseEntity.ok(members);
    }
}