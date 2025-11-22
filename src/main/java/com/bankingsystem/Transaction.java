package com.bankingsystem;

import java.time.LocalDateTime;

public class Transaction {
    public enum Type { DEPOSIT, WITHDRAW, INTEREST, TRANSFER }

    private final LocalDateTime timestamp;
    private final Type type;
    private final double amount;
    private final double balanceAfter;
    private final String description;

    public Transaction(Type type, double amount, double balanceAfter, String description) {
        this.timestamp = LocalDateTime.now();
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.description = description;
    }

    public LocalDateTime getTimestamp() { return timestamp; }
    public Type getType() { return type; }
    public double getAmount() { return amount; }
    public double getBalanceAfter() { return balanceAfter; }
    public String getDescription() { return description; }

    @Override
    public String toString() {
        return String.format("%s %s %.2f -> bal: %.2f %s", timestamp.toLocalDate(), type, amount, balanceAfter, description == null ? "" : ("(" + description + ")"));
    }
}
