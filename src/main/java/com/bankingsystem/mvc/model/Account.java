package com.bankingsystem.mvc.model;

import java.time.Instant;
import java.util.UUID;

public abstract class Account {
    protected String accountId;
    protected String accountNumber;
    protected String customerId;
    protected String accountType;
    protected double balance;
    protected String branch;
    protected String employerName;
    protected String employerAddress;

    public String getEmployerName() { return employerName; }
    public String getEmployerAddress() { return employerAddress; }

    public void setEmployerName(String employerName) { this.employerName = employerName; }
    public void setEmployerAddress(String employerAddress) { this.employerAddress = employerAddress; }

    public void setAccountId(String accountId) { this.accountId = accountId; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public void setBalance(double balance) { this.balance = balance; }

    public Account() {}

    public Account(String customerId, String accountType, double balance, String branch) {
        this.accountId = UUID.randomUUID().toString();
        this.accountNumber = "ACC" + Instant.now().toEpochMilli();
        this.customerId = customerId;
        this.accountType = accountType;
        this.balance = balance;
        this.branch = branch;
    }

    public String getAccountId() { return accountId; }
    public String getAccountNumber() { return accountNumber; }
    public String getCustomerId() { return customerId; }
    public String getAccountType() { return accountType; }
    public double getBalance() { return balance; }
    public String getBranch() { return branch; }

    public void deposit(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");
        this.balance += amount;
    }

    public boolean withdraw(double amount) {
        if (amount <= 0) return false;
        if (amount > balance) return false;
        this.balance -= amount;
        return true;
    }

    public abstract double calculateInterest();
}
