package com.metafit.enums;

import lombok.Getter;

/**
 * Subscription plan types
 */
@Getter
public enum PlanType {
    BASIC("Basic", "₹1,299/month", 150, 2, 0),
    STANDARD("Standard", "₹2,499/month", 500, 5, 1),
    PREMIUM("Premium", "₹4,999/month", Integer.MAX_VALUE, Integer.MAX_VALUE, 10),
    TRIAL("Trial", "Free 7-day trial", 50, 1, 0),
    CUSTOM("Custom", "Custom enterprise plan", Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);

    private final String displayName;
    private final String priceLabel;
    private final int maxMembers;
    private final int maxStaff;
    private final int maxDevices;

    PlanType(String displayName, String priceLabel, int maxMembers, int maxStaff, int maxDevices) {
        this.displayName = displayName;
        this.priceLabel = priceLabel;
        this.maxMembers = maxMembers;
        this.maxStaff = maxStaff;
        this.maxDevices = maxDevices;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPriceLabel() {
        return priceLabel;
    }

    public int getMaxMembers() {
        return maxMembers;
    }

    public int getMaxStaff() {
        return maxStaff;
    }

    public int getMaxDevices() {
        return maxDevices;
    }

    public boolean isUnlimited() {
        return this == PREMIUM || this == CUSTOM;
    }

    public boolean allowsDevices() {
        return maxDevices > 0;
    }
}