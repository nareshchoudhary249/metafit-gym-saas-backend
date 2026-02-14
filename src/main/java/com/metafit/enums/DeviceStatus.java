package com.metafit.enums;


import lombok.Getter;

/**
 * Device operational status
 */
@Getter
public enum DeviceStatus {
    ONLINE("Active", "Device is operational"),
    OFFLINE("Inactive", "Device is offline or disabled"),
    MAINTENANCE("Maintenance", "Device under maintenance"),
    ERROR("Error", "Device has errors"),
    DISABLED("Disabled","Device is intentionally disabled"),
    CALIBRATING("Calibrating", "Device is being calibrated");

    private final String displayName;
    private final String description;

    DeviceStatus(String displayName, String description) {
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

    public boolean needsAttention() {
        return this == ERROR || this == MAINTENANCE;
    }
}
