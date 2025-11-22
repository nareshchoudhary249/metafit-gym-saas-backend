package com.metafit.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "members")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class oMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 15)
    private String phone;

    @Column(unique = true, length = 100)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Gender gender;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(length = 500)
    private String address;

    @Column(name = "emergency_contact", length = 15)
    private String emergencyContact;

    @Column(name = "emergency_contact_name", length = 100)
    private String emergencyContactName;

    @Column(name = "membership_start_date", nullable = false)
    private LocalDate membershipStartDate;

    @Column(name = "membership_end_date", nullable = false)
    private LocalDate membershipEndDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MemberStatus status;

    @Column(name = "membership_plan", length = 50)
    private String membershipPlan;

    @Column(name = "membership_amount")
    private Double membershipAmount;

    @Column(name = "assigned_trainer_id")
    private Long assignedTrainerId;

    @Column(name = "trainer_notes", length = 1000)
    private String trainerNotes;

    @Column(length = 500)
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = MemberStatus.ACTIVE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Helper method to check if membership is expiring soon (within 7 days)
    public boolean isExpiringSoon() {
        if (membershipEndDate == null) return false;
        LocalDate sevenDaysFromNow = LocalDate.now().plusDays(7);
        return membershipEndDate.isAfter(LocalDate.now()) &&
                membershipEndDate.isBefore(sevenDaysFromNow);
    }

    // Helper method to check if membership has expired
    public boolean isExpired() {
        if (membershipEndDate == null) return false;
        return membershipEndDate.isBefore(LocalDate.now());
    }

    // Gender Enum
    public enum Gender {
        MALE,
        FEMALE,
        OTHER
    }

    // MemberStatus Enum
   public enum MemberStatus {
        ACTIVE,
        EXPIRED,
        SUSPENDED,
        CANCELLED
    }
}

