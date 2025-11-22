package com.metafit.dto.response.member;

import com.metafit.entity.Member;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class MemberResponse {

    private UUID id;
    private String fullName;
    private String phone;
    private String email;
    private Member.Gender gender;
    private LocalDate dateOfBirth;
    private LocalDate joinDate;
    private LocalDate membershipEndDate;
    private Member.MembershipStatus status;
    private String address;
    private String emergencyContact;
    private String bloodGroup;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer daysUntilExpiry;
    private Boolean isExpiringSoon;

    public static MemberResponse fromEntity(Member member) {
        MemberResponse response = new MemberResponse();
        response.setId(member.getId());
        response.setFullName(member.getFullName());
        response.setPhone(member.getPhone());
        response.setEmail(member.getEmail());
        response.setGender(member.getGender());
        response.setDateOfBirth(member.getDateOfBirth());
        response.setJoinDate(member.getJoinDate());
        response.setMembershipEndDate(member.getMembershipEndDate());
        response.setStatus(member.getStatus());
        response.setAddress(member.getAddress());
        response.setEmergencyContact(member.getEmergencyContact());
        response.setBloodGroup(member.getBloodGroup());
        response.setNotes(member.getNotes());
        response.setCreatedAt(member.getCreatedAt());
        response.setUpdatedAt(member.getUpdatedAt());

        // Calculate days until expiry
        if (member.getMembershipEndDate() != null) {
            LocalDate today = LocalDate.now();
            long days = java.time.temporal.ChronoUnit.DAYS.between(today, member.getMembershipEndDate());
            response.setDaysUntilExpiry((int) days);
            response.setIsExpiringLittle((days > 0 && days <= 7));
        }

        return response;
    }
}