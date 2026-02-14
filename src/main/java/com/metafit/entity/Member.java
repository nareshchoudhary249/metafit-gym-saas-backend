package com.metafit.entity;

import com.metafit.enums.Gender;
import com.metafit.enums.MemberStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "members")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String fullName;

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

    // Photo URL (optional)
    @Column(name = "photo_url", length = 500)
    private String photoUrl;

    @Column(name = "membership_plan", length = 50)
    private String membershipPlan;

    @Column(name = "membership_amount")
    private BigDecimal membershipAmount;

    @Column(name = "assigned_trainer_id")
    private Long assignedTrainerId;

    @Column(name = "assigned_trainer")
    private Long assignedTrainer;

    @Column(name = "join_date")
    private LocalDate joinDate;

    @Column(name = "blood_group")
    private String bloodGroup;

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

    public boolean isActive() {
        return this.status == MemberStatus.ACTIVE;
    }

    // Helper method to check if membership is expiring soon (within 7 days)
    public boolean isExpiringSoon() {
        if (membershipEndDate == null) return false;
        LocalDate sevenDaysFromNow = LocalDate.now().plusDays(7);
        return membershipEndDate.isAfter(LocalDate.now()) &&
                membershipEndDate.isBefore(sevenDaysFromNow);
    }

    public boolean isExpiringLittle() {
        // Critical: expires within 3 days
        return getDaysUntilExpiry() <= 3 && getDaysUntilExpiry() >= 0;
    }

    // Helper method to check if membership has expired
    public boolean isExpired() {
        if (membershipEndDate == null) return false;
        return membershipEndDate.isBefore(LocalDate.now());
    }

    public long getDaysUntilExpiry() {
        return java.time.temporal.ChronoUnit.DAYS.between(
                LocalDate.now(),
                this.membershipEndDate
        );
    }

    public boolean isExpiringSoon(int days) {
        return getDaysUntilExpiry() <= days && getDaysUntilExpiry() >= 0;
    }

    public boolean isMembershipExpired() {
        return LocalDate.now().isAfter(membershipEndDate);
    }
}

