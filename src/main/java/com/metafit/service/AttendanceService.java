package com.metafit.service;

import com.metafit.dto.request.attendance.CheckInRequest;
import com.metafit.dto.request.attendance.CheckOutRequest;
import com.metafit.dto.response.attendance.AttendanceResponse;
import com.metafit.dto.response.attendance.MemberAttendanceItem;
import com.metafit.dto.response.attendance.TodayAttendanceSummary;

import java.time.LocalDate;
import java.util.List;

/**
 * Attendance Service Interface
 */
public interface AttendanceService {

    AttendanceResponse checkIn(CheckInRequest request, String checkedInBy);

    AttendanceResponse checkOut(CheckOutRequest request);

    List<AttendanceResponse> getTodayAttendance();

    TodayAttendanceSummary getTodayAttendanceSummary();

    long getTodayAttendanceCount();

    List<AttendanceResponse> getAttendanceByDateRange(LocalDate startDate, LocalDate endDate);

    List<MemberAttendanceItem> getMemberAttendanceHistory(Long memberId, int days);

    boolean isMemberCheckedIn(Long memberId);
}
