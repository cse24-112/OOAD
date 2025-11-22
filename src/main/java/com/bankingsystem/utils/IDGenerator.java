package com.bankingsystem.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Utility class for generating unique IDs and account numbers.
 * Provides methods for generating customer IDs, account numbers, and transaction IDs.
 */
public class IDGenerator {
    // Counter for generating sequential numbers
    private static final AtomicLong customerCounter = new AtomicLong(1000);
    private static final AtomicLong accountCounter = new AtomicLong(10000);
    private static final AtomicLong transactionCounter = new AtomicLong(1);
    
    // Branch code prefix (can be configured)
    private static final String BRANCH_CODE = "001";

    /**
     * Generate a unique customer ID.
     * Format: CUST + sequential number (e.g., CUST1001)
     * 
     * @return A unique customer ID
     */
    public static String generateCustomerID() {
        long next = customerCounter.getAndIncrement();
        return "CUST" + next;
    }

    /**
     * Generate a unique account number.
     * Format: Branch code + Account type prefix + sequential number
     * Example: 001-SAV-10001, 001-INV-10002, 001-CHQ-10003
     * 
     * @param accountType The type of account (SAV, INV, CHQ)
     * @return A unique account number
     */
    public static String generateAccountNumber(String accountType) {
        if (accountType == null) accountType = "ACC";
        
        // Normalize account type to 3-letter prefix
        String prefix;
        switch (accountType.toUpperCase()) {
            case "SAVINGS":
            case "SAV":
                prefix = "SAV";
                break;
            case "INVESTMENT":
            case "INV":
                prefix = "INV";
                break;
            case "CHEQUE":
            case "CHQ":
                prefix = "CHQ";
                break;
            default:
                prefix = "ACC";
        }
        
        long next = accountCounter.getAndIncrement();
        return BRANCH_CODE + "-" + prefix + "-" + String.format("%05d", next);
    }

    /**
     * Generate a unique transaction ID.
     * Format: TXN + timestamp + sequential number
     * Example: TXN20240101123456-0001
     * 
     * @return A unique transaction ID
     */
    public static String generateTransactionID() {
        String timestamp = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long next = transactionCounter.getAndIncrement();
        return "TXN" + timestamp + "-" + String.format("%04d", next);
    }

    /**
     * Generate a unique request ID for account opening requests.
     * Format: REQ + sequential number
     * 
     * @return A unique request ID
     */
    public static String generateRequestID() {
        return "REQ" + (1000 + accountCounter.get());
    }

    /**
     * Reset counters (useful for testing).
     * WARNING: Only use in test environments!
     */
    public static void resetCounters() {
        customerCounter.set(1000);
        accountCounter.set(10000);
        transactionCounter.set(1);
    }

    /**
     * Set the branch code for account number generation.
     * @param branchCode The branch code to use
     */
    public static void setBranchCode(String branchCode) {
        // Note: This is a simplified approach. In production, branch code
        // should be passed as a parameter to generateAccountNumber()
    }
}

