package com.metafit.service.impl;

import com.metafit.dto.request.CreateMemberRequest;
import com.metafit.dto.request.RenewMembershipRequest;
import com.metafit.dto.request.UpdateMemberRequest;
import com.metafit.dto.response.MemberDetailResponse;
import com.metafit.dto.response.MemberResponse;
import com.metafit.entity.Member;
import com.metafit.entity.MemberStatus;
import com.metafit.exception.DuplicateResourceException;
import com.metafit.exception.ResourceNotFoundException;
import com.metafit.repository.AttendanceRepository;
import com.metafit.repository.MemberRepository;
import com.metafit.repository.PaymentRepository;
import com.metafit.repository.TrainerRepository;
import com.metafit.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final AttendanceRepository attendanceRepository;
    private final PaymentRepository paymentRepository;
    private final TrainerRepository trainerRepository;

    @Override
    @Transactional
    public MemberResponse createMember(CreateMemberRequest request, String createdBy) {
        log.info("Creating new member: {}", request.getName());

        // Check for duplicate phone
        if (memberRepository.existsByPhone(request.getPhone())) {
            throw new DuplicateResourceException("Phone number already exists: " + request.getPhone());
        }

        // Check for duplicate email if provided
        if (request.getEmail() != null && !request.getEmail().isEmpty()
                && memberRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists: " + request.getEmail());
        }

        // Create member entity
        Member member = Member.builder()
                .name(request.getName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .gender(request.getGender())
                .dateOfBirth(request.getDateOfBirth())
                .address(request.getAddress())
                .emergencyContact(request.getEmergencyContact())
                .emergencyContactName(request.getEmergencyContactName())
                .membershipStartDate(request.getMembershipStartDate())
                .membershipEndDate(request.getMembershipEndDate())
                .membershipPlan(request.getMembershipPlan())
                .membershipAmount(request.getMembershipAmount())
                .status(MemberStatus.ACTIVE)
                .notes(request.getNotes())
                .createdBy(createdBy)
                .build();

        member = memberRepository.save(member);
        log.info("Member created successfully with ID: {}", member.getId());

        return convertToResponse(member);
    }

    @Override
    @Transactional(readOnly = true)
    public MemberDetailResponse getMemberById(Long id) {
        log.debug("Fetching member details for ID: {}", id);

        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with ID: " + id));

        return convertToDetailResponse(member);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MemberResponse> getAllMembers(Pageable pageable) {
        log.debug("Fetching all members with pagination");
        return memberRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MemberResponse> getMembersByStatus(MemberStatus status, Pageable pageable) {
        log.debug("Fetching members with status: {}", status);
        return memberRepository.findByStatusOrderByCreatedAtDesc(status, pageable)
                .map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MemberResponse> searchMembers(String query) {
        log.debug("Searching members with query: {}", query);
        return memberRepository.searchByNameOrPhone(query).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MemberResponse> getExpiringMembers() {
        log.debug("Fetching expiring members");
        LocalDate today = LocalDate.now();
        LocalDate sevenDaysFromNow = today.plusDays(7);

        return memberRepository.findExpiringMembers(today, sevenDaysFromNow).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MemberResponse> getExpiredMembers() {
        log.debug("Fetching expired members");
        return memberRepository.findExpiredMembers(LocalDate.now()).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MemberResponse updateMember(Long id, UpdateMemberRequest request, String updatedBy) {
        log.info("Updating member with ID: {}", id);

        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with ID: " + id));

        // Update fields if provided
        if (request.getName() != null) member.setName(request.getName());
        if (request.getPhone() != null) {
            // Check if new phone is different and not already taken
            if (!member.getPhone().equals(request.getPhone())
                    && memberRepository.existsByPhone(request.getPhone())) {
                throw new DuplicateResourceException("Phone number already exists: " + request.getPhone());
            }
            member.setPhone(request.getPhone());
        }
        if (request.getEmail() != null) {
            // Check if new email is different and not already taken
            if (!request.getEmail().equals(member.getEmail())
                    && memberRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateResourceException("Email already exists: " + request.getEmail());
            }
            member.setEmail(request.getEmail());
        }
        if (request.getGender() != null) member.setGender(request.getGender());
        if (request.getDateOfBirth() != null) member.setDateOfBirth(request.getDateOfBirth());
        if (request.getAddress() != null) member.setAddress(request.getAddress());
        if (request.getEmergencyContact() != null) member.setEmergencyContact(request.getEmergencyContact());
        if (request.getEmergencyContactName() != null) member.setEmergencyContactName(request.getEmergencyContactName());
        if (request.getStatus() != null) member.setStatus(request.getStatus());
        if (request.getNotes() != null) member.setNotes(request.getNotes());

        member.setUpdatedBy(updatedBy);
        member = memberRepository.save(member);

        log.info("Member updated successfully: {}", id);
        return convertToResponse(member);
    }

    @Override
    @Transactional
    public MemberResponse renewMembership(RenewMembershipRequest request, String renewedBy) {
        log.info("Renewing membership for member ID: {}", request.getMemberId());

        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with ID: " + request.getMemberId()));

        // Update membership details
        member.setMembershipEndDate(request.getNewEndDate());
        member.setMembershipPlan(request.getMembershipPlan());
        member.setMembershipAmount(request.getAmount());
        member.setStatus(MemberStatus.ACTIVE);
        member.setUpdatedBy(renewedBy);

        member = memberRepository.save(member);
        log.info("Membership renewed successfully for member ID: {}", request.getMemberId());

        return convertToResponse(member);
    }

    @Override
    @Transactional
    public void deleteMember(Long id) {
        log.info("Deleting member with ID: {}", id);

        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with ID: " + id));

        // Soft delete by changing status
        member.setStatus(MemberStatus.CANCELLED);
        memberRepository.save(member);

        log.info("Member soft deleted: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public long getActiveMemberCount() {
        return memberRepository.countByStatus(MemberStatus.ACTIVE);
    }

    @Override
    @Transactional
    public void updateMemberStatus(Long id, MemberStatus status) {
        log.info("Updating status for member ID: {} to {}", id, status);

        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with ID: " + id));

        member.setStatus(status);
        memberRepository.save(member);
    }

    @Override
    @Transactional
    public void checkAndUpdateExpiredMemberships() {
        log.info("Checking and updating expired memberships");

        List<Member> expiredMembers = memberRepository.findExpiredMembers(LocalDate.now());

        for (Member member : expiredMembers) {
            member.setStatus(MemberStatus.EXPIRED);
            memberRepository.save(member);
            log.debug("Updated member {} to EXPIRED status", member.getId());
        }

        log.info("Updated {} expired memberships", expiredMembers.size());
    }

    // ============ Helper Methods ============

    private MemberResponse convertToResponse(Member member) {
        String trainerName = null;
        if (member.getAssignedTrainerId() != null) {
            trainerName = trainerRepository.findById(member.getAssignedTrainerId())
                    .map(trainer -> trainer.getName())
                    .orElse(null);
        }

        return MemberResponse.builder()
                .id(member.getId())
                .name(member.getName())
                .phone(member.getPhone())
                .email(member.getEmail())
                .gender(member.getGender())
                .status(member.getStatus())
                .membershipStartDate(member.getMembershipStartDate())
                .membershipEndDate(member.getMembershipEndDate())
                .membershipPlan(member.getMembershipPlan())
                .assignedTrainerId(member.getAssignedTrainerId())
                .assignedTrainerName(trainerName)
                .expiringSoon(member.isExpiringSoon())
                .expired(member.isExpired())
                .createdAt(member.getCreatedAt())
                .build();
    }

    private MemberDetailResponse convertToDetailResponse(Member member) {
        String trainerName = null;
        if (member.getAssignedTrainerId() != null) {
            trainerName = trainerRepository.findById(member.getAssignedTrainerId())
                    .map(trainer -> trainer.getName())
                    .orElse(null);
        }

        // Calculate age
        Integer age = null;
        if (member.getDateOfBirth() != null) {
            age = Period.between(member.getDateOfBirth(), LocalDate.now()).getYears();
        }

        // Calculate days remaining
        Integer daysRemaining = null;
        if (member.getMembershipEndDate() != null) {
            daysRemaining = (int) ChronoUnit.DAYS.between(LocalDate.now(), member.getMembershipEndDate());
        }

        // Get statistics
        Long totalAttendance = attendanceRepository.countByMemberId(member.getId());
        Long attendanceThisMonth = attendanceRepository.countByMemberIdAndCheckInTimeBetween(
                member.getId(),
                LocalDate.now().withDayOfMonth(1).atStartOfDay(),
                LocalDate.now().plusDays(1).atStartOfDay()
        );
        Double totalPayments = paymentRepository.sumAmountByMemberId(member.getId());

        return MemberDetailResponse.builder()
                .id(member.getId())
                .name(member.getName())
                .phone(member.getPhone())
                .email(member.getEmail())
                .gender(member.getGender())
                .dateOfBirth(member.getDateOfBirth())
                .age(age)
                .address(member.getAddress())
                .emergencyContact(member.getEmergencyContact())
                .emergencyContactName(member.getEmergencyContactName())
                .status(member.getStatus())
                .membershipStartDate(member.getMembershipStartDate())
                .membershipEndDate(member.getMembershipEndDate())
                .daysRemaining(daysRemaining)
                .membershipPlan(member.getMembershipPlan())
                .membershipAmount(member.getMembershipAmount())
                .assignedTrainerId(member.getAssignedTrainerId())
                .assignedTrainerName(trainerName)
                .trainerNotes(member.getTrainerNotes())
                .notes(member.getNotes())
                .expiringSoon(member.isExpiringSoon())
                .expired(member.isExpired())
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .createdBy(member.getCreatedBy())
                .updatedBy(member.getUpdatedBy())
                .totalAttendance(totalAttendance)
                .attendanceThisMonth(attendanceThisMonth)
                .totalPayments(totalPayments != null ? totalPayments : 0.0)
                .lastCheckIn(attendanceRepository.findTopByMemberIdOrderByCheckInTimeDesc(member.getId())
                        .map(attendance -> attendance.getCheckInTime())
                        .orElse(null))
                .build();
    }
}