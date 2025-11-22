package com.metafit.service;

import com.metafit.dto.request.UpdateTenantConfigRequest;
import com.metafit.dto.response.TenantConfigResponse;

/**
 * Tenant Configuration Service Interface
 * Handles gym-specific branding and configuration
 */
public interface TenantConfigService {

    /**
     * Get current tenant configuration
     * Uses tenant from TenantContext
     * @return Tenant configuration
     */
    TenantConfigResponse getTenantConfig();

    /**
     * Get tenant configuration by tenant code
     * @param tenantCode Tenant code
     * @return Tenant configuration
     */
    TenantConfigResponse getTenantConfigByCode(String tenantCode);

    /**
     * Update tenant configuration
     * @param request Configuration update (branding, contact, settings)
     * @param updatedBy Username of updater
     * @return Updated configuration
     */
    TenantConfigResponse updateTenantConfig(UpdateTenantConfigRequest request, String updatedBy);

    /**
     * Initialize default configuration for new tenant
     * @param tenantCode Tenant code
     * @param tenantName Tenant name
     * @return Created configuration
     */
    TenantConfigResponse initializeDefaultConfig(String tenantCode, String tenantName);

    /**
     * Update branding (logo, colors, tagline)
     * @param tenantCode Tenant code
     * @param brandingJson JSON string with branding details
     * @param updatedBy Username of updater
     * @return Updated configuration
     */
    TenantConfigResponse updateBranding(String tenantCode, String brandingJson, String updatedBy);

    /**
     * Update contact information
     * @param tenantCode Tenant code
     * @param contactJson JSON string with contact details
     * @param updatedBy Username of updater
     * @return Updated configuration
     */
    TenantConfigResponse updateContactInfo(String tenantCode, String contactJson, String updatedBy);

    /**
     * Update gym settings (timezone, currency, working hours)
     * @param tenantCode Tenant code
     * @param settingsJson JSON string with settings
     * @param updatedBy Username of updater
     * @return Updated configuration
     */
    TenantConfigResponse updateSettings(String tenantCode, String settingsJson, String updatedBy);

    /**
     * Update feature flags for tenant
     * @param tenantCode Tenant code
     * @param featuresJson JSON string with feature flags
     * @param updatedBy Username of updater
     * @return Updated configuration
     */
    TenantConfigResponse updateFeatures(String tenantCode, String featuresJson, String updatedBy);

    /**
     * Get tenant name
     * @return Tenant name
     */
    String getTenantName();

    /**
     * Check if feature is enabled for tenant
     * @param featureName Feature name
     * @return true if enabled, false otherwise
     */
    boolean isFeatureEnabled(String featureName);
}