package com.bankingsystem.utils;

import com.bankingsystem.Customer;
import com.bankingsystem.IndividualCustomer;
import com.bankingsystem.CompanyCustomer;
import com.bankingsystem.Staff;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class for authentication operations.
 * Provides methods for password hashing and authentication verification.
 * 
 * Note: In production, use proper password hashing (bcrypt, Argon2, etc.)
 * and store hashed passwords in the database.
 */
public class AuthenticationHelper {
    // Simple hash algorithm (for demo purposes)
    // TODO: Replace with bcrypt or Argon2 for production use
    
    /**
     * Hash a password using SHA-256 (for demo purposes).
     * In production, use bcrypt or Argon2.
     * @param password The plain text password
     * @return The hashed password, or null if hashing fails
     */
    public static String hashPassword(String password) {
        if (password == null) return null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // Fallback: return password as-is (NOT SECURE - for demo only)
            return password;
        }
    }

    /**
     * Verify a password against a hash.
     * @param password The plain text password to verify
     * @param hash The stored hash
     * @return true if password matches hash, false otherwise
     */
    public static boolean verifyPassword(String password, String hash) {
        if (password == null || hash == null) return false;
        String passwordHash = hashPassword(password);
        return passwordHash != null && passwordHash.equals(hash);
    }

    /**
     * Authenticate a customer with username and password.
     * @param customer The customer to authenticate
     * @param username The username
     * @param password The password
     * @return true if authentication succeeds, false otherwise
     */
    public static boolean authenticateCustomer(Customer customer, String username, String password) {
        if (customer == null || username == null || password == null) {
            return false;
        }
        
        // Individual customers have username/password
        if (customer instanceof IndividualCustomer) {
            IndividualCustomer ic = (IndividualCustomer) customer;
            return ic.authenticate(username, password);
        }
        
        // Company customers might have different authentication
        // For now, return false (can be extended later)
        return false;
    }

    /**
     * Authenticate staff with username and password.
     * @param staff The staff member to authenticate
     * @param username The username
     * @param password The password
     * @return true if authentication succeeds, false otherwise
     */
    public static boolean authenticateStaff(Staff staff, String username, String password) {
        if (staff == null || username == null || password == null) {
            return false;
        }
        return staff.authenticate(username, password);
    }

    /**
     * Check if a customer has the required role/permission.
     * @param customer The customer to check
     * @param role The required role (e.g., "INDIVIDUAL", "COMPANY")
     * @return true if customer has the role, false otherwise
     */
    public static boolean hasRole(Customer customer, String role) {
        if (customer == null || role == null) return false;
        
        if ("INDIVIDUAL".equalsIgnoreCase(role)) {
            return customer instanceof IndividualCustomer;
        }
        if ("COMPANY".equalsIgnoreCase(role)) {
            return customer instanceof CompanyCustomer;
        }
        
        return false;
    }
}

