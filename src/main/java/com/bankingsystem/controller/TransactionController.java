package com.bankingsystem.controller;

import com.bankingsystem.Account;
import com.bankingsystem.Transaction;
import com.bankingsystem.Bank;
import com.bankingsystem.dao.AccountDAOInterface;
import com.bankingsystem.dao.AccountDAO;
import com.bankingsystem.dao.TransactionDAOInterface;
import com.bankingsystem.dao.TransactionDAO;
import com.bankingsystem.utils.ValidationHelper;
import java.util.List;
import java.util.Optional;

/**
 * Controller for transaction-related operations.
 * Handles deposits, withdrawals, transfers, and transaction history.
 * Follows MVC pattern: View -> Controller -> DAO -> Model
 */
public class TransactionController {
    private final AccountDAOInterface accountDAO;
    private final TransactionDAOInterface transactionDAO;
    private final Bank bank; // For account lookup

    public TransactionController(Bank bank) {
        this.bank = bank;
        this.accountDAO = new AccountDAO();
        this.transactionDAO = new TransactionDAO();
    }

    public TransactionController(Bank bank, AccountDAOInterface accountDAO, TransactionDAOInterface transactionDAO) {
        // Allow dependency injection for testing
        this.bank = bank;
        this.accountDAO = accountDAO;
        this.transactionDAO = transactionDAO;
    }

    /**
     * Deposit money into an account.
     * @param accountNumber The account number
     * @param amount The amount to deposit
     * @return Result message (success or error)
     */
    public String deposit(String accountNumber, double amount) {
        // Validate inputs
        String error = ValidationHelper.validateAccountNumber(accountNumber);
        if (error != null) return error;

        error = ValidationHelper.validatePositiveAmount(amount, "Deposit amount");
        if (error != null) return error;

        // Find account
        Account account = bank.findAccount(accountNumber);
        if (account == null) {
            return "Account not found";
        }

        try {
            // Perform deposit
            account.deposit(amount);
            
            // Update account in database via DAO
            accountDAO.updateAccount(account);
            
            // Record transaction via DAO
            List<Transaction> transactions = account.getTransactions();
            if (!transactions.isEmpty()) {
                Transaction lastTransaction = transactions.get(transactions.size() - 1);
                transactionDAO.recordTransaction(lastTransaction);
            }

            return String.format("Successfully deposited BWP %.2f. New balance: BWP %.2f", 
                               amount, account.getBalance());
        } catch (IllegalArgumentException e) {
            return "Deposit failed: " + e.getMessage();
        }
    }

    /**
     * Withdraw money from an account.
     * @param accountNumber The account number
     * @param amount The amount to withdraw
     * @return Result message (success or error)
     */
    public String withdraw(String accountNumber, double amount) {
        // Validate inputs
        String error = ValidationHelper.validateAccountNumber(accountNumber);
        if (error != null) return error;

        error = ValidationHelper.validatePositiveAmount(amount, "Withdrawal amount");
        if (error != null) return error;

        // Find account
        Account account = bank.findAccount(accountNumber);
        if (account == null) {
            return "Account not found";
        }

        // Check if account allows withdrawals (SavingsAccount does not)
        if (!(account instanceof com.bankingsystem.Withdrawable)) {
            return "This account type does not allow withdrawals";
        }

        // Perform withdrawal
        boolean success = account.withdraw(amount);
        if (!success) {
            return "Withdrawal failed. Insufficient funds or invalid amount.";
        }

        // Update account in database via DAO
        accountDAO.updateAccount(account);
        
        // Record transaction via DAO
        List<Transaction> transactions = account.getTransactions();
        if (!transactions.isEmpty()) {
            Transaction lastTransaction = transactions.get(transactions.size() - 1);
            transactionDAO.recordTransaction(lastTransaction);
        }

        return String.format("Successfully withdrew BWP %.2f. New balance: BWP %.2f", 
                           amount, account.getBalance());
    }

    /**
     * Transfer money from one account to another.
     * @param fromAccountNumber The source account number
     * @param toAccountNumber The destination account number
     * @param amount The amount to transfer
     * @return Result message (success or error)
     */
    public String transfer(String fromAccountNumber, String toAccountNumber, double amount) {
        // Validate inputs
        String error = ValidationHelper.validateAccountNumber(fromAccountNumber);
        if (error != null) return "Source " + error;

        error = ValidationHelper.validateAccountNumber(toAccountNumber);
        if (error != null) return "Destination " + error;

        if (fromAccountNumber.equals(toAccountNumber)) {
            return "Cannot transfer to the same account";
        }

        error = ValidationHelper.validatePositiveAmount(amount, "Transfer amount");
        if (error != null) return error;

        // Find accounts
        Account fromAccount = bank.findAccount(fromAccountNumber);
        if (fromAccount == null) {
            return "Source account not found";
        }

        Account toAccount = bank.findAccount(toAccountNumber);
        if (toAccount == null) {
            return "Destination account not found";
        }

        // Check if source account allows withdrawals
        if (!(fromAccount instanceof com.bankingsystem.Withdrawable)) {
            return "Source account type does not allow transfers";
        }

        // Perform transfer
        boolean success = fromAccount.transferTo(toAccount, amount);
        if (!success) {
            return "Transfer failed. Insufficient funds or invalid amount.";
        }

        // Update both accounts in database via DAO
        accountDAO.updateAccount(fromAccount);
        accountDAO.updateAccount(toAccount);
        
        // Record transactions via DAO
        List<Transaction> fromTransactions = fromAccount.getTransactions();
        List<Transaction> toTransactions = toAccount.getTransactions();
        if (!fromTransactions.isEmpty()) {
            Transaction lastFromTransaction = fromTransactions.get(fromTransactions.size() - 1);
            if (lastFromTransaction.getType() == Transaction.Type.TRANSFER) {
                transactionDAO.recordTransaction(lastFromTransaction);
            }
        }
        if (!toTransactions.isEmpty()) {
            Transaction lastToTransaction = toTransactions.get(toTransactions.size() - 1);
            if (lastToTransaction.getType() == Transaction.Type.TRANSFER) {
                transactionDAO.recordTransaction(lastToTransaction);
            }
        }

        return String.format("Successfully transferred BWP %.2f from %s to %s. " +
                           "Source balance: BWP %.2f, Destination balance: BWP %.2f",
                           amount, fromAccountNumber, toAccountNumber,
                           fromAccount.getBalance(), toAccount.getBalance());
    }

    /**
     * Get transaction history for an account.
     * @param accountNumber The account number
     * @return List of transactions for the account
     */
    public List<Transaction> getTransactionHistory(String accountNumber) {
        Account account = bank.findAccount(accountNumber);
        if (account == null) {
            return List.of();
        }
        return account.getTransactions();
    }

    /**
     * Get account balance.
     * @param accountNumber The account number
     * @return Balance amount, or null if account not found
     */
    public Double getBalance(String accountNumber) {
        Account account = bank.findAccount(accountNumber);
        if (account == null) {
            return null;
        }
        return account.getBalance();
    }
}

