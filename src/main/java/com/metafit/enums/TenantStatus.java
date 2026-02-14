package com.metafit.enums;


/**
 * Tenant account status
 */
public enum TenantStatus {
    PROVISIONING("Provisioning", "Tenant is being set up"),
    ACTIVE("Active", "Tenant is active and operational"),
    SUSPENDED("Suspended", "Tenant suspended due to payment issues"),
    INACTIVE("Inactive", "Tenant temporarily inactive"),
    DELETED("Deleted", "Tenant account deleted"),
    TRIAL("Trial","Trial period, may have feature limitations"),
    BLOCKED("Blocked", "Tenant blocked due to policy violation");

    private final String displayName;
    private final String description;

    TenantStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public boolean isOperational() {
        return this == ACTIVE;
    }

    public boolean canLogin() {
        return this == ACTIVE || this == PROVISIONING;
    }
}