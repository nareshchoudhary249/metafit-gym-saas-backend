package com.metafit.enums;

/**
 * Source of attendance check-in
 */
public enum AttendanceSource {
    MANUAL("Manual", "Manually recorded by staff"),
    RFID("RFID", "RFID card scan"),
    BIOMETRIC("Biometric", "Fingerprint or face recognition"),
    QR_CODE("QR Code", "QR code scan"),
    MOBILE_APP("Mobile App", "Member self check-in via mobile app");

    private final String displayName;
    private final String description;

    AttendanceSource(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public boolean isAutomated() {
        return this != MANUAL;
    }
}
