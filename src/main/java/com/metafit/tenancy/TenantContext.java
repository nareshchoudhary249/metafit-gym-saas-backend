package com.metafit.tenancy;

import lombok.extern.slf4j.Slf4j;

/**
 * Thread-safe context holder for current tenant information
 */
@Slf4j
public class TenantContext {

    private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();

    public static void setTenantId(String tenantId) {
        log.debug("Setting tenant context to: {}", tenantId);
        CURRENT_TENANT.set(tenantId);
    }

    public static String getTenantId() {
        String tenantId = CURRENT_TENANT.get();
        log.debug("Retrieved tenant context: {}", tenantId);
        return tenantId;
    }

    public static void clear() {
        log.debug("Clearing tenant context");
        CURRENT_TENANT.remove();
    }
}