package com.bankingsystem;

import com.bankingsystem.utils.IDGenerator;

public class SavingsAccount extends Account implements PayInterest {
    private double interestRate; // monthly rate, e.g. 0.0005 for 0.05%
    private double minBalance;

    public SavingsAccount(Customer owner, String branch, double interestRate, double minBalance) {
        super(owner, branch);
        this.interestRate = interestRate;
        this.minBalance = minBalance;
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
        return balance * interestRate;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public double getMinBalance() {
        return minBalance;
    }
}
