package com.metafit.enums;

import lombok.Getter;

/**
 * Device type enumeration for access control devices
 */
@Getter
public enum DeviceType {
    BIOMETRIC("Biometric", "Fingerprint or palm scanner"),
    RFID("RFID", "RFID card reader"),
    QR_SCANNER("QR Scanner", "QR code scanner"),
    FACE_RECOGNITION("Face Recognition", "Facial recognition system"),
    NFC_READER("NFC Reader","NFC Reader"),
    HYBRID("Hybrid", "Multiple authentication methods");

    private final String displayName;
    private final String description;

    DeviceType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public boolean requiresEnrollment() {
        return this == BIOMETRIC || this == FACE_RECOGNITION || this == HYBRID;
    }
}