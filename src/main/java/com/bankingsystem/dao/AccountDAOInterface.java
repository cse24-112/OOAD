package com.bankingsystem.dao;

import com.bankingsystem.Account;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for Account operations.
 * This interface is Oracle-ready and will be implemented with SQL queries later.
 */
public interface AccountDAOInterface {
    /**
     * Save a new account to the database.
     * @param account The account to save
     * @return true if successful, false otherwise
     */
    boolean saveAccount(Account account);

    /**
     * Update an existing account in the database (e.g., after balance change).
     * @param account The account to update
     * @return true if successful, false otherwise
     */
    boolean updateAccount(Account account);

    /**
     * Update only the account balance (optimized for frequent balance updates).
     * @param accountNumber The account number
     * @param newBalance The new balance
     * @return true if successful, false otherwise
     */
    boolean updateAccountBalance(String accountNumber, double newBalance);

    /**
     * Find an account by account number.
     * @param accountNumber The account number to search for
     * @return Optional containing the account if found
     */
    Optional<Account> findAccountByNumber(String accountNumber);

    /**
     * Find all accounts for a specific customer.
     * @param customerID The customer ID
     * @return List of accounts belonging to the customer
     */
    List<Account> findAccountsByCustomerID(String customerID);

    /**
     * Get all accounts from the database.
     * @return List of all accounts
     */
    List<Account> getAllAccounts();

    /**
     * Delete an account from the database.
     * @param accountNumber The account number to delete
     * @return true if successful, false otherwise
     */
    boolean deleteAccount(String accountNumber);
}

