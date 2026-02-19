package com.metafit.service.impl;

import com.metafit.dto.request.CreateDeviceRequest;
import com.metafit.dto.request.DeviceCheckInRequest;
import com.metafit.dto.request.DeviceHeartbeatRequest;
import com.metafit.dto.request.EnrollMemberDeviceRequest;
import com.metafit.dto.response.DeviceCheckInResponse;
import com.metafit.dto.response.DeviceDetailResponse;
import com.metafit.dto.response.DeviceEventLogResponse;
import com.metafit.dto.response.DeviceResponse;
import com.metafit.dto.response.DeviceStatsResponse;
import com.metafit.dto.response.MemberDeviceMappingResponse;
import com.metafit.entity.Attendance;
import com.metafit.entity.Device;
import com.metafit.entity.DeviceEventLog;
import com.metafit.entity.Member;
import com.metafit.entity.MemberDeviceMapping;
import com.metafit.enums.AttendanceSource;
import com.metafit.enums.DeviceEventType;
import com.metafit.enums.DeviceStatus;
import com.metafit.enums.DeviceType;
import com.metafit.enums.MemberStatus;
import com.metafit.exception.DuplicateResourceException;
import com.metafit.exception.ResourceNotFoundException;
import com.metafit.repository.AttendanceRepository;
import com.metafit.repository.DeviceEventLogRepository;
import com.metafit.repository.DeviceRepository;
import com.metafit.repository.MemberDeviceMappingRepository;
import com.metafit.repository.MemberRepository;
import com.metafit.service.DeviceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceServiceImpl implements DeviceService {

    private final DeviceRepository deviceRepository;
    private final MemberDeviceMappingRepository mappingRepository;
    private final DeviceEventLogRepository eventLogRepository;
    private final MemberRepository memberRepository;
    private final AttendanceRepository attendanceRepository;

    private static final int HEARTBEAT_TIMEOUT_MINUTES = 5;

    // ==================== DEVICE MANAGEMENT ====================

    @Override
    @Transactional
    public DeviceResponse createDevice(CreateDeviceRequest request, String createdBy) {
        log.info("Creating new device: {}", request.getName());

        // Check duplicate serial number
        if (request.getSerialNumber() != null
                && deviceRepository.existsBySerialNumber(request.getSerialNumber())) {
            throw new DuplicateResourceException("Device with serial number already exists");
        }

        // Generate API key
        String apiKey = generateApiKey();

        // Build device
        Device device = Device.builder()
                .name(request.getName())
                .deviceType(request.getDeviceType())
                .manufacturer(request.getManufacturer())
                .model(request.getModel())
                .serialNumber(request.getSerialNumber())
                .ipAddress(request.getIpAddress())
                .port(request.getPort())
                .macAddress(request.getMacAddress())
                .location(request.getLocation())
                .connectionType(request.getConnectionType())
                .configuration(request.getConfiguration())
                .notes(request.getNotes())
                .apiKey(apiKey)
                .status(DeviceStatus.OFFLINE)
                .active(true)
                .createdBy(createdBy)
                .build();

        // Generate webhook URL
        device.setWebhookUrl("/api/devices/webhook/" + apiKey);

        device = deviceRepository.save(device);
        log.info("Device created with ID: {}", device.getId());

        // Log event
        logDeviceEvent(device, DeviceEventType.CONFIG_CHANGE,
                null, null, true, "Device registered");

        return convertToResponse(device);
    }

    @Override
    @Transactional(readOnly = true)
    public DeviceDetailResponse getDeviceById(Long id) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found"));

        return convertToDetailResponse(device);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeviceResponse> getAllDevices() {
        return deviceRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeviceResponse> getDevicesByType(DeviceType deviceType) {
        return deviceRepository.findByDeviceType(deviceType).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeviceResponse> getOnlineDevices() {
        return deviceRepository.findActiveAndOnlineDevices().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DeviceResponse updateDevice(Long id, CreateDeviceRequest request, String updatedBy) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found"));

        device.setName(request.getName());
        device.setManufacturer(request.getManufacturer());
        device.setModel(request.getModel());
        device.setIpAddress(request.getIpAddress());
        device.setPort(request.getPort());
        device.setMacAddress(request.getMacAddress());
        device.setLocation(request.getLocation());
        device.setConfiguration(request.getConfiguration());
        device.setNotes(request.getNotes());

        device = deviceRepository.save(device);

        logDeviceEvent(device, DeviceEventType.CONFIG_CHANGE,
                null, null, true, "Device configuration updated");

        return convertToResponse(device);
    }

    @Override
    @Transactional
    public void updateDeviceStatus(Long id, DeviceStatus status) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found"));

        device.setStatus(status);
        deviceRepository.save(device);

        DeviceEventType eventType = status == DeviceStatus.ONLINE
                ? DeviceEventType.DEVICE_ONLINE
                : DeviceEventType.DEVICE_OFFLINE;

        logDeviceEvent(device, eventType, null, null, true,
                "Device status changed to " + status);
    }

    @Override
    @Transactional
    public void deleteDevice(Long id) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found"));

        device.setActive(false);
        device.setStatus(DeviceStatus.DISABLED);
        deviceRepository.save(device);
    }

    @Override
    public Boolean testDeviceConnection(Long id) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found"));

        // TODO: Implement actual connection test based on connection type
        // For now, just check if device has pinged recently
        if (device.getLastPing() != null) {
            LocalDateTime threshold = LocalDateTime.now().minusMinutes(HEARTBEAT_TIMEOUT_MINUTES);
            return device.getLastPing().isAfter(threshold);
        }

        return false;
    }

    // ==================== CHECK-IN PROCESSING ====================

    @Override
    @Transactional
    public DeviceCheckInResponse processCheckIn(DeviceCheckInRequest request) {
        log.info("Processing check-in from device with identifier: {}", request.getDeviceIdentifier());

        // Validate API key
        Device device = deviceRepository.findByApiKey(request.getApiKey())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid device API key"));

        if (!device.canProcessCheckIns()) {
            String msg = "Device is not active or offline";
            logDeviceEvent(device, DeviceEventType.DEVICE_ERROR, request.getDeviceIdentifier(),
                    null, false, msg);
            return DeviceCheckInResponse.builder()
                    .success(false)
                    .message(msg)
                    .errorCode("DEVICE_UNAVAILABLE")
                    .build();
        }

        // Update device ping
        device.updatePing();
        deviceRepository.save(device);

        // Find member mapping
        MemberDeviceMapping mapping = mappingRepository
                .findByDeviceAndIdentifier(device.getId(), request.getDeviceIdentifier())
                .orElse(null);

        if (mapping == null) {
            String msg = "Device identifier not registered";
            logDeviceEvent(device, DeviceEventType.DEVICE_NOT_FOUND,
                    request.getDeviceIdentifier(), null, false, msg);
            return DeviceCheckInResponse.builder()
                    .success(false)
                    .message(msg)
                    .errorCode("IDENTIFIER_NOT_FOUND")
                    .build();
        }

        Member member = mapping.getMember();

        // Check membership status
        if (!MemberStatus.ACTIVE.equals(member.getStatus())) {
            String msg = "Membership is " + member.getStatus();
            logDeviceEvent(device, DeviceEventType.ACCESS_DENIED,
                    request.getDeviceIdentifier(), member.getId(), false, msg);
            return DeviceCheckInResponse.builder()
                    .success(false)
                    .message("Access denied: " + msg)
                    .memberId(member.getId())
                    .memberName(member.getFullName())
                    .membershipStatus(member.getStatus().toString())
                    .errorCode("MEMBERSHIP_INACTIVE")
                    .build();
        }

        // Check if already checked in
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        if (attendanceRepository.findTodayActiveCheckIn(member.getId(), todayStart, todayEnd).isPresent()) {
            String msg = "Member already checked in today";
            logDeviceEvent(device, DeviceEventType.DUPLICATE_CHECK_IN,
                    request.getDeviceIdentifier(), member.getId(), false, msg);
            return DeviceCheckInResponse.builder()
                    .success(false)
                    .message(msg)
                    .memberId(member.getId())
                    .memberName(member.getFullName())
                    .errorCode("ALREADY_CHECKED_IN")
                    .build();
        }

        // Create attendance record
        Attendance attendance = Attendance.builder()
                .member(member)
                .checkInTime(LocalDateTime.now())
                .source(mapDeviceTypeToAttendanceSource(device.getDeviceType()))
                .createdBy("DEVICE:" + device.getName())
                .build();

        attendance = attendanceRepository.save(attendance);

        // Update counters
        device.incrementCheckIns();
        deviceRepository.save(device);

        mapping.recordUsage();
        mappingRepository.save(mapping);

        // Log success
        logDeviceEvent(device, DeviceEventType.CHECK_IN,
                request.getDeviceIdentifier(), member.getId(), true,
                "Check-in successful", attendance.getId());

        log.info("Check-in successful for member: {} via device: {}", member.getFullName(), device.getName());

        return DeviceCheckInResponse.builder()
                .success(true)
                .message("Check-in successful. Welcome " + member.getFullName() + "!")
                .memberId(member.getId())
                .memberName(member.getFullName())
                .membershipStatus(member.getStatus().toString())
                .checkInTime(attendance.getCheckInTime())
                .attendanceId(attendance.getId())
                .build();
    }

    @Override
    @Transactional
    public void processHeartbeat(DeviceHeartbeatRequest request) {
        Device device = deviceRepository.findByApiKey(request.getApiKey())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid device API key"));

        device.updatePing();
        deviceRepository.save(device);

        logDeviceEvent(device, DeviceEventType.HEARTBEAT, null, null, true,
                "Heartbeat received");
    }

    // ==================== MEMBER ENROLLMENT ====================

    @Override
    @Transactional
    public MemberDeviceMappingResponse enrollMember(EnrollMemberDeviceRequest request, String enrolledBy) {
        log.info("Enrolling member {} on device {}", request.getMemberId(), request.getDeviceId());

        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        Device device = deviceRepository.findById(request.getDeviceId())
                .orElseThrow(() -> new ResourceNotFoundException("Device not found"));

        // Check duplicate
        if (mappingRepository.existsByDeviceIdAndDeviceIdentifier(
                request.getDeviceId(), request.getDeviceIdentifier())) {
            throw new DuplicateResourceException("Device identifier already enrolled");
        }

        MemberDeviceMapping mapping = MemberDeviceMapping.builder()
                .member(member)
                .device(device)
                .deviceIdentifier(request.getDeviceIdentifier())
                .deviceType(device.getDeviceType())
                .metadata(request.getMetadata())
                .active(true)
                .createdBy(enrolledBy)
                .build();

        mapping = mappingRepository.save(mapping);

        logDeviceEvent(device, DeviceEventType.ENROLLMENT,
                request.getDeviceIdentifier(), member.getId(), true,
                "Member enrolled on device");

        return convertMappingToResponse(mapping);
    }

    @Override
    @Transactional
    public void unenrollMember(Long mappingId) {
        MemberDeviceMapping mapping = mappingRepository.findById(mappingId)
                .orElseThrow(() -> new ResourceNotFoundException("Mapping not found"));

        mapping.setActive(false);
        mappingRepository.save(mapping);

        logDeviceEvent(mapping.getDevice(), DeviceEventType.UNENROLLMENT,
                mapping.getDeviceIdentifier(), mapping.getMember().getId(),
                true, "Member unenrolled from device");
    }

    @Override
    @Transactional(readOnly = true)
    public List<MemberDeviceMappingResponse> getMemberDeviceMappings(Long memberId) {
        return mappingRepository.findByMemberId(memberId).stream()
                .map(this::convertMappingToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MemberDeviceMappingResponse> getDeviceEnrolledMembers(Long deviceId) {
        return mappingRepository.findByDeviceId(deviceId).stream()
                .map(this::convertMappingToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MemberDeviceMappingResponse findMemberByDeviceIdentifier(String identifier, DeviceType deviceType) {
        MemberDeviceMapping mapping = mappingRepository
                .findByIdentifierAndType(identifier, deviceType)
                .orElse(null);

        return mapping != null ? convertMappingToResponse(mapping) : null;
    }

    // ==================== EVENT LOGS ====================

    @Override
    @Transactional(readOnly = true)
    public List<DeviceEventLogResponse> getDeviceEventLogs(Long deviceId,
                                                           LocalDateTime startTime,
                                                           LocalDateTime endTime) {
        return eventLogRepository.findByDeviceAndTimeRange(deviceId, startTime, endTime).stream()
                .map(this::convertEventLogToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeviceEventLogResponse> getMemberEventLogs(Long memberId) {
        return eventLogRepository.findByMemberIdOrderByEventTimeDesc(memberId).stream()
                .map(this::convertEventLogToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeviceEventLogResponse> getRecentErrors(int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return eventLogRepository.findRecentErrors(since).stream()
                .map(this::convertEventLogToResponse)
                .collect(Collectors.toList());
    }

    // ==================== STATISTICS ====================

    @Override
    @Transactional(readOnly = true)
    public DeviceStatsResponse getDeviceStats() {
        long total = deviceRepository.count();
        long online = deviceRepository.countByStatus(DeviceStatus.ONLINE);
        long offline = deviceRepository.countByStatus(DeviceStatus.OFFLINE);
        long active = deviceRepository.findByActiveTrue().size();

        // Today's check-ins across all devices
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = LocalDate.now().atTime(LocalTime.MAX);
        long todayCheckIns = deviceRepository.findAll().stream()
                .mapToLong(d -> eventLogRepository.countTodayCheckInsByDevice(d.getId(), todayStart, todayEnd))
                .sum();

        // Recent errors (last 24 hours)
        long errors = eventLogRepository.findRecentErrors(LocalDateTime.now().minusHours(24)).size();

        return DeviceStatsResponse.builder()
                .totalDevices(total)
                .onlineDevices(online)
                .offlineDevices(offline)
                .activeDevices(active)
                .totalCheckInsToday(todayCheckIns)
                .errorCount(errors)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Long getTodayCheckInsByDevice(Long deviceId) {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = LocalDate.now().atTime(LocalTime.MAX);
        return eventLogRepository.countTodayCheckInsByDevice(deviceId, todayStart, todayEnd);
    }

    // ==================== UTILITIES ====================

    @Override
    public String generateApiKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return "dev_" + Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    @Override
    public Boolean validateApiKey(String apiKey) {
        return deviceRepository.existsByApiKey(apiKey);
    }

    @Override
    @Transactional
    public void checkDeviceHealth() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(HEARTBEAT_TIMEOUT_MINUTES);
        List<Device> offlineDevices = deviceRepository.findOfflineDevicesSince(threshold);

        for (Device device : offlineDevices) {
            if (device.getStatus() != DeviceStatus.OFFLINE) {
                device.setStatus(DeviceStatus.OFFLINE);
                deviceRepository.save(device);

                logDeviceEvent(device, DeviceEventType.DEVICE_OFFLINE, null, null, true,
                        "Device marked offline due to heartbeat timeout");
            }
        }
    }

    // ==================== HELPER METHODS ====================

    private void logDeviceEvent(Device device, DeviceEventType eventType,
                                String deviceIdentifier, Long memberId,
                                boolean success, String message) {
        logDeviceEvent(device, eventType, deviceIdentifier, memberId, success, message, null);
    }

    private void logDeviceEvent(Device device, DeviceEventType eventType,
                                String deviceIdentifier, Long memberId,
                                boolean success, String message, Long attendanceId) {
        DeviceEventLog log = DeviceEventLog.builder()
                .device(device)
                .eventType(eventType)
                .deviceIdentifier(deviceIdentifier)
                .memberId(memberId)
                .success(success)
                .message(message)
                .attendanceId(attendanceId)
                .build();

        eventLogRepository.save(log);
    }

    private AttendanceSource mapDeviceTypeToAttendanceSource(DeviceType deviceType) {
        return switch (deviceType) {
            case RFID , NFC_READER -> AttendanceSource.RFID;
            case BIOMETRIC -> AttendanceSource.BIOMETRIC;
            case QR_SCANNER -> AttendanceSource.QR_CODE;
            default -> AttendanceSource.MANUAL;
        };
    }

    private DeviceResponse convertToResponse(Device device) {
        return DeviceResponse.builder()
                .id(device.getId())
                .name(device.getName())
                .deviceType(device.getDeviceType())
                .manufacturer(device.getManufacturer())
                .model(device.getModel())
                .serialNumber(device.getSerialNumber())
                .ipAddress(device.getIpAddress())
                .port(device.getPort())
                .location(device.getLocation())
                .status(device.getStatus())
                .connectionType(device.getConnectionType())
                .active(device.getActive())
                .lastPing(device.getLastPing())
                .lastCheckIn(device.getLastCheckIn())
                .totalCheckIns(device.getTotalCheckIns())
                .webhookUrl(device.getWebhookUrl())
                .isOnline(device.isOnline())
                .createdAt(device.getCreatedAt())
                .build();
    }

    private DeviceDetailResponse convertToDetailResponse(Device device) {
        long enrolledMembers = mappingRepository.countActiveMappingsByMember(device.getId());
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = LocalDate.now().atTime(LocalTime.MAX);
        long todayCheckIns = eventLogRepository.countTodayCheckInsByDevice(device.getId(), todayStart, todayEnd);

        return DeviceDetailResponse.builder()
                .id(device.getId())
                .name(device.getName())
                .deviceType(device.getDeviceType())
                .manufacturer(device.getManufacturer())
                .model(device.getModel())
                .serialNumber(device.getSerialNumber())
                .ipAddress(device.getIpAddress())
                .port(device.getPort())
                .macAddress(device.getMacAddress())
                .location(device.getLocation())
                .status(device.getStatus())
                .connectionType(device.getConnectionType())
                .active(device.getActive())
                .configuration(device.getConfiguration())
                .notes(device.getNotes())
                .lastPing(device.getLastPing())
                .lastCheckIn(device.getLastCheckIn())
                .totalCheckIns(device.getTotalCheckIns())
                .enrolledMembers(enrolledMembers)
                .todayCheckIns(todayCheckIns)
                .webhookUrl(device.getWebhookUrl())
                .apiKey(device.getApiKey())
                .createdAt(device.getCreatedAt())
                .updatedAt(device.getUpdatedAt())
                .createdBy(device.getCreatedBy())
                .build();
    }

    private MemberDeviceMappingResponse convertMappingToResponse(MemberDeviceMapping mapping) {
        return MemberDeviceMappingResponse.builder()
                .id(mapping.getId())
                .memberId(mapping.getMember().getId())
                .memberName(mapping.getMember().getFullName())
                .deviceId(mapping.getDevice().getId())
                .deviceName(mapping.getDevice().getName())
                .deviceType(mapping.getDeviceType())
                .deviceIdentifier(mapping.getDeviceIdentifier())
                .active(mapping.getActive())
                .enrolledAt(mapping.getEnrolledAt())
                .lastUsed(mapping.getLastUsed())
                .usageCount(mapping.getUsageCount())
                .build();
    }

    private DeviceEventLogResponse convertEventLogToResponse(DeviceEventLog log) {
        return DeviceEventLogResponse.builder()
                .id(log.getId())
                .deviceId(log.getDevice().getId())
                .deviceName(log.getDevice().getName())
                .eventType(log.getEventType().toString())
                .deviceIdentifier(log.getDeviceIdentifier())
                .memberId(log.getMemberId())
                .memberName(log.getMemberId() != null ?
                        memberRepository.findById(log.getMemberId())
                                .map(Member::getFullName).orElse(null) : null)
                .success(log.getSuccess())
                .message(log.getMessage())
                .errorCode(log.getErrorCode())
                .attendanceId(log.getAttendanceId())
                .eventTime(log.getEventTime())
                .build();
    }
}
