package com.metafit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when business logic validation fails
 * Examples: Trainer capacity exceeded, membership expired, duplicate check-in
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BusinessValidationException extends RuntimeException {

    private final String field;
    private final String errorCode;

    public BusinessValidationException(String message) {
        super(message);
        this.field = null;
        this.errorCode = null;
    }

    public BusinessValidationException(String message, String field) {
        super(message);
        this.field = field;
        this.errorCode = null;
    }

    public BusinessValidationException(String message, String field, String errorCode) {
        super(message);
        this.field = field;
        this.errorCode = errorCode;
    }

    public String getField() {
        return field;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
