package com.metafit.dto.response.payment;

import com.metafit.entity.Payment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private Long id;
    private Long memberId;
    private String memberName;
    private String memberPhone;
    private BigDecimal amount;
    private String method;
    private LocalDateTime paidAt;
    private String transactionId;
    private String notes;

    public static PaymentResponse fromEntity(Payment payment) {
        PaymentResponse response = new PaymentResponse();
        response.setId(payment.getId());
        response.setMemberId(payment.getMember().getId());
        response.setMemberName(payment.getMember().getFullName());
        response.setMemberPhone(payment.getMember().getPhone());
        response.setAmount(payment.getAmount());
        response.setMethod(payment.getPaymentMethod().name());
        response.setPaidAt(payment.getPaymentDate());
        response.setTransactionId(payment.getTransactionId());
        response.setNotes(payment.getNotes());
        return response;
    }
}
