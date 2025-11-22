package com.bankingsystem;

public interface Withdrawable {
    /**
     * Attempt to withdraw an amount from the account.
     * @param amount amount to withdraw
     * @return true if withdrawal succeeded, false otherwise
     */
    boolean withdraw(double amount);
}
