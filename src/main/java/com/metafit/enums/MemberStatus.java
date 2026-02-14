package com.metafit.enums;

/**
 * Membership status enumeration
 */
public enum MemberStatus {
    ACTIVE("Active", "Member has an active membership"),
    EXPIRED("Expired", "Membership has expired"),
    SUSPENDED("Suspended", "Membership temporarily suspended"),
    CANCELLED("Cancelled", "Membership cancelled by member or admin"),
    PENDING("Pending", "Awaiting payment or verification");

    private final String displayName;
    private final String description;

    MemberStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return this == ACTIVE;
    }

    public boolean canCheckIn() {
        return this == ACTIVE;
    }
}