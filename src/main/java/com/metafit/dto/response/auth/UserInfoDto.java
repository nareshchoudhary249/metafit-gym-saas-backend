package com.metafit.dto.response.auth;

import com.metafit.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user information in authentication responses
 * Contains user details without sensitive information like password
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoDto {

    private Long id;

    private String username;

    private String fullName;

    private String email;

    private String phone;

    private Role role;

    private String tenantCode;

    private boolean active;

    private boolean mustChangePassword;
}