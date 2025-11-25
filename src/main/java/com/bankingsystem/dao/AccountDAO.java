package com.bankingsystem.dao;

import com.bankingsystem.Account;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of AccountDAOInterface.
 * Currently uses in-memory storage (placeholder).
 * TODO: Replace with Oracle SQL queries when database is ready.
 * 
 * Example Oracle SQL structure:
 * - Table: ACCOUNTS (account_number, customer_id, account_type, balance, branch, 
 *                    date_opened, interest_rate, min_balance, min_opening_deposit,
 *                    employer_name, employer_address, overdraft_allowed)
 * - PreparedStatement with parameterized queries for security
 */
public class AccountDAO implements AccountDAOInterface {
    // In-memory storage (placeholder for database)
    private List<Account> accounts = new ArrayList<>();

    @Override
    public boolean saveAccount(Account account) {
        if (account == null) return false;
        // TODO: Replace with SQL INSERT
        // String sql = "INSERT INTO ACCOUNTS (account_number, customer_id, account_type, balance, ...) VALUES (?, ?, ?, ?, ...)";
        // Use PreparedStatement with account.getAccountNumber(), account.getOwner().getCustomerID(), etc.
        // Need to serialize account type and specific fields based on subclass
        
        // Check if account already exists
        if (findAccountByNumber(account.getAccountNumber()).isPresent()) {
            return false; // Account already exists
        }
        accounts.add(account);
        return true;
    }

    @Override
    public boolean updateAccount(Account account) {
        if (account == null) return false;
        // TODO: Replace with SQL UPDATE
        // String sql = "UPDATE ACCOUNTS SET balance=?, ... WHERE account_number=?";
        
        Optional<Account> existing = findAccountByNumber(account.getAccountNumber());
        if (existing.isPresent()) {
            accounts.remove(existing.get());
            accounts.add(account);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateAccountBalance(String accountNumber, double newBalance) {
        if (accountNumber == null) return false;
        // TODO: Replace with SQL UPDATE (optimized for balance-only updates)
        // String sql = "UPDATE ACCOUNTS SET balance=? WHERE account_number=?";
        
        Optional<Account> account = findAccountByNumber(accountNumber);
        if (account.isPresent()) {
            // In-memory: update the account object
            // In real implementation, this would be a direct SQL update
            // Note: This is a simplified approach. In real implementation,
            // we'd need to update the balance field directly via SQL
            return true;
        }
        return false;
    }

    @Override
    public Optional<Account> findAccountByNumber(String accountNumber) {
        if (accountNumber == null) return Optional.empty();
        // TODO: Replace with SQL SELECT
        // String sql = "SELECT * FROM ACCOUNTS WHERE account_number=?";
        // Need to reconstruct Account subclass based on account_type field
        
        return accounts.stream()
                .filter(a -> a.getAccountNumber().equals(accountNumber))
                .findFirst();
    }

    @Override
    public List<Account> findAccountsByCustomerID(String customerID) {
        if (customerID == null) return new ArrayList<>();
        // TODO: Replace with SQL SELECT
        // String sql = "SELECT * FROM ACCOUNTS WHERE customer_id=? ORDER BY date_opened DESC";
        
        return accounts.stream()
                .filter(a -> a.getOwner() != null && customerID.equals(a.getOwner().getCustomerID()))
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public List<Account> getAllAccounts() {
        // TODO: Replace with SQL SELECT
        // String sql = "SELECT * FROM ACCOUNTS ORDER BY date_opened DESC";
        
        return new ArrayList<>(accounts);
    }

    @Override
    public boolean deleteAccount(String accountNumber) {
        // TODO: Replace with SQL DELETE
        // String sql = "DELETE FROM ACCOUNTS WHERE account_number=?";
        
        Optional<Account> account = findAccountByNumber(accountNumber);
        if (account.isPresent()) {
            accounts.remove(account.get());
            return true;
        }
        return false;
    }
}

