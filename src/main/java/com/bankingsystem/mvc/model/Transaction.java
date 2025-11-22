package com.bankingsystem.mvc.model;

import java.time.Instant;

public class Transaction {
    private String transactionId;
    private String accountId;
    private String type; // DEPOSIT, WITHDRAW
    private double amount;
    private Instant timestamp;

    public Transaction() {}

    public Transaction(String accountId, String type, double amount) {
        this.transactionId = "TX" + Instant.now().toEpochMilli();
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
        this.timestamp = Instant.now();
    }

    public String getTransactionId() { return transactionId; }
    public String getAccountId() { return accountId; }
    public String getType() { return type; }
    public double getAmount() { return amount; }
    public Instant getTimestamp() { return timestamp; }
}
