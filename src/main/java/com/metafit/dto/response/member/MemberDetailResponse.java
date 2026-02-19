package com.metafit.dto.response.member;

import com.metafit.enums.Gender;
import com.metafit.enums.MemberStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDetailResponse {
    private Long id;
    private String name;
    private String phone;
    private String email;
    private Gender gender;
    private LocalDate dateOfBirth;
    private Integer age;
    private String address;
    private String emergencyContact;
    private String emergencyContactName;
    private MemberStatus status;
    private LocalDate membershipStartDate;
    private LocalDate membershipEndDate;
    private Integer daysRemaining;
    private String membershipPlan;
    private BigDecimal membershipAmount;
    private Long assignedTrainerId;
    private String assignedTrainerName;
    private String trainerNotes;
    private String notes;
    private boolean expiringSoon;
    private boolean expired;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    // Statistics (to be populated by service)
    private Long totalAttendance;
    private Long attendanceThisMonth;
    private Double totalPayments;
    private LocalDateTime lastCheckIn;
}
