package com.metafit.dto.request.member;

import com.metafit.enums.PaymentMethod;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RenewMembershipRequest {

    @NotNull(message = "Member ID is required")
    private Long memberId;

    @NotNull(message = "Duration in months is required")
    @Min(value = 1, message = "Minimum duration is 1 month")
    @Max(value = 24, message = "Maximum duration is 24 months")
    private Integer durationMonths;

    // MISSING FIELDS - NOW ADDED
    @NotNull(message = "New end date is required")
    @Future(message = "End date must be in the future")
    private LocalDate newEndDate; // Explicit end date (calculated or custom)

    @NotNull(message = "Plan duration is required")
    @Min(value = 1, message = "Duration must be at least 1 day")
    private Integer durationDays;

    @NotBlank(message = "Membership plan is required")
    @Size(max = 100)
    private String membershipPlan; // e.g., "3 Month Premium", "6 Month Standard"

    @NotNull(message = "Payment amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount; // Actual amount paid (may include discount)

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    @Size(max = 255)
    private String transactionId;

    @Size(max = 500)
    private String remarks; // Any notes about the renewal

    private Boolean applyDiscount; // Whether discount was applied
    private String notes;

    @DecimalMin(value = "0.00")
    @DecimalMax(value = "100.00")
    private BigDecimal discountPercentage; // Discount applied (0-100%)
}