package com.metafit.dto.response.member;

import com.metafit.entity.Member;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MemberDetailResponse {
    private Long id;
    private String name;
    private String phone;
    private String email;
    private Member.Gender gender;
    private LocalDate dateOfBirth;
    private Integer age;
    private String address;
    private String emergencyContact;
    private String emergencyContactName;
    private Member.MemberStatus status;
    private LocalDate membershipStartDate;
    private LocalDate membershipEndDate;
    private Integer daysRemaining;
    private String membershipPlan;
    private Double membershipAmount;
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
