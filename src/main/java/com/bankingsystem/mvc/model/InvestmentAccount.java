package com.bankingsystem.mvc.model;

public class InvestmentAccount extends Account {
    private static final double MONTHLY_RATE = 0.05; // 5%
    private double minOpeningDeposit = 500.0;

    public InvestmentAccount(String customerId, double balance, String branch) {
        super(customerId, "INVESTMENT", balance, branch);
    }

    public static boolean meetsOpeningDeposit(double amount) {
        return amount >= 500.0;
    }

    @Override
    public double calculateInterest() {
        return balance * MONTHLY_RATE;
    }
}
