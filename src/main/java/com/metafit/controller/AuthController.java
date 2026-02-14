package com.metafit.controller;

import com.metafit.dto.request.auth.ChangePasswordRequest;
import com.metafit.dto.request.auth.CreateUserRequest;
import com.metafit.dto.request.auth.LoginRequest;
import com.metafit.dto.request.auth.RefreshTokenRequest;
import com.metafit.dto.response.auth.LoginResponse;
import com.metafit.dto.response.auth.UserResponse;
import com.metafit.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for authentication and user management
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Login endpoint
     * POST /api/auth/login
     *
     * NOTE: This endpoint requires X-Tenant-ID header but NO JWT token
     * It's public and should be excluded from JWT filter
     *
     * @param request Login credentials (username, password)
     * @param tenantCode Tenant identifier from X-Tenant-ID header
     * @return Login response with JWT tokens
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request,
            @RequestHeader("X-Tenant-ID") String tenantCode) {

        log.info("POST /api/auth/login - Login attempt for user: {} in tenant: {}",
                request.getUsername(), tenantCode);

        LoginResponse response = authService.login(request, tenantCode);

        log.info("Login successful for user: {} with role: {}",
                request.getUsername(), response.getRole());

        return ResponseEntity.ok(response);
    }

    /**
     * Refresh JWT token
     * POST /api/auth/refresh
     *
     * @param request Refresh token request containing the refresh token
     * @return New login response with fresh access and refresh tokens
     */
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request) {

        log.info("POST /api/auth/refresh - Token refresh requested");

        LoginResponse response = authService.refreshToken(request.getRefreshToken());

        log.info("Token refreshed successfully");

        return ResponseEntity.ok(response);
    }

    /**
     * Logout endpoint
     * POST /api/auth/logout
     *
     * In stateless JWT architecture, logout is primarily client-side
     * (client deletes the token). This endpoint is for logging purposes.
     *
     * For enhanced security, you could implement:
     * - Token blacklisting (store invalidated tokens in Redis)
     * - Short token expiry times
     *
     * @return 204 No Content
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        String username = getCurrentUsername();
        log.info("POST /api/auth/logout - User logged out: {}", username);

        authService.logout(username);

        return ResponseEntity.noContent().build();
    }

    /**
     * Change password for authenticated user
     * POST /api/auth/change-password
     *
     * @param request Contains oldPassword and newPassword
     * @return 204 No Content on success
     */
    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @Valid @RequestBody ChangePasswordRequest request) {

        String username = getCurrentUsername();
        log.info("POST /api/auth/change-password - Password change requested for user: {}", username);

        authService.changePassword(request, username);

        log.info("Password changed successfully for user: {}", username);

        return ResponseEntity.noContent().build();
    }

    /**
     * Get current authenticated user information
     * GET /api/auth/me
     *
     * @return Current user details
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        String username = getCurrentUsername();
        log.info("GET /api/auth/me - Fetching current user: {}", username);

        UserResponse response = authService.getCurrentUser(username);

        return ResponseEntity.ok(response);
    }

    /**
     * Create new user (staff member)
     * POST /api/auth/users
     *
     * Requires: OWNER or ADMIN role
     *
     * @param request User creation details
     * @return Created user response with 201 Created status
     */
    @PostMapping("/users")
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody CreateUserRequest request) {

        String createdBy = getCurrentUsername();
        log.info("POST /api/auth/users - Creating new user: {} by: {}",
                request.getUsername(), createdBy);

        UserResponse response = authService.createUser(request);

        log.info("User created successfully: {}", response.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get all users in the tenant
     * GET /api/auth/users
     *
     * Requires: OWNER or ADMIN role
     *
     * @return List of all users
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        log.info("GET /api/auth/users - Fetching all users");

        List<UserResponse> users = authService.getAllUsers();

        log.info("Fetched {} users", users.size());

        return ResponseEntity.ok(users);
    }

    /**
     * Get user by ID
     * GET /api/auth/users/{id}
     *
     * @param id User ID
     * @return User details
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        log.info("GET /api/auth/users/{} - Fetching user", id);

        UserResponse response = authService.getUserById(id);

        return ResponseEntity.ok(response);
    }

    /**
     * Update user status (activate/deactivate)
     * PATCH /api/auth/users/{id}/status
     *
     * Requires: OWNER or ADMIN role
     *
     * @param id User ID
     * @param isActive New status (true = active, false = inactive)
     * @return Updated user response
     */
    @PatchMapping("/users/{id}/status")
    public ResponseEntity<UserResponse> updateUserStatus(
            @PathVariable Long id,
            @RequestParam Boolean isActive) {

        String updatedBy = getCurrentUsername();
        log.info("PATCH /api/auth/users/{}/status - Updating status to: {} by: {}",
                id, isActive, updatedBy);

        UserResponse response = authService.updateUserStatus(id, isActive);

        log.info("User status updated successfully: {}", response.getUsername());

        return ResponseEntity.ok(response);
    }

    /**
     * Reset user password (Admin only)
     * POST /api/auth/users/{id}/reset-password
     *
     * Requires: OWNER or ADMIN role
     *
     * @param id User ID whose password to reset
     * @param request Contains newPassword field
     * @return 204 No Content on success
     */
    @PostMapping("/users/{id}/reset-password")
    public ResponseEntity<Void> resetUserPassword(
            @PathVariable Long id,
            @RequestBody ResetPasswordRequest request) {

        String resetBy = getCurrentUsername();
        log.info("POST /api/auth/users/{}/reset-password - Resetting password by: {}",
                id, resetBy);

        authService.resetUserPassword(id, request.getNewPassword(), resetBy);

        log.info("Password reset successfully for user ID: {}", id);

        return ResponseEntity.noContent().build();
    }

    // ============= HELPER METHODS =============

    /**
     * Get username of currently authenticated user from SecurityContext
     * @return Username or "anonymous" if not authenticated
     */
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : "anonymous";
    }

    /**
     * Inner DTO class for password reset request
     */
    @lombok.Data
    public static class ResetPasswordRequest {
        @jakarta.validation.constraints.NotBlank
        @jakarta.validation.constraints.Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                message = "Password must be at least 8 characters with uppercase, lowercase, digit, and special character"
        )
        private String newPassword;
    }
}