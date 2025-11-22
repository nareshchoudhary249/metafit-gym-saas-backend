package com.metafit.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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
     * NOTE: This endpoint requires X-Tenant-ID header but no JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request,
            @RequestHeader("X-Tenant-ID") String tenantCode) {

        log.info("POST /api/auth/login - Login attempt for user: {} in tenant: {}",
                request.getUsername(), tenantCode);

        LoginResponse response = authService.login(request, tenantCode);

        log.info("Login successful for user: {}", request.getUsername());
        return ResponseEntity.ok(response);
    }

    /**
     * Refresh token
     * POST /api/auth/refresh
     */
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request) {

        log.info("POST /api/auth/refresh - Token refresh requested");

        LoginResponse response = authService.refreshToken(request.getRefreshToken());

        return ResponseEntity.ok(response);
    }

    /**
     * Logout (client-side token deletion, server just logs)
     * POST /api/auth/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        String username = getCurrentUsername();
        log.info("POST /api/auth/logout - User logged out: {}", username);

        // In a stateless JWT system, logout is handled client-side
        // Here we just log the event

        return ResponseEntity.noContent().build();
    }

    /**
     * Change password
     * POST /api/auth/change-password
     */
    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @Valid @RequestBody ChangePasswordRequest request) {

        String username = getCurrentUsername();
        log.info("POST /api/auth/change-password - Password change for user: {}", username);

        authService.changePassword(request, username);

        return ResponseEntity.noContent().build();
    }

    /**
     * Get current user info
     * GET /api/auth/me
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        String username = getCurrentUsername();
        log.info("GET /api/auth/me - Fetching current user: {}", username);

        // This would need to be implemented to fetch user by username
        // For now, return basic info from security context

        return ResponseEntity.ok(new UserResponse());
    }

    /**
     * Create new user (Owner/Admin only)
     * POST /api/auth/users
     */
    @PostMapping("/users")
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody CreateUserRequest request) {

        log.info("POST /api/auth/users - Creating new user: {}", request.getUsername());

        UserResponse response = authService.createUser(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get all users (Owner/Admin only)
     * GET /api/auth/users
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        log.info("GET /api/auth/users - Fetching all users");

        List<UserResponse> users = authService.getAllUsers();

        return ResponseEntity.ok(users);
    }

    /**
     * Get user by ID
     * GET /api/auth/users/{id}
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
        log.info("GET /api/auth/users/{} - Fetching user", id);

        UserResponse response = authService.getUserById(id);

        return ResponseEntity.ok(response);
    }

    /**
     * Update user status (activate/deactivate)
     * PATCH /api/auth/users/{id}/status
     */
    @PatchMapping("/users/{id}/status")
    public ResponseEntity<UserResponse> updateUserStatus(
            @PathVariable UUID id,
            @RequestParam Boolean isActive) {

        log.info("PATCH /api/auth/users/{}/status - Updating status to: {}", id, isActive);

        UserResponse response = authService.updateUserStatus(id, isActive);

        return ResponseEntity.ok(response);
    }

    /**
     * Reset user password (Admin only)
     * POST /api/auth/users/{id}/reset-password
     */
    @PostMapping("/users/{id}/reset-password")
    public ResponseEntity<Void> resetUserPassword(
            @PathVariable UUID id,
            @RequestBody String newPassword) {

        log.info("POST /api/auth/users/{}/reset-password - Resetting password", id);

        authService.resetUserPassword(id, newPassword);

        return ResponseEntity.noContent().build();
    }

    // ============= HELPER METHODS =============

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : "anonymous";
    }
}