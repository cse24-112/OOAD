package com.bankingsystem.utils;

import java.util.regex.Pattern;

/**
 * Utility class for input validation.
 * Provides static methods for validating various types of input data.
 */
public class ValidationHelper {
    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    // Phone number pattern (allows various formats)
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^[+]?[(]?[0-9]{1,4}[)]?[-\\s.]?[(]?[0-9]{1,4}[)]?[-\\s.]?[0-9]{1,9}$"
    );

    /**
     * Validate that a string is not null or blank.
     * @param value The string to validate
     * @param fieldName The name of the field (for error messages)
     * @return Error message if invalid, null if valid
     */
    public static String validateRequired(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            return fieldName + " is required";
        }
        return null;
    }

    /**
     * Validate email format.
     * @param email The email to validate
     * @return Error message if invalid, null if valid
     */
    public static String validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return "Email is required";
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return "Invalid email format";
        }
        return null;
    }

    /**
     * Validate phone number format.
     * @param phone The phone number to validate
     * @return Error message if invalid, null if valid
     */
    public static String validatePhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return "Phone number is required";
        }
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            return "Invalid phone number format";
        }
        return null;
    }

    /**
     * Validate that a number is positive.
     * @param amount The amount to validate
     * @param fieldName The name of the field
     * @return Error message if invalid, null if valid
     */
    public static String validatePositiveAmount(double amount, String fieldName) {
        if (amount <= 0) {
            return fieldName + " must be greater than zero";
        }
        return null;
    }

    /**
     * Validate that a number is not negative.
     * @param amount The amount to validate
     * @param fieldName The name of the field
     * @return Error message if invalid, null if valid
     */
    public static String validateNonNegative(double amount, String fieldName) {
        if (amount < 0) {
            return fieldName + " cannot be negative";
        }
        return null;
    }

    /**
     * Validate that a number meets a minimum requirement.
     * @param amount The amount to validate
     * @param minimum The minimum required value
     * @param fieldName The name of the field
     * @return Error message if invalid, null if valid
     */
    public static String validateMinimum(double amount, double minimum, String fieldName) {
        if (amount < minimum) {
            return fieldName + " must be at least " + minimum;
        }
        return null;
    }

    /**
     * Validate national ID format (Botswana format: typically 9 digits).
     * @param nationalID The national ID to validate
     * @return Error message if invalid, null if valid
     */
    public static String validateNationalID(String nationalID) {
        if (nationalID == null || nationalID.trim().isEmpty()) {
            return "National ID is required";
        }
        // Botswana national ID is typically 9 digits
        if (!nationalID.matches("^[0-9]{9}$")) {
            return "National ID must be 9 digits";
        }
        return null;
    }

    /**
     * Validate username format (alphanumeric, 3-20 characters).
     * @param username The username to validate
     * @return Error message if invalid, null if valid
     */
    public static String validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return "Username is required";
        }
        if (!username.matches("^[a-zA-Z0-9_]{3,20}$")) {
            return "Username must be 3-20 characters (letters, numbers, underscore only)";
        }
        return null;
    }

    /**
     * Validate password strength (minimum 6 characters).
     * @param password The password to validate
     * @return Error message if invalid, null if valid
     */
    public static String validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            return "Password is required";
        }
        if (password.length() < 6) {
            return "Password must be at least 6 characters";
        }
        return null;
    }

    /**
     * Validate account number format.
     * @param accountNumber The account number to validate
     * @return Error message if invalid, null if valid
     */
    public static String validateAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            return "Account number is required";
        }
        // Account numbers should be non-empty strings
        return null;
    }
}

