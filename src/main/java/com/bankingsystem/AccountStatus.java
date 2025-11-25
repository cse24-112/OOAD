package com.bankingsystem;

/**
 * Represents the approval status of a bank account.
 * Accounts start as PENDING and must be approved by staff
 * before becoming active for transactions.
 */
public enum AccountStatus {
    PENDING("Pending Approval"),
    APPROVED("Approved"),
    REJECTED("Rejected");

    private final String displayName;

    AccountStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
