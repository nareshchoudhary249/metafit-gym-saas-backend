package com.metafit.enums;


/**
 * Tenant subscription status
 */
public enum SubscriptionStatus {
    TRIAL("Trial", "Free trial period", true),
    ACTIVE("Active", "Subscription is active", true),
    EXPIRED("Expired", "Subscription has expired", false),
    SUSPENDED("Suspended", "Subscription suspended due to payment failure", false),
    CANCELLED("Cancelled", "Subscription cancelled by tenant", false),
    GRACE_PERIOD("Grace Period", "Expired but in grace period", true),
    PENDING("Pending", "Awaiting payment confirmation", false);

    private final String displayName;
    private final String description;
    private final boolean allowsAccess;

    SubscriptionStatus(String displayName, String description, boolean allowsAccess) {
        this.displayName = displayName;
        this.description = description;
        this.allowsAccess = allowsAccess;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public boolean allowsAccess() {
        return allowsAccess;
    }

    public boolean needsRenewal() {
        return this == EXPIRED || this == GRACE_PERIOD || this == PENDING;
    }

    public boolean isActionRequired() {
        return this == SUSPENDED || this == PENDING || this == EXPIRED;
    }
}
