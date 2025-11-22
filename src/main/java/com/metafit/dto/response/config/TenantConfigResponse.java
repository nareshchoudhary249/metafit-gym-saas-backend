package com.metafit.dto.response.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for tenant configuration response
 * Sent to frontend to customize UI per client
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TenantConfigResponse {

    @JsonProperty("gym_name")
    private String gymName;

    private String tagline;

    @JsonProperty("logo_url")
    private String logoUrl;

    @JsonProperty("primary_color")
    private String primaryColor = "#10B981";

    @JsonProperty("accent_color")
    private String accentColor = "#3B82F6";

    private ContactInfo contact;
    private Settings settings;
    private Features features;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContactInfo {
        private String address;
        private String phone;
        private String email;
        private String website;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Settings {
        private String timezone = "Asia/Kolkata";
        private String currency = "INR";

        @JsonProperty("working_hours")
        private String workingHours = "6:00 AM - 10:00 PM";

        @JsonProperty("sms_notifications")
        private Boolean smsNotifications = false;

        @JsonProperty("email_notifications")
        private Boolean emailNotifications = true;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Features {
        @JsonProperty("biometric_enabled")
        private Boolean biometricEnabled = false;

        @JsonProperty("rfid_enabled")
        private Boolean rfidEnabled = false;

        @JsonProperty("store_module")
        private Boolean storeModule = false;

        @JsonProperty("trainer_module")
        private Boolean trainerModule = true;

        @JsonProperty("reports_enabled")
        private Boolean reportsEnabled = true;
    }
}