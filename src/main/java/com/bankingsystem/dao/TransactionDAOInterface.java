package com.bankingsystem.dao;

import com.bankingsystem.Transaction;
import java.util.List;

/**
 * Data Access Object interface for Transaction operations.
 * This interface is Oracle-ready and will be implemented with SQL queries later.
 */
public interface TransactionDAOInterface {
    /**
     * Record a new transaction in the database.
     * @param transaction The transaction to record
     * @return true if successful, false otherwise
     */
    boolean recordTransaction(Transaction transaction);

    /**
     * Get all transactions for a specific account.
     * @param accountNumber The account number
     * @return List of transactions for the account, ordered by timestamp (newest first)
     */
    List<Transaction> getTransactionsByAccount(String accountNumber);

    /**
     * Get all transactions for a specific customer (across all their accounts).
     * @param customerID The customer ID
     * @return List of transactions for the customer, ordered by timestamp (newest first)
     */
    List<Transaction> getTransactionsByCustomer(String customerID);

    /**
     * Get transactions within a date range for an account.
     * @param accountNumber The account number
     * @param startDate The start date (inclusive)
     * @param endDate The end date (inclusive)
     * @return List of transactions within the date range
     */
    List<Transaction> getTransactionsByDateRange(String accountNumber, 
                                                 java.time.LocalDateTime startDate, 
                                                 java.time.LocalDateTime endDate);

    /**
     * Get all transactions from the database.
     * @return List of all transactions, ordered by timestamp (newest first)
     */
    List<Transaction> getAllTransactions();
}

