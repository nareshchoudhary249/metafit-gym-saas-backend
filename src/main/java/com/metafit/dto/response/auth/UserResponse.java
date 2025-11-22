package com.metafit.dto.response.auth;

import java.time.LocalDateTime;
import java.util.UUID;

import com.metafit.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private UUID id;
    private String username;
    private String fullName;
    private String role;
    private String email;
    private String phone;
    private Boolean isActive;
    private LocalDateTime lastLogin;
    private Boolean forcePasswordChange;
    private LocalDateTime createdAt;

    public static UserResponse fromEntity(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setFullName(user.getFullName());
        response.setRole(user.getRole().name());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setIsActive(user.getIsActive());
        response.setLastLogin(user.getLastLogin());
        response.setForcePasswordChange(user.getForcePasswordChange());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }
}