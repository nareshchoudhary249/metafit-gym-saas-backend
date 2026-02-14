package com.metafit.dto.response.auth;

import com.metafit.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for authentication endpoints
 * Returned after successful login
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    // JWT Tokens
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private Long expiresIn; // Seconds until token expires

    // User Information
    private Long userId;
    private String username;
    private String fullName;
    private String email;
    private Role role;

    // Tenant Information
    private Long tenantId;
    private String tenantName;
    private String tenantCode; // Subdomain or unique code
    private String subscriptionPlan; // BASIC, STANDARD, PREMIUM

    // Permissions/Features (Optional)
    private Boolean canManageMembers;
    private Boolean canManagePayments;
    private Boolean canManageStaff;
    private Boolean canViewReports;

    // Subscription Status
    private Boolean subscriptionActive;
    private String subscriptionExpiryDate;

    /**
     * Create a simple auth response (minimal fields)
     */
    public static AuthResponse simple(String accessToken, Long userId, String username, Role role) {
        return AuthResponse.builder()
                .accessToken(accessToken)
                .userId(userId)
                .username(username)
                .role(role)
                .tokenType("Bearer")
                .build();
    }
}
