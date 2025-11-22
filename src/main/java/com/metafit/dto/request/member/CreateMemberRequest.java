package com.metafit.dto.request.member;

import com.metafit.entity.Member;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


// ============= CREATE MEMBER REQUEST =============

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMemberRequest {

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String fullName;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid Indian phone number")
    private String phone;

    @Email(message = "Invalid email format")
    private String email;

    @NotNull(message = "Gender is required")
    private Member.Gender gender;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    private String address;
    private String emergencyContact;
    private String bloodGroup;
    private String notes;
}
