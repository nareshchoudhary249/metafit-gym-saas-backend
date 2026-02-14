package com.metafit.entity;

import com.metafit.enums.ConnectionType;
import com.metafit.enums.DeviceStatus;
import com.metafit.enums.DeviceType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Device Entity
 * Represents hardware devices (RFID scanners, biometric devices, etc.)
 * connected to the gym management system
 */
@Entity
@Table(name = "devices")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Device name/identifier
     * Example: "Main Entrance RFID", "Gym Floor Biometric"
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * Device type (RFID, BIOMETRIC, QR_SCANNER)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "device_type", nullable = false, length = 20)
    private DeviceType deviceType;

    /**
     * Device manufacturer
     * Example: "ZKTeco", "HID", "eSSL", "Matrix"
     */
    @Column(length = 50)
    private String manufacturer;

    /**
     * Device model
     * Example: "K40", "iCLASS SE", "X990"
     */
    @Column(length = 50)
    private String model;

    /**
     * Serial number (unique identifier from manufacturer)
     */
    @Column(name = "serial_number", unique = true, length = 100)
    private String serialNumber;

    /**
     * IP Address (for network-connected devices)
     * Example: "192.168.1.100"
     */
    @Column(name = "ip_address", length = 15)
    private String ipAddress;

    /**
     * Port number (for TCP/IP devices)
     * Example: 4370
     */
    @Column
    private Integer port;

    /**
     * MAC Address
     */
    @Column(name = "mac_address", length = 17)
    private String macAddress;

    /**
     * Device location in gym
     * Example: "Main Entrance", "Back Door", "Reception"
     */
    @Column(length = 100)
    private String location;

    /**
     * API endpoint or webhook URL for this device
     * Example: "/api/devices/webhook/device123"
     */
    @Column(name = "webhook_url", length = 255)
    private String webhookUrl;

    /**
     * API key for device authentication
     */
    @Column(name = "api_key", length = 100)
    private String apiKey;

    /**
     * Device status
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DeviceStatus status;

    /**
     * Connection type (TCP_IP, SERIAL, USB, BLUETOOTH, WEBHOOK)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "connection_type", length = 20)
    private ConnectionType connectionType;

    /**
     * Is device active and accepting check-ins
     */
    @Column(nullable = false)
    private Boolean active;

    /**
     * Configuration JSON (device-specific settings)
     * Example: {"timeout": 30, "retries": 3, "mode": "1:N"}
     */
    @Column(columnDefinition = "TEXT")
    private String configuration;

    /**
     * Last successful ping/heartbeat
     */
    @Column(name = "last_ping")
    private LocalDateTime lastPing;

    /**
     * Last successful check-in processed
     */
    @Column(name = "last_check_in")
    private LocalDateTime lastCheckIn;

    /**
     * Total check-ins processed by this device
     */
    @Column(name = "total_check_ins")
    private Long totalCheckIns;

    /**
     * Notes about the device
     */
    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        if (active == null) {
            active = true;
        }
        if (status == null) {
            status = DeviceStatus.OFFLINE;
        }
        if (totalCheckIns == null) {
            totalCheckIns = 0L;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Check if device is online
     */
    public boolean isOnline() {
        return DeviceStatus.ONLINE.equals(status);
    }

    /**
     * Check if device is active and can process check-ins
     */
    public boolean canProcessCheckIns() {
        return active && isOnline();
    }

    /**
     * Update last ping timestamp
     */
    public void updatePing() {
        this.lastPing = LocalDateTime.now();
        this.status = DeviceStatus.ONLINE;
    }

    /**
     * Increment check-in counter
     */
    public void incrementCheckIns() {
        if (this.totalCheckIns == null) {
            this.totalCheckIns = 0L;
        }
        this.totalCheckIns++;
        this.lastCheckIn = LocalDateTime.now();
    }
}

///**
// * Device Type Enum
// */
//public enum DeviceType {
//    RFID_SCANNER,       // RFID card reader
//    BIOMETRIC,          // Fingerprint/Face recognition
//    QR_SCANNER,         // QR code scanner
//    BARCODE_SCANNER,    // Barcode scanner
//    NFC_READER,         // NFC card reader
//    MOBILE_APP,         // Mobile app integration
//    WEB_KIOSK          // Web-based kiosk
//}
//
///**
// * Device Status Enum
// */
//public enum DeviceStatus {
//    ONLINE,             // Device is connected and working
//    OFFLINE,            // Device is not responding
//    ERROR,              // Device has errors
//    MAINTENANCE,        // Device is under maintenance
//    DISABLED            // Device is intentionally disabled
//}
//
///**
// * Connection Type Enum
// */
//public enum ConnectionType {
//    TCP_IP,             // Network connection (Ethernet/WiFi)
//    SERIAL,             // Serial port (RS232/RS485)
//    USB,                // USB connection
//    BLUETOOTH,          // Bluetooth wireless
//    WEBHOOK,            // HTTP webhook (device pushes data)
//    REST_API            // REST API (we poll device)
