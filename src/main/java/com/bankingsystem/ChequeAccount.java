package com.bankingsystem;

import com.bankingsystem.utils.IDGenerator;

public class ChequeAccount extends Account implements Withdrawable {
    private EmploymentInfo employmentInfo;
    private boolean overdraftAllowed;

    public ChequeAccount(Customer owner, String branch, EmploymentInfo employmentInfo, boolean overdraftAllowed) {
        super(owner, branch);
        this.employmentInfo = employmentInfo;
        this.overdraftAllowed = overdraftAllowed;
    }

    @Override
    protected String generateAccountNumber() {
        return IDGenerator.generateAccountNumber("CHQ");
    }

    public EmploymentInfo getEmploymentInfo() {
        return employmentInfo;
    }

    public boolean isOverdraftAllowed() {
        return overdraftAllowed;
    }

    @Override
    public boolean withdraw(double amount) {
        if (amount <= 0) return false;
        if (amount <= balance) {
            balance -= amount;
            transactions.add(new Transaction(Transaction.Type.WITHDRAW, amount, balance, null));
            return true;
        }
        if (overdraftAllowed) {
            balance -= amount; // allow balance to go negative
            transactions.add(new Transaction(Transaction.Type.WITHDRAW, amount, balance, "overdraft"));
            return true;
        }
        return false;
    }

    @Override
    public double calculateInterest() {
        // Cheque accounts don't pay interest in this design
        return 0.0;
    }
}
