package com.metafit.service.impl;

import com.metafit.dto.request.attendance.CheckInRequest;
import com.metafit.dto.response.attendance.AttendanceResponse;
import com.metafit.entity.Member;
import com.metafit.exception.ResourceNotFoundException;
import com.metafit.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for attendance management
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AttendanceServiceimpl {

    private final AttendanceRepository attendanceRepository;
    private final MemberRepository memberRepository;

    /**
     * Mark member check-in
     */
    public AttendanceResponse checkIn(CheckInRequest request, String currentUsername) {
        log.info("Check-in requested for member: {}", request.getMemberId());

        // Verify member exists and is active
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Member not found with ID: " + request.getMemberId()
                ));

        if (!member.getStatus().equals(Member.MembershipStatus.ACTIVE)) {
            log.warn("Attempt to check-in inactive member: {}", member.getFullName());
            throw new IllegalArgumentException(
                    "Cannot check-in. Member status is: " + member.getStatus()
            );
        }

        // Check if membership is expired
        if (member.getMembershipEndDate() != null &&
                member.getMembershipEndDate().isBefore(LocalDate.now())) {
            log.warn("Attempt to check-in member with expired membership: {}", member.getFullName());
            throw new IllegalArgumentException(
                    "Cannot check-in. Membership expired on: " + member.getMembershipEndDate()
            );
        }

        // Check if already checked in today and not checked out
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        List<Attendance> todayAttendance = attendanceRepository
                .findByMemberIdAndCheckInBetween(request.getMemberId(), todayStart, todayEnd);

        boolean alreadyCheckedIn = todayAttendance.stream()
                .anyMatch(a -> a.getCheckOut() == null);

        if (alreadyCheckedIn) {
            log.warn("Member already checked in: {}", member.getFullName());
            throw new IllegalArgumentException(
                    member.getFullName() + " is already checked in. Please check out first."
            );
        }

        // Create attendance record
        Attendance attendance = new Attendance();
        attendance.setMember(member);
        attendance.setCheckIn(LocalDateTime.now());
        attendance.setSource(Attendance.AttendanceSource.valueOf(request.getSource().toUpperCase()));
        attendance.setCreatedBy(currentUsername);

        Attendance savedAttendance = attendanceRepository.save(attendance);

        log.info("Check-in successful for member: {} at {}",
                member.getFullName(), savedAttendance.getCheckIn());

        return AttendanceResponse.fromEntity(savedAttendance);
    }

    /**
     * Mark member check-out
     */
    public AttendanceResponse checkOut(CheckOutRequest request) {
        log.info("Check-out requested for attendance: {}", request.getAttendanceId());

        Attendance attendance = attendanceRepository.findById(request.getAttendanceId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Attendance record not found with ID: " + request.getAttendanceId()
                ));

        if (attendance.getCheckOut() != null) {
            log.warn("Attendance already checked out: {}", request.getAttendanceId());
            throw new IllegalArgumentException("Already checked out at: " + attendance.getCheckOut());
        }

        attendance.setCheckOut(LocalDateTime.now());
        Attendance updatedAttendance = attendanceRepository.save(attendance);

        log.info("Check-out successful for member: {} at {}",
                attendance.getMember().getFullName(), updatedAttendance.getCheckOut());

        return AttendanceResponse.fromEntity(updatedAttendance);
    }

    /**
     * Get today's attendance list
     */
    @Transactional(readOnly = true)
    public List<AttendanceResponse> getTodayAttendance() {
        log.debug("Fetching today's attendance");

        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        List<Attendance> attendances = attendanceRepository
                .findByCheckInBetweenOrderByCheckInDesc(todayStart, todayEnd);

        log.info("Today's attendance count: {}", attendances.size());

        return attendances.stream()
                .map(AttendanceResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get attendance by date range
     */
    @Transactional(readOnly = true)
    public List<AttendanceResponse> getAttendanceByDateRange(
            LocalDate startDate, LocalDate endDate) {

        log.debug("Fetching attendance from {} to {}", startDate, endDate);

        LocalDateTime start = LocalDateTime.of(startDate, LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(endDate, LocalTime.MAX);

        List<Attendance> attendances = attendanceRepository
                .findByCheckInBetweenOrderByCheckInDesc(start, end);

        return attendances.stream()
                .map(AttendanceResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get member attendance history
     */
    @Transactional(readOnly = true)
    public List<MemberAttendanceItem> getMemberAttendanceHistory(
            UUID memberId, int days) {

        log.debug("Fetching attendance history for member: {} (last {} days)", memberId, days);

        LocalDateTime since = LocalDateTime.now().minusDays(days);
        LocalDateTime now = LocalDateTime.now();

        List<Attendance> attendances = attendanceRepository
                .findByMemberIdAndCheckInBetween(memberId, since, now);

        log.info("Found {} attendance records for member", attendances.size());

        return attendances.stream()
                .map(a -> {
                    MemberAttendanceItem item = new MemberAttendanceItem();
                    item.setDate(a.getCheckIn());
                    item.setCheckIn(a.getCheckIn());
                    item.setCheckOut(a.getCheckOut());
                    item.setSource(a.getSource().name());

                    if (a.getCheckOut() != null) {
                        long minutes = java.time.Duration.between(
                                a.getCheckIn(), a.getCheckOut()
                        ).toMinutes();
                        item.setDurationMinutes(minutes);
                    }

                    return item;
                })
                .collect(Collectors.toList());
    }

    /**
     * Get today's attendance summary
     */
    @Transactional(readOnly = true)
    public TodayAttendanceSummary getTodayAttendanceSummary() {
        log.debug("Fetching today's attendance summary");

        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        List<Attendance> attendances = attendanceRepository
                .findByCheckInBetweenOrderByCheckInDesc(todayStart, todayEnd);

        long totalCheckIns = attendances.size();
        long currentlyInGym = attendances.stream()
                .filter(a -> a.getCheckOut() == null)
                .count();
        long checkedOut = totalCheckIns - currentlyInGym;

        LocalDateTime lastCheckIn = attendances.isEmpty()
                ? null
                : attendances.get(0).getCheckIn();

        TodayAttendanceSummary summary = new TodayAttendanceSummary(
                totalCheckIns, currentlyInGym, checkedOut, lastCheckIn
        );

        log.info("Today's summary - Total: {}, In Gym: {}, Checked Out: {}",
                totalCheckIns, currentlyInGym, checkedOut);

        return summary;
    }

    /**
     * Get attendance count for today
     */
    @Transactional(readOnly = true)
    public long getTodayAttendanceCount() {
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        return attendanceRepository.countByCheckInBetween(todayStart, todayEnd);
    }

    /**
     * Check if member is currently checked in
     */
    @Transactional(readOnly = true)
    public boolean isMemberCheckedIn(UUID memberId) {
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        List<Attendance> todayAttendance = attendanceRepository
                .findByMemberIdAndCheckInBetween(memberId, todayStart, todayEnd);

        return todayAttendance.stream()
                .anyMatch(a -> a.getCheckOut() == null);
    }
}