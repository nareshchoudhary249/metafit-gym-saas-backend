package com.metafit.dto.response.payment;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private UUID id;
    private UUID memberId;
    private String memberName;
    private String memberPhone;
    private BigDecimal amount;
    private String method;
    private LocalDateTime paidAt;
    private String paymentFor;
    private String transactionId;
    private String notes;
    private String receivedBy;

    public static PaymentResponse fromEntity(Payment payment) {
        PaymentResponse response = new PaymentResponse();
        response.setId(payment.getId());
        response.setMemberId(payment.getMember().getId());
        response.setMemberName(payment.getMember().getFullName());
        response.setMemberPhone(payment.getMember().getPhone());
        response.setAmount(payment.getAmount());
        response.setMethod(payment.getMethod().name());
        response.setPaidAt(payment.getPaidAt());
        response.setPaymentFor(payment.getPaymentFor());
        response.setTransactionId(payment.getTransactionId());
        response.setNotes(payment.getNotes());
        response.setReceivedBy(payment.getReceivedBy());
        return response;
    }
}
