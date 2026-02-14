package com.metafit.service;

import com.metafit.dto.request.auth.ChangePasswordRequest;
import com.metafit.dto.request.auth.CreateUserRequest;
import com.metafit.dto.request.auth.LoginRequest;
import com.metafit.dto.response.auth.LoginResponse;
import com.metafit.dto.response.auth.UserResponse;

import java.util.List;

/**
 * Authentication Service Interface
 * Handles authentication, authorization, and user management
 */
public interface AuthService {

    /**
     * Authenticate user and generate JWT token
     * @param request Login credentials (username, password)
     * @param tenantCode Tenant identifier from X-Tenant-ID header
     * @return Login response with JWT tokens and user info
     */
    LoginResponse login(LoginRequest request, String tenantCode);

    /**
     * Refresh JWT token
     * @param refreshToken Refresh token
     * @return New login response with fresh tokens
     */
    LoginResponse refreshToken(String refreshToken);

    /**
     * Logout user (invalidate token if needed)
     * Note: In stateless JWT, this is mainly for logging
     * @param username Username of the user logging out
     */
    void logout(String username);

    /**
     * Change user password
     * @param request Password change details (oldPassword, newPassword)
     * @param username Username of the user
     */
    void changePassword(ChangePasswordRequest request, String username);

    /**
     * Get current user information
     * @param username Username from JWT token
     * @return User response with details
     */
    UserResponse getCurrentUser(String username);

    /**
     * Create a new user (staff member)
     * Only Owner/Admin can create users
     * @param request User creation details
     * @return Created user response
     */
    UserResponse createUser(CreateUserRequest request);

    /**
     * Get all users in the current tenant
     * @return List of all users
     */
    List<UserResponse> getAllUsers();

    /**
     * Get user by ID
     * @param id User ID
     * @return User response
     */
    UserResponse getUserById(Long id);

    /**
     * Update user status (activate/deactivate)
     * @param id User ID
     * @param isActive New status (true = active, false = inactive)
     * @return Updated user response
     */
    UserResponse updateUserStatus(Long id, Boolean isActive);

    /**
     * Reset user password (admin function)
     * @param userId User ID whose password to reset
     * @param newPassword New password
     * @param resetBy Username of admin performing reset
     */
    void resetUserPassword(Long userId, String newPassword, String resetBy);

    /**
     * Check if user must change password on first login
     * @param username Username
     * @return true if password change required
     */
    boolean mustChangePassword(String username);

    /**
     * Validate JWT token
     * @param token JWT token
     * @return true if valid, false otherwise
     */
    boolean validateToken(String token);

    /**
     * Get username from JWT token
     * @param token JWT token
     * @return Username extracted from token
     */
    String getUsernameFromToken(String token);
}