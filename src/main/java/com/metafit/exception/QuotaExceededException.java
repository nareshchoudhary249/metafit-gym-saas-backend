package com.metafit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when tenant exceeds subscription quota limits
 * Examples: Max members reached, max staff reached, max devices reached
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class QuotaExceededException extends RuntimeException {

    private final String quotaType;
    private final int currentCount;
    private final int maxAllowed;

    public QuotaExceededException(String quotaType, int currentCount, int maxAllowed) {
        super(String.format("%s quota exceeded. Current: %d, Maximum allowed: %d. Please upgrade your plan.",
                quotaType, currentCount, maxAllowed));
        this.quotaType = quotaType;
        this.currentCount = currentCount;
        this.maxAllowed = maxAllowed;
    }

    public QuotaExceededException(String message) {
        super(message);
        this.quotaType = null;
        this.currentCount = 0;
        this.maxAllowed = 0;
    }

    public String getQuotaType() {
        return quotaType;
    }

    public int getCurrentCount() {
        return currentCount;
    }

    public int getMaxAllowed() {
        return maxAllowed;
    }
}
