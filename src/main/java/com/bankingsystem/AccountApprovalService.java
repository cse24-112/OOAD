package com.bankingsystem;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing account approval workflow.
 * Handles approval/rejection of pending accounts by staff members.
 */
public class AccountApprovalService {

    /**
     * Get all pending accounts across the bank.
     */
    public static List<Account> getPendingAccounts(Bank bank) {
        return bank.getAccounts().stream()
            .filter(acc -> acc.getStatus() == AccountStatus.PENDING)
            .collect(Collectors.toList());
    }

    /**
     * Get pending accounts for a specific customer.
     */
    public static List<Account> getPendingAccountsByCustomer(Customer customer) {
        return customer.getAccounts().stream()
            .filter(acc -> acc.getStatus() == AccountStatus.PENDING)
            .collect(Collectors.toList());
    }

    /**
     * Approve a pending account. Validates eligibility and assigns account number.
     * 
     * @param account The account to approve
     * @param staffUsername The username of the staff member approving
     * @return true if approval succeeded, false otherwise
     */
    public static boolean approveAccount(Account account, String staffUsername) {
        if (account == null || account.getStatus() != AccountStatus.PENDING) {
            return false;
        }

        // Validate account eligibility based on type
        if (!isAccountEligibleForApproval(account)) {
            return false;
        }

        try {
            account.approve(staffUsername);
            return true;
        } catch (IllegalStateException e) {
            return false;
        }
    }

    /**
     * Reject a pending account.
     * 
     * @param account The account to reject
     * @param staffUsername The username of the staff member rejecting
     * @return true if rejection succeeded, false otherwise
     */
    public static boolean rejectAccount(Account account, String staffUsername) {
        if (account == null || account.getStatus() != AccountStatus.PENDING) {
            return false;
        }

        try {
            account.reject(staffUsername);
            return true;
        } catch (IllegalStateException e) {
            return false;
        }
    }

    /**
     * Check if an account is eligible for approval based on its type and balance.
     * Investment accounts must have minimum P1000 balance.
     * Savings and Cheque accounts are always eligible.
     */
    public static boolean isAccountEligibleForApproval(Account account) {
        if (account instanceof InvestmentAccount) {
            InvestmentAccount investmentAccount = (InvestmentAccount) account;
            return investmentAccount.isEligibleForApproval();
        }
        // Savings and Cheque accounts are always eligible
        return true;
    }

    /**
     * Get the account number for a given account (only available after approval).
     */
    public static String getAccountNumber(Account account) {
        if (account.getStatus() == AccountStatus.APPROVED) {
            return account.getAccountNumber();
        }
        return null;
    }

    /**
     * Check if an account has a pending approval notification for the customer.
     */
    public static boolean hasPendingApprovalNotification(Account account) {
        return account.isShowApprovalNotification();
    }

    /**
     * Clear the approval notification after displaying to customer.
     */
    public static void clearApprovalNotification(Account account) {
        account.clearApprovalNotification();
    }
}
