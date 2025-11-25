package com.bankingsystem;

import com.bankingsystem.utils.IDGenerator;

public class InvestmentAccount extends Account implements PayInterest, Withdrawable {
    private double interestRate; // monthly rate for 8% annual = 0.006667 (0.6667% per month)
    private double minOpeningDeposit;
    private static final double MINIMUM_DEPOSIT_REQUIRED = 1000.0; // Pula

    public InvestmentAccount(Customer owner, String branch, double interestRate, double minOpeningDeposit) {
        super(owner, branch);
        this.interestRate = interestRate;
        this.minOpeningDeposit = minOpeningDeposit;
    }

    @Override
    protected String generateAccountNumber() {
        return IDGenerator.generateAccountNumber("INV");
    }

    /**
     * Check if a proposed initial deposit meets the minimum opening deposit requirement.
     * Investment accounts must have at least P1000 to be approved.
     */
    public static boolean meetsOpeningDeposit(double amount, double required) {
        return amount >= required;
    }

    /**
     * Verify if this investment account is eligible for approval based on current balance.
     */
    public boolean isEligibleForApproval() {
        return balance >= MINIMUM_DEPOSIT_REQUIRED;
    }

    @Override
    public double applyMonthlyInterest() {
        // Investment accounts pay 8% annual interest (0.6667% monthly) only if approved
        if (this.status == AccountStatus.APPROVED) {
            double interest = calculateInterest();
            if (interest > 0) {
                recordInterest(interest);
            }
            return interest;
        }
        return 0.0;
    }

    @Override
    public boolean withdraw(double amount) {
        if (amount <= 0) return false;
        if (amount <= balance) {
            balance -= amount;
            transactions.add(new Transaction(Transaction.Type.WITHDRAW, amount, balance, null));
            return true;
        }
        return false;
    }

    @Override
    public double calculateInterest() {
        // Only calculate interest if account is approved
        if (this.status == AccountStatus.APPROVED) {
            return balance * interestRate;
        }
        return 0.0;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public double getMinOpeningDeposit() {
        return minOpeningDeposit;
    }

    public static double getMinimumDepositRequired() {
        return MINIMUM_DEPOSIT_REQUIRED;
    }
}

