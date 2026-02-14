package com.metafit.service;

import com.metafit.dto.request.*;
import com.metafit.dto.response.*;
import com.metafit.enums.DeviceStatus;
import com.metafit.enums.DeviceType;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Device Service Interface
 * Handles all device-related operations
 */
public interface DeviceService {

    // ==================== DEVICE MANAGEMENT ====================

    /**
     * Register a new device
     */
    DeviceResponse createDevice(CreateDeviceRequest request, String createdBy);

    /**
     * Get device by ID
     */
    DeviceDetailResponse getDeviceById(Long id);

    /**
     * Get all devices
     */
    List<DeviceResponse> getAllDevices();

    /**
     * Get devices by type
     */
    List<DeviceResponse> getDevicesByType(DeviceType deviceType);

    /**
     * Get online devices
     */
    List<DeviceResponse> getOnlineDevices();

    /**
     * Update device
     */
    DeviceResponse updateDevice(Long id, CreateDeviceRequest request, String updatedBy);

    /**
     * Update device status
     */
    void updateDeviceStatus(Long id, DeviceStatus status);

    /**
     * Delete device
     */
    void deleteDevice(Long id);

    /**
     * Test device connection
     */
    Boolean testDeviceConnection(Long id);

    // ==================== CHECK-IN PROCESSING ====================

    /**
     * Process check-in event from device
     */
    DeviceCheckInResponse processCheckIn(DeviceCheckInRequest request);

    /**
     * Process device heartbeat
     */
    void processHeartbeat(DeviceHeartbeatRequest request);

    // ==================== MEMBER ENROLLMENT ====================

    /**
     * Enroll member on device
     */
    MemberDeviceMappingResponse enrollMember(EnrollMemberDeviceRequest request, String enrolledBy);

    /**
     * Unenroll member from device
     */
    void unenrollMember(Long mappingId);

    /**
     * Get member's device mappings
     */
    List<MemberDeviceMappingResponse> getMemberDeviceMappings(Long memberId);

    /**
     * Get device's enrolled members
     */
    List<MemberDeviceMappingResponse> getDeviceEnrolledMembers(Long deviceId);

    /**
     * Find member by device identifier
     */
    MemberDeviceMappingResponse findMemberByDeviceIdentifier(String identifier, DeviceType deviceType);

    // ==================== EVENT LOGS ====================

    /**
     * Get device event logs
     */
    List<DeviceEventLogResponse> getDeviceEventLogs(Long deviceId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * Get member event logs
     */
    List<DeviceEventLogResponse> getMemberEventLogs(Long memberId);

    /**
     * Get recent errors
     */
    List<DeviceEventLogResponse> getRecentErrors(int hours);

    // ==================== STATISTICS ====================

    /**
     * Get device statistics
     */
    DeviceStatsResponse getDeviceStats();

    /**
     * Get device check-in count for today
     */
    Long getTodayCheckInsByDevice(Long deviceId);

    // ==================== UTILITIES ====================

    /**
     * Generate API key for device
     */
    String generateApiKey();

    /**
     * Check if device is authorized
     */
    Boolean validateApiKey(String apiKey);

    /**
     * Mark devices as offline if no heartbeat
     */
    void checkDeviceHealth();
}