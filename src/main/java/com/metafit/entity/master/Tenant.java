package com.metafit.entity.master;

import com.metafit.enums.TenantStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Tenant Entity (Master Database)
 * Represents a gym tenant in the multi-tenant system
 *
 * Stored in: gym_master_db.tenants table
 * Each tenant has its own separate database (gym_<code>_db)
 */
@Entity
@Table(name = "tenants", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tenant {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

/**
 * Gym name
 * Example: "FitGym Mumbai", "PowerHouse Fitness"
 */
@Column(nullable = false, length = 100)
private String name;

/**
 * Unique tenant code (used for routing and database naming)
 * Example: "fitgym", "powerhouse", "elitegym"
 * Rules: lowercase, alphanumeric, no spaces
 */
@Column(nullable = false, unique = true, length = 20)
private String code;

/**
 * Database name for this tenant
 * Example: "gym_fitgym_db"
 * Format: gym_<code>_db
 */
@Column(name = "db_name", nullable = false, unique = true, length = 100)
private String dbName;

/**
 * Tenant status
 * ACTIVE - Full access
 * TRIAL - Trial period
 * SUSPENDED - Payment issues or policy violation
 * CANCELLED - Account closed
 */
@Enumerated(EnumType.STRING)
@Column(nullable = false, length = 20)
public TenantStatus status;

/**
 * Gym owner name
 */
@Column(name = "owner_name", nullable = false, length = 100)
private String ownerName;

/**
 * Gym owner email (unique across all tenants)
 */
@Column(name = "owner_email", nullable = false, unique = true, length = 100)
private String ownerEmail;

/**
 * Gym owner phone
 */
@Column(name = "owner_phone", nullable = false, length = 15)
private String ownerPhone;

/**
 * Configuration JSON (JSONB in PostgreSQL)
 * Contains: branding, contact, settings, features
 *
 * Example structure:
 * {
 *   "branding": {
 *     "primaryColor": "#10B981",
 *     "accentColor": "#3B82F6",
 *     "logo": "https://...",
 *     "tagline": "Transform Your Body"
 *   },
 *   "contact": {
 *     "address": "123 Main St",
 *     "phone": "9876543210",
 *     "email": "info@fitgym.com",
 *     "website": "https://fitgym.com"
 *   },
 *   "settings": {
 *     "timezone": "Asia/Kolkata",
 *     "currency": "INR",
 *     "workingHours": "6:00 AM - 10:00 PM"
 *   },
 *   "features": {
 *     "smsNotifications": true,
 *     "emailNotifications": true,
 *     "biometricAccess": false
 *   }
 * }
 */
@Column(columnDefinition = "jsonb")
private String config;

/**
 * When tenant was created
 */
@Column(name = "created_at", nullable = false, updatable = false)
private LocalDateTime createdAt;

/**
 * Last update timestamp
 */
@Column(name = "updated_at")
private LocalDateTime updatedAt;

@PrePersist
protected void onCreate() {
    createdAt = LocalDateTime.now();
    updatedAt = LocalDateTime.now();

    // Default status to TRIAL if not set
    if (status == null) {
        status = TenantStatus.TRIAL;
    }

    // Auto-generate database name from code if not set
    if (dbName == null && code != null) {
        dbName = "gym_" + code.toLowerCase() + "_db";
    }
}

@PreUpdate
protected void onUpdate() {
    updatedAt = LocalDateTime.now();
}

/**
 * Check if tenant is active
 */
public boolean isActive() {
    return TenantStatus.ACTIVE.equals(status);
}

/**
 * Check if tenant is in trial
 */
public boolean isTrial() {
    return TenantStatus.TRIAL.equals(status);
}

/**
 * Check if tenant is suspended
 */
public boolean isSuspended() {
    return TenantStatus.SUSPENDED.equals(status);
}
}

