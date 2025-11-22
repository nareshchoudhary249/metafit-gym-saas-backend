package com.metafit.dto.request.member;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RenewMembershipRequest {

    @NotNull(message = "Member ID is required")
    private UUID memberId;

    @NotNull(message = "Plan duration is required")
    @Min(value = 1, message = "Duration must be at least 1 day")
    private Integer durationDays;

    @NotNull(message = "Payment amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private java.math.BigDecimal amount;

    @NotNull(message = "Payment method is required")
    private String paymentMethod;

    private String transactionId;
    private String notes;
}