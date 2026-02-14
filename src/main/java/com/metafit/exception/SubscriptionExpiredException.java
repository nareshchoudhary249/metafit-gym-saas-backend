package com.metafit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDate;

/**
 * Exception thrown when tenant subscription has expired
 */
@ResponseStatus(HttpStatus.PAYMENT_REQUIRED)
public class SubscriptionExpiredException extends RuntimeException {

    private final Long tenantId;
    private final LocalDate expiryDate;

    public SubscriptionExpiredException(Long tenantId, LocalDate expiryDate) {
        super(String.format("Subscription expired on %s. Please renew to continue using the service.", expiryDate));
        this.tenantId = tenantId;
        this.expiryDate = expiryDate;
    }

    public SubscriptionExpiredException(String message) {
        super(message);
        this.tenantId = null;
        this.expiryDate = null;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }
}