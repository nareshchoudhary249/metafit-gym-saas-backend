package com.metafit.dto.request.auth;

import com.metafit.constants.ValidationConstants;
import com.metafit.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating a new user (staff account)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50)
    @Pattern(regexp = ValidationConstants.USERNAME_REGEX,
            message = ValidationConstants.MSG_INVALID_USERNAME)
    private String username;


    @NotBlank(message = "Full name is required")
    @Size(min = ValidationConstants.MIN_NAME_LENGTH,
            max = ValidationConstants.MAX_NAME_LENGTH)
    private String fullName;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(regexp = ValidationConstants.PASSWORD_REGEX,
            message = ValidationConstants.MSG_INVALID_PASSWORD)
    private String password;

    @NotBlank(message = "Role is required")
    private Role role; // OWNER, ADMIN, RECEPTION, TRAINER

    @NotBlank(message = "Email is required")
    @Email(message = ValidationConstants.MSG_INVALID_EMAIL)
    @Size(max = ValidationConstants.MAX_EMAIL_LENGTH)
    private String email;


    @Pattern(regexp = ValidationConstants.PHONE_REGEX,
            message = ValidationConstants.MSG_INVALID_PHONE)
    private String phone;
}