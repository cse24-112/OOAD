package com.bankingsystem.persistence;

import com.bankingsystem.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Additional methods to add to AccountDAOImpl class
 * Insert these methods in AccountDAOImpl.java before the deleteAccount() method
 */
public class AccountDAOExtension {
    
    // ADD THESE THREE METHODS TO AccountDAOImpl.java

    /**
     * Get all pending accounts ordered by date created
     * Retrieves directly from database
     */
    public List<Account> getPendingAccounts() {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts WHERE status = 'PENDING' ORDER BY date_opened DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Account account = mapRowToAccount(rs);
                if (account != null) {
                    accounts.add(account);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting pending accounts: " + e.getMessage());
            e.printStackTrace();
        }

        return accounts;
    }

    /**
     * Get all approved accounts ordered by date created
     * Retrieves directly from database
     */
    public List<Account> getApprovedAccounts() {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts WHERE status = 'APPROVED' ORDER BY date_opened DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Account account = mapRowToAccount(rs);
                if (account != null) {
                    accounts.add(account);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting approved accounts: " + e.getMessage());
            e.printStackTrace();
        }

        return accounts;
    }

    /**
     * Get accounts for a specific customer filtered by status
     * Useful for checking pending vs approved accounts per customer
     */
    public List<Account> getAccountsByCustomerIdAndStatus(String customerId, String status) {
        if (customerId == null) return new ArrayList<>();

        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts WHERE customer_id = ? AND status = ? ORDER BY date_opened DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, customerId);
            pstmt.setString(2, status);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Account account = mapRowToAccount(rs);
                if (account != null) {
                    accounts.add(account);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting accounts by customer and status: " + e.getMessage());
            e.printStackTrace();
        }

        return accounts;
    }
}
