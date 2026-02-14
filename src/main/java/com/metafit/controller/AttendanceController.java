package com.metafit.controller;

import com.metafit.dto.request.attendance.CheckInRequest;
import com.metafit.dto.request.attendance.CheckOutRequest;
import com.metafit.dto.response.attendance.AttendanceResponse;
import com.metafit.dto.response.attendance.MemberAttendanceItem;
import com.metafit.dto.response.attendance.TodayAttendanceSummary;
import com.metafit.service.AttendanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * REST controller for attendance management
 */
@Slf4j
@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    /**
     * Mark member check-in
     * POST /api/attendance/check-in
     */
    @PostMapping("/check-in")
    public ResponseEntity<AttendanceResponse> checkIn(
            @Valid @RequestBody CheckInRequest request) {

        String username = getCurrentUsername();
        log.info("POST /api/attendance/check-in - Member: {}, By: {}",
                request.getMemberId(), username);

        AttendanceResponse response = attendanceService.checkIn(request, username);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Mark member check-out
     * POST /api/attendance/check-out
     */
    @PostMapping("/check-out")
    public ResponseEntity<AttendanceResponse> checkOut(
            @Valid @RequestBody CheckOutRequest request) {

        log.info("POST /api/attendance/check-out - Attendance: {}",
                request.getAttendanceId());

        AttendanceResponse response = attendanceService.checkOut(request);

        return ResponseEntity.ok(response);
    }

    /**
     * Get today's attendance list
     * GET /api/attendance/today
     */
    @GetMapping("/today")
    public ResponseEntity<List<AttendanceResponse>> getTodayAttendance() {
        log.info("GET /api/attendance/today - Fetching today's attendance");

        List<AttendanceResponse> attendances = attendanceService.getTodayAttendance();

        return ResponseEntity.ok(attendances);
    }

    /**
     * Get today's attendance summary
     * GET /api/attendance/today/summary
     */
    @GetMapping("/today/summary")
    public ResponseEntity<TodayAttendanceSummary> getTodayAttendanceSummary() {
        log.info("GET /api/attendance/today/summary");

        TodayAttendanceSummary summary = attendanceService.getTodayAttendanceSummary();

        return ResponseEntity.ok(summary);
    }

    /**
     * Get today's attendance count
     * GET /api/attendance/today/count
     */
    @GetMapping("/today/count")
    public ResponseEntity<Long> getTodayAttendanceCount() {
        log.info("GET /api/attendance/today/count");

        long count = attendanceService.getTodayAttendanceCount();

        return ResponseEntity.ok(count);
    }

    /**
     * Get attendance by date range
     * GET /api/attendance?startDate=2025-11-01&endDate=2025-11-17
     */
    @GetMapping
    public ResponseEntity<List<AttendanceResponse>> getAttendanceByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("GET /api/attendance?startDate={}&endDate={}", startDate, endDate);

        List<AttendanceResponse> attendances = attendanceService
                .getAttendanceByDateRange(startDate, endDate);

        return ResponseEntity.ok(attendances);
    }

    /**
     * Get member attendance history
     * GET /api/attendance/member/{memberId}?days=30
     */
    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<AttendanceResponse>> getMemberAttendanceHistory(
            @PathVariable Long memberId,
            @RequestParam(defaultValue = "30") int days) {

        log.info("GET /api/attendance/member/{} - Last {} days", memberId, days);

        List<AttendanceResponse> history = attendanceService
                .getMemberAttendanceHistory(memberId, days);

        return ResponseEntity.ok(history);
    }

    /**
     * Check if member is currently checked in
     * GET /api/attendance/member/{memberId}/is-checked-in
     */
    @GetMapping("/member/{memberId}/is-checked-in")
    public ResponseEntity<Boolean> isMemberCheckedIn(@PathVariable Long memberId) {
        log.debug("GET /api/attendance/member/{}/is-checked-in", memberId);

        boolean isCheckedIn = attendanceService.isMemberCheckedIn(memberId);

        return ResponseEntity.ok(isCheckedIn);
    }

    // ============= HELPER METHODS =============

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : "system";
    }
}