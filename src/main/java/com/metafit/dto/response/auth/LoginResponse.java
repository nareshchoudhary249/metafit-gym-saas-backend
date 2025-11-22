package com.metafit.dto.response.auth;

import com.metafit.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String token;
    private String refreshToken;
    private Long expiresIn; // milliseconds
    private UserInfo user;
    private TenantInfo tenant;
    private Boolean forcePasswordChange;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private UUID id;
        private String username;
        private String fullName;
        private String role;
        private String email;
        private String phone;
        private LocalDateTime lastLogin;

        public static UserInfo fromEntity(User user) {
            UserInfo info = new UserInfo();
            info.setId(user.getId());
            info.setUsername(user.getUsername());
            info.setFullName(user.getFullName());
            info.setRole(user.getRole().name());
            info.setEmail(user.getEmail());
            info.setPhone(user.getPhone());
            info.setLastLogin(user.getLastLogin());
            return info;
        }
    }