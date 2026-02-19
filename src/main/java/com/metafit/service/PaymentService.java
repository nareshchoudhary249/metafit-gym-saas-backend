package com.metafit.service;

import com.metafit.dto.request.payment.CreatePaymentRequest;
import com.metafit.dto.response.payment.PaymentResponse;
import com.metafit.dto.response.payment.RevenueReportResponse;

import java.time.LocalDate;
import java.util.List;

/**
 * Payment Service Interface
 * Handles payment-related business logic used by controllers.
 */
public interface PaymentService {

    PaymentResponse createPayment(CreatePaymentRequest request, String currentUsername);

    List<PaymentResponse> getMemberPaymentHistory(Long memberId);

    List<PaymentResponse> getPaymentsByDateRange(LocalDate startDate, LocalDate endDate);

    RevenueReportResponse getTodayRevenue();
}
