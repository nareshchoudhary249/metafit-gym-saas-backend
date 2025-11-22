package com.metafit.tenancy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Resolves tenant identifier from HTTP request header
 * Header: X-Tenant-ID
 */
@Slf4j
@Component
public class TenantIdentifierResolver {

    private static final String TENANT_HEADER = "X-Tenant-ID";
    private static final String DEFAULT_TENANT = "default";

    public String resolveTenantIdentifier() {
        // First check ThreadLocal context
        String tenantId = TenantContext.getTenantId();

        if (tenantId != null && !tenantId.isEmpty()) {
            log.debug("Tenant resolved from context: {}", tenantId);
            return tenantId;
        }

        // Try to get from request header
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            tenantId = request.getHeader(TENANT_HEADER);

            if (tenantId != null && !tenantId.isEmpty()) {
                log.debug("Tenant resolved from header: {}", tenantId);
                TenantContext.setTenantId(tenantId);
                return tenantId;
            }
        }

        log.warn("No tenant identifier found, using default");
        return DEFAULT_TENANT;
    }

    public void validateTenantIdentifier(String tenantId) {
        if (tenantId == null || tenantId.trim().isEmpty()) {
            throw new IllegalArgumentException("Tenant identifier cannot be null or empty");
        }
    }
}