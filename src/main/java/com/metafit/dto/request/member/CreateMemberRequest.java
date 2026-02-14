package com.metafit.dto.request.member;

import com.metafit.constants.ValidationConstants;
import com.metafit.entity.Member;
import com.metafit.enums.Gender;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request DTO for creating a new member
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMemberRequest {

    // Basic Information
    @NotBlank(message = ValidationConstants.MSG_REQUIRED)
    @Size(min = ValidationConstants.MIN_NAME_LENGTH,
            max = ValidationConstants.MAX_NAME_LENGTH,
            message = ValidationConstants.MSG_NAME_TOO_SHORT)
    private String fullName;

    @Email(message = ValidationConstants.MSG_INVALID_EMAIL)
    @Size(max = ValidationConstants.MAX_EMAIL_LENGTH)
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = ValidationConstants.PHONE_REGEX,
            message = ValidationConstants.MSG_INVALID_PHONE)
    private String phone;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @Size(max = ValidationConstants.MAX_ADDRESS_LENGTH)
    private String address;

    // Emergency Contact
    @Pattern(regexp = ValidationConstants.PHONE_REGEX,
            message = "Invalid emergency contact number")
    private String emergencyContact;

    @Size(max = 10)
    private String bloodGroup;

    // Membership Information (MISSING FIELDS - NOW ADDED)
    @NotNull(message = "Membership start date is required")
    @FutureOrPresent(message = "Membership start date cannot be in the past")
    private LocalDate membershipStartDate;

    @NotNull(message = "Membership end date is required")
    private LocalDate membershipEndDate;

    @NotBlank(message = "Membership plan is required")
    @Size(max = 100)
    private String membershipPlan; // e.g., "3 Month Premium", "1 Month Basic"

    @NotNull(message = "Membership amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    @DecimalMax(value = "999999.99", message = "Amount too large")
    private BigDecimal membershipAmount;

    // Payment Information (for first payment)
    @NotBlank(message = "Payment method is required")
    private String paymentMethod; // CASH, UPI, CARD, etc.

    @Size(max = 255)
    private String transactionId; // For digital payments

    @Size(max = ValidationConstants.MAX_NOTES_LENGTH)
    private String notes; // Any additional notes about the member
}