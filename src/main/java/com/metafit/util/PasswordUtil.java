package com.metafit.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.regex.Pattern;

/**
 * Password Utility Class
 * Provides password hashing, validation, and generation utilities
 */
@Component
public class PasswordUtil {

    private final PasswordEncoder passwordEncoder;
    private static final SecureRandom random = new SecureRandom();

    // Password validation patterns
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile(".*[A-Z].*");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile(".*[a-z].*");
    private static final Pattern DIGIT_PATTERN = Pattern.compile(".*\\d.*");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");

    // Character sets for password generation
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL_CHARS = "!@#$%^&*()_+-=[]{}";
    private static final String ALL_CHARS = UPPERCASE + LOWERCASE + DIGITS + SPECIAL_CHARS;

    public PasswordUtil() {
        this.passwordEncoder = new BCryptPasswordEncoder(10);
    }

    /**
     * Hash a plain text password using BCrypt
     * @param plainPassword Plain text password
     * @return Hashed password
     */
    public String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        return passwordEncoder.encode(plainPassword);
    }

    /**
     * Verify if plain password matches hashed password
     * @param plainPassword Plain text password
     * @param hashedPassword Hashed password from database
     * @return true if passwords match, false otherwise
     */
    public boolean matches(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        return passwordEncoder.matches(plainPassword, hashedPassword);
    }

    /**
     * Generate a random secure password
     * @param length Length of password (minimum 8)
     * @return Generated password
     */
    public String generateRandomPassword(int length) {
        if (length < 8) {
            throw new IllegalArgumentException("Password length must be at least 8 characters");
        }

        StringBuilder password = new StringBuilder(length);

        // Ensure at least one character from each category
        password.append(UPPERCASE.charAt(random.nextInt(UPPERCASE.length())));
        password.append(LOWERCASE.charAt(random.nextInt(LOWERCASE.length())));
        password.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        password.append(SPECIAL_CHARS.charAt(random.nextInt(SPECIAL_CHARS.length())));

        // Fill the rest randomly
        for (int i = 4; i < length; i++) {
            password.append(ALL_CHARS.charAt(random.nextInt(ALL_CHARS.length())));
        }

        // Shuffle the password
        return shuffleString(password.toString());
    }

    /**
     * Generate default password for new users (simple format)
     * Format: username + "123"
     */
    public String generateDefaultPassword(String username) {
        return username + "123";
    }

    /**
     * Validate password strength
     * @param password Password to validate
     * @return true if password meets strength requirements
     */
    public boolean isStrongPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        return UPPERCASE_PATTERN.matcher(password).matches() &&
                LOWERCASE_PATTERN.matcher(password).matches() &&
                DIGIT_PATTERN.matcher(password).matches();
    }

    /**
     * Validate password with strict requirements
     * Requires: uppercase, lowercase, digit, special character, min 8 chars
     */
    public boolean isVeryStrongPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        return UPPERCASE_PATTERN.matcher(password).matches() &&
                LOWERCASE_PATTERN.matcher(password).matches() &&
                DIGIT_PATTERN.matcher(password).matches() &&
                SPECIAL_CHAR_PATTERN.matcher(password).matches();
    }

    /**
     * Get password strength message
     * @param password Password to check
     * @return Descriptive message about password strength
     */
    public String getPasswordStrengthMessage(String password) {
        if (password == null || password.isEmpty()) {
            return "Password cannot be empty";
        }

        if (password.length() < 8) {
            return "Password must be at least 8 characters long";
        }

        int strength = 0;
        StringBuilder message = new StringBuilder("Password is ");

        if (UPPERCASE_PATTERN.matcher(password).matches()) strength++;
        if (LOWERCASE_PATTERN.matcher(password).matches()) strength++;
        if (DIGIT_PATTERN.matcher(password).matches()) strength++;
        if (SPECIAL_CHAR_PATTERN.matcher(password).matches()) strength++;

        switch (strength) {
            case 4:
                message.append("very strong");
                break;
            case 3:
                message.append("strong");
                break;
            case 2:
                message.append("medium");
                break;
            default:
                message.append("weak");
                break;
        }

        if (strength < 3) {
            message.append(". Consider adding ");
            if (!UPPERCASE_PATTERN.matcher(password).matches()) message.append("uppercase letters, ");
            if (!LOWERCASE_PATTERN.matcher(password).matches()) message.append("lowercase letters, ");
            if (!DIGIT_PATTERN.matcher(password).matches()) message.append("digits, ");
            if (!SPECIAL_CHAR_PATTERN.matcher(password).matches()) message.append("special characters, ");

            // Remove trailing comma and space
            message.setLength(message.length() - 2);
        }

        return message.toString();
    }

    /**
     * Shuffle string characters randomly
     */
    private String shuffleString(String input) {
        char[] characters = input.toCharArray();
        for (int i = characters.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = characters[i];
            characters[i] = characters[j];
            characters[j] = temp;
        }
        return new String(characters);
    }

    /**
     * Check if password has been compromised (basic check)
     * In production, integrate with Have I Been Pwned API
     */
    public boolean isCommonPassword(String password) {
        // List of most common passwords to reject
        String[] commonPasswords = {
                "password", "123456", "12345678", "qwerty", "abc123",
                "password123", "admin", "letmein", "welcome", "monkey"
        };

        String lowerPassword = password.toLowerCase();
        for (String common : commonPasswords) {
            if (lowerPassword.contains(common)) {
                return true;
            }
        }

        return false;
    }
}