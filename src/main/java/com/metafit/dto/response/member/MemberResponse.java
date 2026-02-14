package com.metafit.dto.response.member;

import com.metafit.entity.Member;
import com.metafit.enums.Gender;
import com.metafit.enums.MemberStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class MemberResponse {

    private Long id;
    private String fullName;
    private String phone;
    private String email;
    private Gender gender;
    private LocalDate dateOfBirth;
    private LocalDate joinDate;
    private LocalDate membershipStartDate;
    private LocalDate membershipEndDate;
    private String membershipPlan;
    private MemberStatus status;
    private String statusDisplay;
    private String address;
    private String emergencyContact;
    private String bloodGroup;
    private String notes;
    private Long assignedTrainerId;
    private String assignedTrainerName;
    private Boolean isExpired;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer daysUntilExpiry;  // Exact days remaining
    private Boolean isExpiringSoon; // ⚠️ Yellow warning (7 days)
    private Boolean isExpiringLittle;  // 🚨 Red alert (3 days)

    // Photo
    private String photoUrl;
    private Boolean isActive;


    public static MemberResponse fromEntity(Member member) {
        MemberResponse response = new MemberResponse();
        response.setId(member.getId());
        response.setFullName(member.getFullName());
        response.setPhone(member.getPhone());
        response.setEmail(member.getEmail());
        response.setGender(member.getGender());
        response.setDateOfBirth(member.getDateOfBirth());
        response.setJoinDate(member.getJoinDate());
        response.setMembershipStartDate(member.getMembershipStartDate());
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