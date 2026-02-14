package com.metafit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when tenant is not found or invalid
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class TenantNotFoundException extends RuntimeException {

    private final String tenantIdentifier;

    public TenantNotFoundException(String tenantIdentifier) {
        super(String.format("Tenant not found: %s", tenantIdentifier));
        this.tenantIdentifier = tenantIdentifier;
    }

    public TenantNotFoundException(String tenantIdentifier, String message) {
        super(message);
        this.tenantIdentifier = tenantIdentifier;
    }

    public String getTenantIdentifier() {
        return tenantIdentifier;
    }
}
