package com.metafit.constants;

/**
 * Validation constants and regex patterns
 */
public final class ValidationConstants {

    private ValidationConstants() {
        throw new UnsupportedOperationException("Cannot instantiate constants class");
    }

    // Regex Patterns
    public static final String PHONE_REGEX = "^[6-9]\\d{9}$"; // Indian phone numbers
    public static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    public static final String USERNAME_REGEX = "^[a-zA-Z0-9._-]{3,20}$";
    public static final String SUBDOMAIN_REGEX = "^[a-z0-9]([a-z0-9-]{0,61}[a-z0-9])?$";
    public static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
    public static final String INDIAN_PIN_CODE_REGEX = "^[1-9][0-9]{5}$";
    public static final String PAN_CARD_REGEX = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$";
    public static final String GST_NUMBER_REGEX = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$";
    public static final String IFSC_CODE_REGEX = "^[A-Z]{4}0[A-Z0-9]{6}$";

    // Length Constraints
    public static final int MIN_NAME_LENGTH = 2;
    public static final int MAX_NAME_LENGTH = 100;
    public static final int MIN_USERNAME_LENGTH = 3;
    public static final int MAX_USERNAME_LENGTH = 20;
    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final int MAX_PASSWORD_LENGTH = 50;
    public static final int MAX_EMAIL_LENGTH = 255;
    public static final int MAX_PHONE_LENGTH = 15;
    public static final int MAX_ADDRESS_LENGTH = 500;
    public static final int MAX_DESCRIPTION_LENGTH = 1000;
    public static final int MAX_NOTES_LENGTH = 2000;

    // Numeric Constraints
    public static final double MIN_AMOUNT = 0.01;
    public static final double MAX_AMOUNT = 999999.99;
    public static final int MIN_MEMBERSHIP_MONTHS = 1;
    public static final int MAX_MEMBERSHIP_MONTHS = 24;
    public static final int MIN_TRAINER_EXPERIENCE = 0;
    public static final int MAX_TRAINER_EXPERIENCE = 50;

    // Error Messages
    public static final String MSG_INVALID_PHONE = "Phone number must be a valid 10-digit Indian mobile number";
    public static final String MSG_INVALID_EMAIL = "Email must be valid";
    public static final String MSG_INVALID_USERNAME = "Username must be 3-20 characters with only letters, numbers, dots, underscores, and hyphens";
    public static final String MSG_INVALID_PASSWORD = "Password must be at least 8 characters with uppercase, lowercase, digit, and special character";
    public static final String MSG_INVALID_SUBDOMAIN = "Subdomain must be 1-63 characters with lowercase letters, numbers, and hyphens";
    public static final String MSG_NAME_TOO_SHORT = "Name must be at least 2 characters";
    public static final String MSG_NAME_TOO_LONG = "Name cannot exceed 100 characters";
    public static final String MSG_AMOUNT_INVALID = "Amount must be between ₹0.01 and ₹999,999.99";
    public static final String MSG_REQUIRED = "This field is required";
    public static final String MSG_INVALID_DATE = "Invalid date format";
    public static final String MSG_FUTURE_DATE_NOT_ALLOWED = "Future date not allowed";
    public static final String MSG_PAST_DATE_NOT_ALLOWED = "Past date not allowed";
}