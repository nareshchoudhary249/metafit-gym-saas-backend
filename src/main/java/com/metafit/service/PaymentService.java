package com.metafit.service;

import com.metafit.dto.request.RecordPaymentRequest;
import com.metafit.dto.response.PaymentResponse;
import com.metafit.dto.response.PaymentSummaryResponse;

import java.time.LocalDate;
import java.util.List;

/**
 * Payment Service Interface
 * Handles all payment-related business logic
 */
public interface PaymentService {

    /**
     * Record a new payment
     * @param request Payment details (memberId, amount, method, notes)
     * @param recordedBy Username of staff who recorded payment
     * @return Payment response
     */
    PaymentResponse recordPayment(RecordPaymentRequest request, String recordedBy);

    /**
     * Get payment by ID
     * @param id Payment ID
     * @return Payment response
     */
    PaymentResponse getPaymentById(Long id);

    /**
     * Get all payments for a member
     * @param memberId Member ID
     * @return List of payment records
     */
    List<PaymentResponse> getMemberPayments(Long memberId);

    /**
     * Get payments between dates
     * @param startDate Start date
     * @param endDate End date
     * @return List of payment records
     */
    List<PaymentResponse> getPaymentsBetweenDates(LocalDate startDate, LocalDate endDate);

    /**
     * Get today's payments
     * @return List of today's payment records
     */
    List<PaymentResponse> getTodayPayments();

    /**
     * Get today's total revenue
     * @return Total revenue amount for today
     */
    Double getTodayRevenue();

    /**
     * Get revenue between dates
     * @param startDate Start date
     * @param endDate End date
     * @return Total revenue for date range
     */
    Double getRevenueBetweenDates(LocalDate startDate, LocalDate endDate);

    /**
     * Get today's payment summary
     * @return Summary with total revenue, count, breakdown by payment method
     */
    PaymentSummaryResponse getTodayPaymentSummary();

    /**
     * Get payment summary for date range
     * @param startDate Start date
     * @param endDate End date
     * @return Payment summary
     */
    PaymentSummaryResponse getPaymentSummaryBetweenDates(LocalDate startDate, LocalDate endDate);

    /**
     * Get total payments made by a member
     * @param memberId Member ID
     * @return Total amount paid
     */
    Double getTotalPaymentsByMember(Long memberId);

    /**
     * Get revenue breakdown by payment method for today
     * @return Map of payment method to revenue amount
     */
    PaymentSummaryResponse getRevenueByPaymentMethod();

    /**
     * Get this month's revenue
     * @return Total revenue for current month
     */
    Double getThisMonthRevenue();

    /**
     * Get payment count for today
     * @return Count of today's payments
     */
    Long getTodayPaymentCount();
}