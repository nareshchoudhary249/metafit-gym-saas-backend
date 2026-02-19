package com.metafit.controller;

import com.metafit.dto.response.config.TenantConfigResponse;
import com.metafit.service.TenantConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for tenant configuration
 */
@Slf4j
@RestController
@RequestMapping("/api/config")
@RequiredArgsConstructor
public class TenantConfigController {

    private final TenantConfigService tenantConfigService;

    /**
     * Get current tenant configuration
     * GET /api/config
     */
    @GetMapping
    public ResponseEntity<TenantConfigResponse> getTenantConfig() {
        log.info("GET /api/config - Fetching tenant configuration");

        TenantConfigResponse config = tenantConfigService.getCurrentTenantConfig();

        return ResponseEntity.ok(config);
    }

    /**
     * Update tenant configuration (Owner only)
     * PUT /api/config
     */
    @PutMapping
    public ResponseEntity<TenantConfigResponse> updateTenantConfig(
            @RequestBody TenantConfigResponse configRequest) {
        log.info("PUT /api/config - Updating tenant configuration");

        TenantConfigResponse updated = tenantConfigService.updateTenantConfig(configRequest);

        return ResponseEntity.ok(updated);
    }
}
