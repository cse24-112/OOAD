package com.bankingsystem.persistence;

import com.bankingsystem.Transaction;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * TransactionDAOImpl - JDBC implementation for Transaction persistence
 * Handles CRUD operations for transactions in H2 database
 */
public class TransactionDAOImpl {
    private Connection connection;

    public TransactionDAOImpl() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    /**
     * Save a new transaction to database
     */
    public boolean saveTransaction(String accountNumber, Transaction transaction) {
        if (accountNumber == null || transaction == null) return false;

        String sql = "INSERT INTO transactions (transaction_id, account_number, transaction_type, amount, " +
                "balance_after, description, transaction_timestamp) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            String transactionId = "TXN" + System.nanoTime();
            pstmt.setString(1, transactionId);
            pstmt.setString(2, accountNumber);
            pstmt.setString(3, transaction.getType().toString());
            pstmt.setDouble(4, transaction.getAmount());
            pstmt.setDouble(5, transaction.getBalanceAfter());
            pstmt.setString(6, transaction.getDescription());
            pstmt.setTimestamp(7, Timestamp.valueOf(transaction.getTimestamp()));

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error saving transaction: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get all transactions for an account
     */
    public List<Transaction> getTransactionsByAccountNumber(String accountNumber) {
        if (accountNumber == null) return new ArrayList<>();

        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE account_number = ? ORDER BY transaction_timestamp DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Transaction transaction = mapRowToTransaction(rs);
                if (transaction != null) {
                    transactions.add(transaction);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting transactions: " + e.getMessage());
            e.printStackTrace();
        }

        return transactions;
    }

    /**
     * Get all transactions
     */
    public List<Transaction> getAllTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions ORDER BY transaction_timestamp DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Transaction transaction = mapRowToTransaction(rs);
                if (transaction != null) {
                    transactions.add(transaction);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting all transactions: " + e.getMessage());
            e.printStackTrace();
        }

        return transactions;
    }

    /**
     * Get transaction count for an account
     */
    public int getTransactionCount(String accountNumber) {
        if (accountNumber == null) return 0;

        String sql = "SELECT COUNT(*) FROM transactions WHERE account_number = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting transaction count: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * Get total amount transferred in/out for an account
     */
    public double getTotalTransferred(String accountNumber) {
        if (accountNumber == null) return 0;

        String sql = "SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE account_number = ? AND transaction_type = 'TRANSFER'";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting total transferred: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * Get total interest earned for an account
     */
    public double getTotalInterestEarned(String accountNumber) {
        if (accountNumber == null) return 0;

        String sql = "SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE account_number = ? AND transaction_type = 'INTEREST'";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting total interest earned: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * Map database row to Transaction object
     */
    private Transaction mapRowToTransaction(ResultSet rs) throws SQLException {
        Transaction.Type type = Transaction.Type.valueOf(rs.getString("transaction_type"));
        double amount = rs.getDouble("amount");
        double balanceAfter = rs.getDouble("balance_after");
        String description = rs.getString("description");

        Transaction transaction = new Transaction(type, amount, balanceAfter, description);
        return transaction;
    }
}
