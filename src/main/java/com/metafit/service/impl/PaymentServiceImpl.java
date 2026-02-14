package com.metafit.service.impl;

import com.metafit.dto.request.payment.CreatePaymentRequest;
import com.metafit.dto.response.payment.PaymentResponse;
import com.metafit.dto.response.payment.RevenueReportResponse;
import com.metafit.entity.Member;
import com.metafit.entity.Payment;
import com.metafit.enums.PaymentMethod;
import com.metafit.exception.ResourceNotFoundException;
import com.metafit.repository.MemberRepository;
import com.metafit.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final MemberRepository memberRepository;

    public PaymentResponse createPayment(CreatePaymentRequest request, String currentUsername) {
        log.info("Creating payment for member: {}, Amount: {}",
                request.getMemberId(), request.getAmount());

        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Member not found with ID: " + request.getMemberId()
                )).getMember();

        Payment payment = new Payment();
        payment.setMember(member);
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(PaymentMethod.valueOf(request.getMethod().toUpperCase()));
        payment.setPaidAt(LocalDateTime.now());
        payment.setPaymentFor(request.getPaymentFor());
        payment.setTransactionId(request.getTransactionId());
        payment.setNotes(request.getNotes());
        payment.setReceivedBy(currentUsername);

        Payment savedPayment = paymentRepository.save(payment);

        log.info("Payment created successfully: {} for member: {}",
                savedPayment.getId(), member.getFullName());

        return PaymentResponse.fromEntity(savedPayment);
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getMemberPaymentHistory(UUID memberId) {
        log.debug("Fetching payment history for member: {}", memberId);

        List<Payment> payments = paymentRepository.findByMemberIdOrderByPaidAtDesc(memberId);

        return payments.stream()
                .map(PaymentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByDateRange(LocalDate startDate, LocalDate endDate) {
        log.debug("Fetching payments from {} to {}", startDate, endDate);

        LocalDateTime start = LocalDateTime.of(startDate, LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(endDate, LocalTime.MAX);

        List<Payment> payments = paymentRepository.findByPaidAtBetweenOrderByPaidAtDesc(start, end);

        return payments.stream()
                .map(PaymentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RevenueReportResponse getTodayRevenue() {
        log.debug("Calculating today's revenue");

        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        List<Payment> payments = paymentRepository.findByPaidAtBetweenOrderByPaidAtDesc(todayStart, todayEnd);

        BigDecimal totalRevenue = payments.stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, BigDecimal> breakdown = payments.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getMethod().name(),
                        Collectors.reducing(BigDecimal.ZERO, Payment::getAmount, BigDecimal::add)
                ));

        log.info("Today's revenue: {} from {} transactions", totalRevenue, payments.size());

        return new RevenueReportResponse(totalRevenue, (long) payments.size(), breakdown);
    }
}