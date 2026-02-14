package com.metafit.enums;

/**
 * Device event type for logging
 */
public enum DeviceEventType {
    CHECK_IN("Check In", "Member check-in event"),
    CHECK_OUT("Check Out", "Member check-out event"),
    ENROLLMENT("Enrollment", "New member enrolled on device"),
    UNENROLLMENT("Unenrollment", "Member removed from device"),
    HEARTBEAT("Heartbeat", "Device heartbeat/health check"),
    ERROR("Error", "Device error event"),
    SYNC("Sync", "Device data synchronization"),
    CONFIG_CHANGE("Config Change", "Device configuration updated"),
    FIRMWARE_UPDATE("Firmware Update", "Device firmware updated"),
    DEVICE_ONLINE("Device came online", "Device came online"),          // Device came online
    DEVICE_OFFLINE("Device went offline", "Device went offline"),         // Device went offline
    DEVICE_ERROR("Device reported error","Device reported error"),
    ACCESS_DENIED("Access denied","Access denied (expired membership, etc.)"),
    DEVICE_NOT_FOUND("Device identifier not found","Device identifier not found"),
    DUPLICATE_CHECK_IN("Member already checked in","Member already checked in"),
    REBOOT("Reboot", "Device rebooted");

    private final String displayName;
    private final String description;

    DeviceEventType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public boolean isAttendanceEvent() {
        return this == CHECK_IN || this == CHECK_OUT;
    }

    public boolean isSystemEvent() {
        return this == HEARTBEAT || this == SYNC || this == CONFIG_CHANGE ||
                this == FIRMWARE_UPDATE || this == REBOOT;
    }

    public boolean isErrorEvent() {
        return this == ERROR;
    }
}