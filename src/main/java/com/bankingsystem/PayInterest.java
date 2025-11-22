package com.bankingsystem;

public interface PayInterest {
    /**
     * Calculate monthly interest for the account (amount, not rate).
     * @return interest amount to be applied to the account balance
     */
    double calculateInterest();
}
