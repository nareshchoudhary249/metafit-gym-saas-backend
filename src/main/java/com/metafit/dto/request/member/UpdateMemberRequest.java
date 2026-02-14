package com.metafit.dto.request.member;

import com.metafit.constants.ValidationConstants;
import com.metafit.entity.Member;
import com.metafit.enums.Gender;
import com.metafit.enums.MemberStatus;
import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Request DTO for updating member information
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMemberRequest {

    // Basic Information
    @NotBlank(message = ValidationConstants.MSG_REQUIRED)
    @Size(min = ValidationConstants.MIN_NAME_LENGTH,
            max = ValidationConstants.MAX_NAME_LENGTH)
    private String fullName;

    @Email(message = ValidationConstants.MSG_INVALID_EMAIL)
    @Size(max = ValidationConstants.MAX_EMAIL_LENGTH)
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = ValidationConstants.PHONE_REGEX,
            message = ValidationConstants.MSG_INVALID_PHONE)
    private String phone;

    // MISSING FIELDS - NOW ADDED
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

    // Status Management
    private MemberStatus status; // ACTIVE, SUSPENDED, CANCELLED

    @Size(max = ValidationConstants.MAX_NOTES_LENGTH)
    private String notes; // Reason for status change or any updates
}