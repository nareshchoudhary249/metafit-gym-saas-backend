package com.metafit.service;

import com.metafit.dto.request.ChangePasswordRequest;
import com.metafit.dto.request.CreateUserRequest;
import com.metafit.dto.request.LoginRequest;
import com.metafit.dto.response.AuthResponse;
import com.metafit.dto.response.UserResponse;

import java.util.List;

/**
 * Authentication Service Interface
 * Handles authentication, authorization, and user management
 */
public interface AuthService {

    /**
     * Authenticate user and generate JWT token
     * @param request Login credentials (username, password, tenantCode)
     * @return Auth response with token and user info
     */
    AuthResponse login(LoginRequest request);

    /**
     * Refresh JWT token
     * @param refreshToken Refresh token
     * @return New auth response with fresh tokens
     */
    AuthResponse refreshToken(String refreshToken);

    /**
     * Logout user (invalidate token if needed)
     * @param token JWT token
     */
    void logout(String token);

    /**
     * Change user password
     * @param request Password change details (oldPassword, newPassword)
     * @param username Username of the user
     */
    void changePassword(ChangePasswordRequest request, String username);

    /**
     * Get current user information
     * @param username Username
     * @return User response
     */
    UserResponse getCurrentUser(String username);

    /**
     * Create a new user (staff member)
     * @param request User creation details
     * @param createdBy Username of creator
     * @return Created user response
     */
    UserResponse createUser(CreateUserRequest request, String createdBy);

    /**
     * Get all users in the tenant
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
     * @param active New status
     */
    void updateUserStatus(Long id, boolean active);

    /**
     * Reset user password (admin function)
     * @param userId User ID
     * @param newPassword New password
     * @param resetBy Username of admin resetting
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
     * @return Username
     */
    String getUsernameFromToken(String token);
}