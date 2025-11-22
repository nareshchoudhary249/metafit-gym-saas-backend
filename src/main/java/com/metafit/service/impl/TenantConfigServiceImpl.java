package com.metafit.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.metafit.dto.response.config.TenantConfigResponse;
import com.metafit.entity.master.Tenant;
import com.metafit.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for tenant configuration
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TenantConfigService {

    private final TenantRepository tenantRepository;
    private final ObjectMapper objectMapper;

    /**
     * Get current tenant configuration
     */
    @Transactional(readOnly = true)
    public TenantConfigResponse getCurrentTenantConfig() {
        String tenantCode = TenantContext.getTenantId();
        log.debug("Fetching configuration for tenant: {}", tenantCode);

        Tenant tenant = tenantRepository.findByCode(tenantCode)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tenant not found with code: " + tenantCode
                ));

        TenantConfigResponse config = new TenantConfigResponse();

        // Parse JSONB config if exists
        if (tenant.getConfig() != null && !tenant.getConfig().isEmpty()) {
            try {
                // Parse the JSONB string to TenantConfigResponse
                config = objectMapper.readValue(tenant.getConfig(), TenantConfigResponse.class);
            } catch (JsonProcessingException e) {
                log.warn("Failed to parse tenant config JSON, using defaults", e);
            }
        }

        // Set basic info from tenant table
        config.setGymName(tenant.getName());

        // Set defaults if not in config
        if (config.getPrimaryColor() == null) {
            config.setPrimaryColor("#10B981");
        }
        if (config.getAccentColor() == null) {
            config.setAccentColor("#3B82F6");
        }

        log.info("Retrieved configuration for gym: {}", config.getGymName());
        return config;
    }

    /**
     * Update tenant configuration
     */
    @Transactional
    public TenantConfigResponse updateTenantConfig(TenantConfigResponse configRequest) {
        String tenantCode = TenantContext.getTenantId();
        log.info("Updating configuration for tenant: {}", tenantCode);

        Tenant tenant = tenantRepository.findByCode(tenantCode)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tenant not found with code: " + tenantCode
                ));

        try {
            // Convert config to JSON string
            String configJson = objectMapper.writeValueAsString(configRequest);
            tenant.setConfig(configJson);

            tenantRepository.save(tenant);
            log.info("Configuration updated successfully for: {}", tenantCode);

            return configRequest;
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize tenant config", e);
            throw new RuntimeException("Failed to update configuration", e);
        }
    }
}