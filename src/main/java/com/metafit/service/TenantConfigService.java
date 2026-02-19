package com.metafit.service;

import com.metafit.dto.response.config.TenantConfigResponse;

/**
 * Tenant Configuration Service Interface
 */
public interface TenantConfigService {

    TenantConfigResponse getCurrentTenantConfig();

    TenantConfigResponse updateTenantConfig(TenantConfigResponse configRequest);
}
