package com.bankingsystem;

public class EmploymentVerificationService {
    /**
     * Simple stub verification: verifies if employer name and address are present.
     * In real system this would call external APIs or HR checks.
     */
    public static boolean verify(EmploymentInfo info) {
        if (info == null) return false;
        if (info.getEmployerName() == null || info.getEmployerName().isBlank()) return false;
        if (info.getEmployerAddress() == null || info.getEmployerAddress().isBlank()) return false;
        return true;
    }
}
