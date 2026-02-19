package com.metafit.controller;

import com.metafit.dto.request.payment.CreatePaymentRequest;
import com.metafit.dto.response.payment.PaymentResponse;
import com.metafit.dto.response.payment.RevenueReportResponse;
import com.metafit.service.PaymentService;
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

@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(@Valid @RequestBody CreatePaymentRequest request) {
        String username = getCurrentUsername();
        log.info("POST /api/payments - Creating payment for member: {}", request.getMemberId());

        PaymentResponse response = paymentService.createPayment(request, username);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<PaymentResponse>> getMemberPaymentHistory(@PathVariable Long memberId) {
        log.info("GET /api/payments/member/{}", memberId);

        List<PaymentResponse> payments = paymentService.getMemberPaymentHistory(memberId);

        return ResponseEntity.ok(payments);
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getPaymentsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("GET /api/payments?startDate={}&endDate={}", startDate, endDate);

        List<PaymentResponse> payments = paymentService.getPaymentsByDateRange(startDate, endDate);

        return ResponseEntity.ok(payments);
    }

    @GetMapping("/today/revenue")
    public ResponseEntity<RevenueReportResponse> getTodayRevenue() {
        log.info("GET /api/payments/today/revenue");

        RevenueReportResponse report = paymentService.getTodayRevenue();

        return ResponseEntity.ok(report);
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : "system";
    }
}
