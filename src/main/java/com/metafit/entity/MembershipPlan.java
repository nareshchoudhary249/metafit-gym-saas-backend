package com.metafit.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Membership Plan Entity
 * Defines various membership packages offered by the gym
 */
@Entity
@Table(name = "membership_plans")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MembershipPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(name = "duration_months", nullable = false)
    private Integer durationMonths;

    @Column(nullable = false)
    private Double price;

    @Column(name = "discount_percentage")
    private Double discountPercentage;

    @Column(name = "discounted_price")
    private Double discountedPrice;

    @Column(nullable = false)
    private Boolean active;

    @Column(name = "features", columnDefinition = "TEXT")
    private String features; // JSON string or comma-separated features

    @Column(name = "max_freeze_days")
    private Integer maxFreezeDays;

    @Column(name = "personal_training_sessions")
    private Integer personalTrainingSessions;

    @Column(name = "diet_plan_included")
    private Boolean dietPlanIncluded;

    @Column(name = "display_order")
    private Integer displayOrder;

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

        if (active == null) {
            active = true;
        }

        if (dietPlanIncluded == null) {
            dietPlanIncluded = false;
        }

        // Calculate discounted price if discount is provided
        if (discountPercentage != null && discountPercentage > 0) {
            discountedPrice = price - (price * discountPercentage / 100);
        } else {
            discountedPrice = price;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();

        // Recalculate discounted price on update
        if (discountPercentage != null && discountPercentage > 0) {
            discountedPrice = price - (price * discountPercentage / 100);
        } else {
            discountedPrice = price;
        }
    }

    /**
     * Get final price (considering discount)
     */
    public Double getFinalPrice() {
        return discountedPrice != null ? discountedPrice : price;
    }

    /**
     * Check if plan has discount
     */
    public boolean hasDiscount() {
        return discountPercentage != null && discountPercentage > 0;
    }
}