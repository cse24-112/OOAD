package com.bankingsystem;

import com.bankingsystem.utils.IDGenerator;
import java.time.LocalDate;
import java.util.Objects;

public abstract class Account {
    protected String accountNumber;
    protected String branch;
    protected double balance;
    protected LocalDate dateOpened;
    protected Customer owner;
    protected java.util.List<Transaction> transactions = new java.util.ArrayList<>();
    protected AccountStatus status = AccountStatus.PENDING;
    protected java.time.LocalDateTime approvalDateTime;
    protected String approvalStaffUsername;
    protected boolean showApprovalNotification = false;

    public Account(Customer owner, String branch) {
        this.accountNumber = null; // Will be assigned upon approval
        this.branch = branch;
        this.balance = 0.0;
        this.dateOpened = LocalDate.now();
        this.owner = owner;
        this.status = AccountStatus.PENDING;
    }

    /**
     * Generate account number using IDGenerator utility.
     * Subclasses can override to specify account type.
     * This is called during account approval, not during creation.
     */
    protected String generateAccountNumber() {
        // Default: use generic account type
        // Subclasses should override to specify their type
        return IDGenerator.generateAccountNumber("ACC");
    }

    /**
     * Approve this account and assign a unique account number.
     * Should only be called once, by a staff member during approval.
     * 
     * @param staffUsername The username of the staff member approving
     */
    public void approve(String staffUsername) {
        if (this.status != AccountStatus.PENDING) {
            throw new IllegalStateException("Cannot approve non-pending account");
        }
        this.status = AccountStatus.APPROVED;
        this.accountNumber = generateAccountNumber();
        this.approvalDateTime = java.time.LocalDateTime.now();
        this.approvalStaffUsername = staffUsername;
        this.showApprovalNotification = true;
    }

    /**
     * Reject this account.
     * 
     * @param staffUsername The username of the staff member rejecting
     */
    public void reject(String staffUsername) {
        if (this.status != AccountStatus.PENDING) {
            throw new IllegalStateException("Cannot reject non-pending account");
        }
        this.status = AccountStatus.REJECTED;
        this.approvalStaffUsername = staffUsername;
        this.approvalDateTime = java.time.LocalDateTime.now();
    }

    public AccountStatus getStatus() {
        return status;
    }

    public java.time.LocalDateTime getApprovalDateTime() {
        return approvalDateTime;
    }

    public String getApprovalStaffUsername() {
        return approvalStaffUsername;
    }

    public boolean isShowApprovalNotification() {
        return showApprovalNotification;
    }

    public void clearApprovalNotification() {
        this.showApprovalNotification = false;
    }


    public String getAccountNumber() {
        return accountNumber;
    }

    public String getBranch() {
        return branch;
    }

    public double getBalance() {
        return balance;
    }

    public LocalDate getDateOpened() {
        return dateOpened;
    }

    public Customer getOwner() {
        return owner;
    }

    public void deposit(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Deposit must be positive");
        balance += amount;
        transactions.add(new Transaction(Transaction.Type.DEPOSIT, amount, balance, null));
    }

    /**
     * Default withdrawal: not allowed unless subclass overrides.
     */
    public boolean withdraw(double amount) {
        return false;
    }

    public boolean transferTo(Account to, double amount) {
        Objects.requireNonNull(to);
        if (this.withdraw(amount)) {
            // Deposit to destination account (this will create a DEPOSIT transaction)
            to.deposit(amount);
            
            // Replace the DEPOSIT transaction with a TRANSFER transaction on destination
            // Remove the last transaction (the deposit) and add transfer instead
            if (!to.transactions.isEmpty()) {
                to.transactions.remove(to.transactions.size() - 1);
            }
            to.transactions.add(new Transaction(Transaction.Type.TRANSFER, amount, to.balance, 
                "Transfer from " + this.accountNumber));
            
            // Record transfer transaction on source account
            String description = "Transfer to " + to.getAccountNumber();
            transactions.add(new Transaction(Transaction.Type.TRANSFER, amount, balance, description));
            
            return true;
        }
        return false;
    }

    /**
     * Calculate and return interest amount (not applied). Subclasses that pay interest implement PayInterest.
     */
    public abstract double calculateInterest();

    /**
     * Apply monthly interest to this account.
     * This method calculates interest and adds it to the balance.
     * Subclasses that pay interest should override this method.
     * @return The amount of interest applied, or 0 if no interest was applied
     */
    public double applyMonthlyInterest() {
        double interest = calculateInterest();
        if (interest > 0) {
            recordInterest(interest);
        }
        return interest;
    }

    public java.util.List<Transaction> getTransactions() {
        return java.util.Collections.unmodifiableList(transactions);
    }

    protected void recordInterest(double amount) {
        if (amount <= 0) return;
        balance += amount;
        transactions.add(new Transaction(Transaction.Type.INTEREST, amount, balance, "monthly interest"));
    }
}
