package com.metafit.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Tenant Information DTO
 * Contains basic tenant details used in multi-tenant context
 * Used for storing tenant info in JWT token or security context
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TenantInfo {

    /**
     * Tenant ID from master database
     */
    private Long id;

    /**
     * Tenant code (unique identifier)
     * Example: "fitgym", "powergym", "elitegym"
     */
    private String code;

    /**
     * Tenant name (gym name)
     * Example: "FitGym Mumbai"
     */
    private String name;

    /**
     * Database name for this tenant
     * Example: "gym_fitgym_db"
     */
    private String dbName;

    /**
     * Tenant status
     * ACTIVE, TRIAL, SUSPENDED, CANCELLED
     */
    private String status;

    /**
     * Owner name
     */
    private String ownerName;

    /**
     * Owner email
     */
    private String ownerEmail;

    /**
     * Owner phone
     */
    private String ownerPhone;

    /**
     * Subscription plan ID (if applicable)
     */
    private Long subscriptionPlanId;

    /**
     * Subscription plan name
     * Example: "Basic", "Standard", "Premium"
     */
    private String subscriptionPlanName;

    /**
     * Is subscription active
     */
    private Boolean subscriptionActive;

    /**
     * Primary color for branding (hex code)
     * Example: "#10B981"
     */
    private String primaryColor;

    /**
     * Gym logo URL (if uploaded)
     */
    private String logoUrl;
}