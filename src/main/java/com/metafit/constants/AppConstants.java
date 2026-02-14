package com.metafit.constants;

/**
 * Application-wide constants
 */
public final class AppConstants {

    private AppConstants() {
        throw new UnsupportedOperationException("Cannot instantiate constants class");
    }

    // API Version
    public static final String API_VERSION = "v1";
    public static final String API_BASE_PATH = "/api";

    // Pagination
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;
    public static final String DEFAULT_SORT_FIELD = "createdAt";
    public static final String DEFAULT_SORT_DIRECTION = "DESC";

    // Date/Time Formats
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String TIME_FORMAT = "HH:mm:ss";
    public static final String TIMEZONE = "Asia/Kolkata";

    // JWT
    public static final String JWT_SECRET_KEY = "metafit_secret_key_2024_should_be_changed_in_production";
    public static final long JWT_EXPIRATION_MS = 24 * 60 * 60 * 1000; // 24 hours
    public static final long JWT_REFRESH_EXPIRATION_MS = 7 * 24 * 60 * 60 * 1000; // 7 days
    public static final String JWT_HEADER = "Authorization";
    public static final String JWT_PREFIX = "Bearer ";

    // Tenant
    public static final String TENANT_HEADER = "X-Tenant-ID";
    public static final String TENANT_SUBDOMAIN_HEADER = "X-Tenant-Subdomain";
    public static final int MAX_TENANT_NAME_LENGTH = 100;

    // Member
    public static final int MEMBERSHIP_EXPIRY_WARNING_DAYS = 7;
    public static final int MIN_MEMBER_AGE = 10;
    public static final int MAX_MEMBER_AGE = 100;
    public static final int PHONE_LENGTH = 10;

    // Trainer
    public static final int DEFAULT_TRAINER_CAPACITY = 20;
    public static final int MAX_TRAINER_CAPACITY = 50;

    // Device
    public static final int DEVICE_HEARTBEAT_TIMEOUT_MINUTES = 10;
    public static final int MAX_DEVICE_NAME_LENGTH = 100;
    public static final int DEVICE_EVENT_RETENTION_DAYS = 90;

    // Payment
    public static final String RECEIPT_NUMBER_PREFIX = "RCP";
    public static final int RECEIPT_NUMBER_LENGTH = 10;

    // File Upload
    public static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    public static final String[] ALLOWED_IMAGE_TYPES = {"image/jpeg", "image/png", "image/jpg"};
    public static final String UPLOAD_DIRECTORY = "/var/uploads/metafit";

    // Validation Messages
    public static final String VALIDATION_REQUIRED = "This field is required";
    public static final String VALIDATION_INVALID_EMAIL = "Invalid email format";
    public static final String VALIDATION_INVALID_PHONE = "Invalid phone number (must be 10 digits)";
    public static final String VALIDATION_FUTURE_DATE = "Date cannot be in the future";
    public static final String VALIDATION_PAST_DATE = "Date cannot be in the past";

    // Error Messages
    public static final String ERROR_MEMBER_NOT_FOUND = "Member not found";
    public static final String ERROR_TRAINER_NOT_FOUND = "Trainer not found";
    public static final String ERROR_DEVICE_NOT_FOUND = "Device not found";
    public static final String ERROR_PAYMENT_NOT_FOUND = "Payment not found";
    public static final String ERROR_USER_NOT_FOUND = "User not found";
    public static final String ERROR_TENANT_NOT_FOUND = "Tenant not found";
    public static final String ERROR_UNAUTHORIZED = "Unauthorized access";
    public static final String ERROR_FORBIDDEN = "Access forbidden";
    public static final String ERROR_DUPLICATE_RESOURCE = "Resource already exists";
    public static final String ERROR_INVALID_CREDENTIALS = "Invalid username or password";
    public static final String ERROR_SUBSCRIPTION_EXPIRED = "Subscription has expired";

    // Success Messages
    public static final String SUCCESS_CREATED = "Created successfully";
    public static final String SUCCESS_UPDATED = "Updated successfully";
    public static final String SUCCESS_DELETED = "Deleted successfully";
    public static final String SUCCESS_CHECK_IN = "Check-in successful";
    public static final String SUCCESS_CHECK_OUT = "Check-out successful";
    public static final String SUCCESS_PAYMENT_RECORDED = "Payment recorded successfully";
}
