package com.bankingsystem;

import com.bankingsystem.utils.IDGenerator;

public class SavingsAccount extends Account implements PayInterest {
    private double interestRate; // monthly rate for 4% annual = 0.003333 (0.3333% per month)
    private double minBalance;
    private static final double ACCOUNT_OPENING_FEE = 50.0; // Pula

    public SavingsAccount(Customer owner, String branch, double interestRate, double minBalance) {
        super(owner, branch);
        this.interestRate = interestRate;
        this.minBalance = minBalance;
        // Immediately charge the account opening fee
        this.balance = -ACCOUNT_OPENING_FEE;
    }

    @Override
    protected String generateAccountNumber() {
        return IDGenerator.generateAccountNumber("SAV");
    }

    @Override
    public boolean withdraw(double amount) {
        // Savings account does not allow withdrawals per requirements
        return false;
    }

    @Override
    public double calculateInterest() {
        // Only pay interest if account is approved and balance is positive
        if (this.status == AccountStatus.APPROVED && balance > 0) {
            return balance * interestRate;
        }
        return 0.0;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public double getMinBalance() {
        return minBalance;
    }

    public static double getAccountOpeningFee() {
        return ACCOUNT_OPENING_FEE;
    }
}

