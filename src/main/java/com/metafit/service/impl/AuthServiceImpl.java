package com.metafit.service.impl;

import com.metafit.dto.request.auth.ChangePasswordRequest;
import com.metafit.dto.request.auth.CreateUserRequest;
import com.metafit.dto.request.auth.LoginRequest;
import com.metafit.dto.response.TenantInfo;
import com.metafit.dto.response.auth.LoginResponse;
import com.metafit.dto.response.auth.UserResponse;
import com.metafit.entity.User;
import com.metafit.entity.master.Tenant;
import com.metafit.enums.TenantStatus;
import com.metafit.exception.ResourceNotFoundException;
import com.metafit.exception.UnauthorizedException;
import com.metafit.repository.UserRepository;
import com.metafit.repository.master.TenantRepository;
import com.metafit.security.jwt.JwtUtil;
import com.metafit.tenancy.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for authentication and user management
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl {

    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;

    /**
     * Authenticate user and generate JWT token
     */
    @Transactional
    public LoginResponse login(LoginRequest request, String tenantCode) {
        log.info("Login attempt for user: {} in tenant: {}", request.getUsername(), tenantCode);

        // Validate tenant
        Tenant tenant = tenantRepository.findByCode(tenantCode)
                .orElseThrow(() -> {
                    log.error("Invalid tenant code: {}", tenantCode);
                    return new UnauthorizedException("Invalid tenant code");
                });

        // Check tenant status
        if (!tenant.getStatus().equals(TenantStatus.ACTIVE)) {
            log.error("Tenant is not active: {}", tenantCode);
            throw new UnauthorizedException("Your subscription is not active. Please contact support.");
        }

        // Set tenant context for database routing
        TenantContext.setTenantId(tenantCode);

        // Find user
        User user = userRepository.findByUsernameAndIsActiveTrue(request.getUsername())
                .orElseThrow(() -> {
                    log.warn("User not found or inactive: {}", request.getUsername());
                    return new UnauthorizedException("Invalid username or password");
                });

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            log.warn("Invalid password for user: {}", request.getUsername());
            throw new UnauthorizedException("Invalid username or password");
        }

        // Update last login
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        // Generate tokens
        String token = jwtUtil.generateToken(
                user.getUsername(),
                tenantCode,
                user.getRole().name()
        );

        String refreshToken = jwtUtil.generateToken(
                user.getUsername() + ":refresh",
                tenantCode,
                user.getRole().name()
        );

        log.info("Login successful for user: {} in tenant: {}", request.getUsername(), tenantCode);

        // Build response
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setRefreshToken(refreshToken);
        response.setExpiresIn(jwtExpiration);
        response.setUser(LoginResponse.UserInfo.fromEntity(user));
        response.setForcePasswordChange(user.getForcePasswordChange());

        TenantInfo tenantInfo = new TenantInfo();
        tenantInfo.setCode(tenant.getCode());
        tenantInfo.setName(tenant.getName());
        tenantInfo.setStatus(tenant.getStatus().name());
        response.setTenant(tenantInfo);

        return response;
    }

    /**
     * Refresh JWT token
     */
    public LoginResponse refreshToken(String refreshToken) {
        log.debug("Token refresh requested");

        if (!jwtUtil.validateToken(refreshToken)) {
            log.warn("Invalid refresh token");
            throw new UnauthorizedException("Invalid or expired refresh token");
        }

        String username = jwtUtil.getUsernameFromToken(refreshToken).replace(":refresh", "");
        String tenantCode = jwtUtil.getTenantFromToken(refreshToken);
        String role = jwtUtil.getRoleFromToken(refreshToken);

        // Generate new tokens
        String newToken = jwtUtil.generateToken(username, tenantCode, role);
        String newRefreshToken = jwtUtil.generateToken(username + ":refresh", tenantCode, role);

        LoginResponse response = new LoginResponse();
        response.setToken(newToken);
        response.setRefreshToken(newRefreshToken);
        response.setExpiresIn(jwtExpiration);

        log.info("Token refreshed for user: {} in tenant: {}", username, tenantCode);

        return response;
    }

    /**
     * Change user password
     */
    @Transactional
    public void changePassword(ChangePasswordRequest request, String username) {
        log.info("Password change requested for user: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            log.warn("Current password verification failed for user: {}", username);
            throw new UnauthorizedException("Current password is incorrect");
        }

        // Verify password confirmation
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("New password and confirmation do not match");
        }

        // Update password
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        user.setPasswordChangedAt(LocalDateTime.now());
        user.setForcePasswordChange(false);

        userRepository.save(user);

        log.info("Password changed successfully for user: {}", username);
    }

    /**
     * Create new user (Owner/Admin only)
     */
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        log.info("Creating new user: {}", request.getUsername());

        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            log.warn("Username already exists: {}", request.getUsername());
            throw new IllegalArgumentException("Username already exists");
        }

        // Validate role
        User.UserRole role;
        try {
            role = User.UserRole.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role: " + request.getRole());
        }

        // Create user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setFullName(request.getFullName());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setIsActive(true);
        user.setForcePasswordChange(true);

        User savedUser = userRepository.save(user);

        log.info("User created successfully: {}", savedUser.getUsername());

        return UserResponse.fromEntity(savedUser);
    }

    /**
     * Get all users
     */
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        log.debug("Fetching all users");

        List<User> users = userRepository.findAll();

        return users.stream()
                .map(UserResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get user by ID
     */
    @Transactional(readOnly = true)
    public UserResponse getUserById(UUID id) {
        log.debug("Fetching user by ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        return UserResponse.fromEntity(user);
    }

    /**
     * Update user status (activate/deactivate)
     */
    @Transactional
    public UserResponse updateUserStatus(UUID id, Boolean isActive) {
        log.info("Updating user status: {} -> {}", id, isActive);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        user.setIsActive(isActive);
        User updatedUser = userRepository.save(user);

        log.info("User status updated: {} is now {}", user.getUsername(), isActive ? "active" : "inactive");

        return UserResponse.fromEntity(updatedUser);
    }

    /**
     * Reset user password (Admin only)
     */
    @Transactional
    public void resetUserPassword(UUID userId, String newPassword) {
        log.info("Password reset for user ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setForcePasswordChange(true);
        user.setPasswordChangedAt(LocalDateTime.now());

        userRepository.save(user);

        log.info("Password reset successfully for user: {}", user.getUsername());
    }
}