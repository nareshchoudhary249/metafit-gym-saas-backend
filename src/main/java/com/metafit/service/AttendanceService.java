package com.metafit.service;

import com.metafit.dto.request.CheckInRequest;
import com.metafit.dto.request.CheckOutRequest;
import com.metafit.dto.response.AttendanceResponse;
import com.metafit.dto.response.AttendanceSummaryResponse;
import com.metafit.dto.response.attendance.TodayAttendanceSummaryResponse;

import java.time.LocalDate;
import java.util.List;

/**
 * Attendance Service Interface
 * Handles all attendance-related business logic
 */
public interface AttendanceService {

    /**
     * Check-in a member
     * @param request Check-in details (memberId, source, notes)
     * @param checkedInBy Username of staff who checked in the member
     * @return Attendance response
     */
    AttendanceResponse checkIn(CheckInRequest request, String checkedInBy);

    /**
     * Check-out a member
     * @param request Check-out details (memberId or attendanceId)
     * @return Updated attendance response
     */
    AttendanceResponse checkOut(CheckOutRequest request);

    /**
     * Get today's attendance list
     * @return List of today's attendance records
     */
    List<AttendanceResponse> getTodayAttendance();

    /**
     * Get today's attendance summary
     * @return Summary with total count, active check-ins, etc.
     */
    AttendanceSummaryResponse getTodaySummary();

    /**
     * Get attendance count for today
     * @return Count of today's check-ins
     */
    Long getTodayAttendanceCount();

    /**
     * Get attendance records between dates
     * @param startDate Start date
     * @param endDate End date
     * @return List of attendance records
     */
    List<AttendanceResponse> getAttendanceBetweenDates(LocalDate startDate, LocalDate endDate);

    /**
     * Get attendance history for a member
     * @param memberId Member ID
     * @param days Number of days to look back (default 30)
     * @return List of attendance records
     */
    List<AttendanceResponse> getMemberAttendanceHistory(Long memberId, Integer days);

    /**
     * Check if member is currently checked in
     * @param memberId Member ID
     * @return true if checked in, false otherwise
     */
    boolean isMemberCheckedIn(Long memberId);

    /**
     * Get attendance statistics for a member
     * @param memberId Member ID
     * @return Statistics (total count, average duration, etc.)
     */
    TodayAttendanceSummaryResponse getMemberAttendanceStats(Long memberId);

    /**
     * Get active check-ins (members currently in gym)
     * @return List of active attendance records
     */
    List<AttendanceResponse> getActiveCheckIns();
}