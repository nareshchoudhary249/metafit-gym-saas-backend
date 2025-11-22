package com.metafit.controller;

import com.metafit.service.MemberService;
import com.metafit.service.impl.AttendanceService;
import com.metafit.service.impl.PaymentService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * REST controller for dashboard statistics
 */
@Slf4j
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final MemberService memberService;
    private final AttendanceService attendanceService;
    private final PaymentService paymentService;

    /**
     * Get dashboard statistics
     * GET /api/dashboard/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<DashboardStats> getDashboardStats() {
        log.info("GET /api/dashboard/stats - Fetching dashboard statistics");

        DashboardStats stats = new DashboardStats();

        // Member stats
        stats.setActiveMembers(memberService.getActiveMembersCount());
        stats.setExpiringLittle(memberService.getExpiringMembers().size());

        // Attendance stats
        stats.setTodayCheckIns(attendanceService.getTodayAttendanceCount());

        // Revenue stats
        var todayRevenue = paymentService.getTodayRevenue();
        stats.setTodayRevenue(todayRevenue.getTotalRevenue());
        stats.setTodayTransactions(todayRevenue.getTransactionCount());

        log.info("Dashboard stats compiled - Active: {}, Check-ins: {}, Revenue: {}",
                stats.getActiveMembers(), stats.getTodayCheckIns(), stats.getTodayRevenue());

        return ResponseEntity.ok(stats);
    }

    @Data
    @AllArgsConstructor
    public static class DashboardStats {
        private Long activeMembers = 0L;
        private Long todayCheckIns = 0L;
        private Long expiringLittle = 0L;
        private BigDecimal todayRevenue = BigDecimal.ZERO;
        private Long todayTransactions = 0L;

        public DashboardStats() {}
    }
}