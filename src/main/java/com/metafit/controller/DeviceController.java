package com.metafit.controller;

import com.metafit.dto.request.*;
import com.metafit.dto.response.*;
import com.metafit.enums.DeviceStatus;
import com.metafit.enums.DeviceType;
import com.metafit.service.DeviceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Device Controller
 * REST API endpoints for device management and integration
 */
@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${app.cors.allowed-origins}")
public class DeviceController {

    private final DeviceService deviceService;

    // ==================== DEVICE MANAGEMENT ====================

    /**
     * Create new device
     * POST /api/devices
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<DeviceResponse> createDevice(
            @Valid @RequestBody CreateDeviceRequest request,
            Authentication authentication) {

        log.info("Creating device: {}", request.getName());
        DeviceResponse response = deviceService.createDevice(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get all devices
     * GET /api/devices
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'RECEPTION')")
    public ResponseEntity<List<DeviceResponse>> getAllDevices() {
        log.debug("Fetching all devices");
        List<DeviceResponse> devices = deviceService.getAllDevices();
        return ResponseEntity.ok(devices);
    }

    /**
     * Get device by ID
     * GET /api/devices/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<DeviceDetailResponse> getDeviceById(@PathVariable Long id) {
        log.debug("Fetching device: {}", id);
        DeviceDetailResponse response = deviceService.getDeviceById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Get devices by type
     * GET /api/devices/type/{type}
     */
    @GetMapping("/type/{type}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<List<DeviceResponse>> getDevicesByType(@PathVariable DeviceType type) {
        log.debug("Fetching devices by type: {}", type);
        List<DeviceResponse> devices = deviceService.getDevicesByType(type);
        return ResponseEntity.ok(devices);
    }

    /**
     * Get online devices
     * GET /api/devices/online
     */
    @GetMapping("/online")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'RECEPTION')")
    public ResponseEntity<List<DeviceResponse>> getOnlineDevices() {
        log.debug("Fetching online devices");
        List<DeviceResponse> devices = deviceService.getOnlineDevices();
        return ResponseEntity.ok(devices);
    }

    /**
     * Update device
     * PUT /api/devices/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<DeviceResponse> updateDevice(
            @PathVariable Long id,
            @Valid @RequestBody CreateDeviceRequest request,
            Authentication authentication) {

        log.info("Updating device: {}", id);
        DeviceResponse response = deviceService.updateDevice(id, request, authentication.getName());
        return ResponseEntity.ok(response);
    }

    /**
     * Update device status
     * PATCH /api/devices/{id}/status
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<Map<String, String>> updateDeviceStatus(
            @PathVariable Long id,
            @RequestParam DeviceStatus status) {

        log.info("Updating device {} status to {}", id, status);
        deviceService.updateDeviceStatus(id, status);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Device status updated successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * Delete device
     * DELETE /api/devices/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Map<String, String>> deleteDevice(@PathVariable Long id) {
        log.info("Deleting device: {}", id);
        deviceService.deleteDevice(id);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Device deleted successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * Test device connection
     * POST /api/devices/{id}/test
     */
    @PostMapping("/{id}/test")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> testDevice(@PathVariable Long id) {
        log.info("Testing device connection: {}", id);
        Boolean isConnected = deviceService.testDeviceConnection(id);

        Map<String, Object> response = new HashMap<>();
        response.put("connected", isConnected);
        response.put("message", isConnected ? "Device is online" : "Device is offline");
        return ResponseEntity.ok(response);
    }

    // ==================== WEBHOOK ENDPOINTS (For Devices) ====================

    /**
     * Device check-in webhook
     * POST /api/devices/webhook/check-in
     *
     * This endpoint receives check-in events from external devices
     * No authentication required (uses API key in request body)
     */
    @PostMapping("/webhook/check-in")
    public ResponseEntity<DeviceCheckInResponse> handleDeviceCheckIn(
            @Valid @RequestBody DeviceCheckInRequest request) {

        log.info("Received check-in from device: {}", request.getDeviceIdentifier());

        try {
            DeviceCheckInResponse response = deviceService.processCheckIn(request);
            HttpStatus status = response.getSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(response);
        } catch (Exception e) {
            log.error("Error processing check-in", e);
            DeviceCheckInResponse errorResponse = DeviceCheckInResponse.builder()
                    .success(false)
                    .message("Internal error: " + e.getMessage())
                    .errorCode("INTERNAL_ERROR")
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Device heartbeat webhook
     * POST /api/devices/webhook/heartbeat
     */
    @PostMapping("/webhook/heartbeat")
    public ResponseEntity<Map<String, String>> handleDeviceHeartbeat(
            @Valid @RequestBody DeviceHeartbeatRequest request) {

        log.debug("Received heartbeat from device");
        deviceService.processHeartbeat(request);

        Map<String, String> response = new HashMap<>();
        response.put("status", "ok");
        response.put("message", "Heartbeat received");
        return ResponseEntity.ok(response);
    }

    // ==================== MEMBER ENROLLMENT ====================

    /**
     * Enroll member on device
     * POST /api/devices/enroll
     */
    @PostMapping("/enroll")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'RECEPTION')")
    public ResponseEntity<MemberDeviceMappingResponse> enrollMember(
            @Valid @RequestBody EnrollMemberDeviceRequest request,
            Authentication authentication) {

        log.info("Enrolling member {} on device {}", request.getMemberId(), request.getDeviceId());
        MemberDeviceMappingResponse response = deviceService.enrollMember(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Unenroll member from device
     * DELETE /api/devices/enroll/{mappingId}
     */
    @DeleteMapping("/enroll/{mappingId}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<Map<String, String>> unenrollMember(@PathVariable Long mappingId) {
        log.info("Unenrolling member mapping: {}", mappingId);
        deviceService.unenrollMember(mappingId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Member unenrolled successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * Get member's device mappings
     * GET /api/devices/member/{memberId}/mappings
     */
    @GetMapping("/member/{memberId}/mappings")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'RECEPTION')")
    public ResponseEntity<List<MemberDeviceMappingResponse>> getMemberMappings(@PathVariable Long memberId) {
        log.debug("Fetching device mappings for member: {}", memberId);
        List<MemberDeviceMappingResponse> mappings = deviceService.getMemberDeviceMappings(memberId);
        return ResponseEntity.ok(mappings);
    }

    /**
     * Get device's enrolled members
     * GET /api/devices/{deviceId}/members
     */
    @GetMapping("/{deviceId}/members")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<List<MemberDeviceMappingResponse>> getDeviceMembers(@PathVariable Long deviceId) {
        log.debug("Fetching enrolled members for device: {}", deviceId);
        List<MemberDeviceMappingResponse> members = deviceService.getDeviceEnrolledMembers(deviceId);
        return ResponseEntity.ok(members);
    }

    // ==================== EVENT LOGS ====================

    /**
     * Get device event logs
     * GET /api/devices/{deviceId}/logs
     */
    @GetMapping("/{deviceId}/logs")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<List<DeviceEventLogResponse>> getDeviceLogs(
            @PathVariable Long deviceId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {

        if (startTime == null) startTime = LocalDateTime.now().minusDays(7);
        if (endTime == null) endTime = LocalDateTime.now();

        log.debug("Fetching device logs for device: {}", deviceId);
        List<DeviceEventLogResponse> logs = deviceService.getDeviceEventLogs(deviceId, startTime, endTime);
        return ResponseEntity.ok(logs);
    }

    /**
     * Get member event logs
     * GET /api/devices/member/{memberId}/logs
     */
    @GetMapping("/member/{memberId}/logs")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'RECEPTION')")
    public ResponseEntity<List<DeviceEventLogResponse>> getMemberLogs(@PathVariable Long memberId) {
        log.debug("Fetching device logs for member: {}", memberId);
        List<DeviceEventLogResponse> logs = deviceService.getMemberEventLogs(memberId);
        return ResponseEntity.ok(logs);
    }

    /**
     * Get recent errors
     * GET /api/devices/errors
     */
    @GetMapping("/errors")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<List<DeviceEventLogResponse>> getRecentErrors(
            @RequestParam(defaultValue = "24") int hours) {

        log.debug("Fetching recent device errors");
        List<DeviceEventLogResponse> errors = deviceService.getRecentErrors(hours);
        return ResponseEntity.ok(errors);
    }

    // ==================== STATISTICS ====================

    /**
     * Get device statistics
     * GET /api/devices/stats
     */
    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<DeviceStatsResponse> getDeviceStats() {
        log.debug("Fetching device statistics");
        DeviceStatsResponse stats = deviceService.getDeviceStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * Get today's check-ins by device
     * GET /api/devices/{deviceId}/today-checkins
     */
    @GetMapping("/{deviceId}/today-checkins")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'RECEPTION')")
    public ResponseEntity<Map<String, Long>> getTodayCheckIns(@PathVariable Long deviceId) {
        log.debug("Fetching today's check-ins for device: {}", deviceId);
        Long count = deviceService.getTodayCheckInsByDevice(deviceId);

        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }
}