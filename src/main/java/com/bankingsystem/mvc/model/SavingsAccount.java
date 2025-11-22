package com.bankingsystem.mvc.model;

public class SavingsAccount extends Account {
    private static final double MONTHLY_RATE = 0.0005; // 0.05%

    public SavingsAccount(String customerId, double balance, String branch) {
        super(customerId, "SAVINGS", balance, branch);
    }

    @Override
    public double calculateInterest() {
        return balance * MONTHLY_RATE;
    }

    @Override
    public boolean withdraw(double amount) {
        // Savings withdrawal not allowed
        throw new UnsupportedOperationException("Withdrawals from Savings accounts are not permitted");
    }
}
