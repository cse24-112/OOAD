package com.bankingsystem;

import com.bankingsystem.utils.IDGenerator;

public class InvestmentAccount extends Account implements PayInterest, Withdrawable {
    private double interestRate; // monthly rate, e.g. 0.05 for 5%
    private double minOpeningDeposit;

    public InvestmentAccount(Customer owner, String branch, double interestRate, double minOpeningDeposit) {
        super(owner, branch);
        this.interestRate = interestRate;
        this.minOpeningDeposit = minOpeningDeposit;
    }

    @Override
    protected String generateAccountNumber() {
        return IDGenerator.generateAccountNumber("INV");
    }

    @Override
    public double applyMonthlyInterest() {
        // Investment accounts pay 5% monthly interest
        double interest = calculateInterest();
        if (interest > 0) {
            recordInterest(interest);
        }
        return interest;
    }

    public static boolean meetsOpeningDeposit(double amount, double required) {
        return amount >= required;
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
        return balance * interestRate;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public double getMinOpeningDeposit() {
        return minOpeningDeposit;
    }
}
