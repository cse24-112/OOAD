package com.bankingsystem.mvc.dao;

import com.bankingsystem.mvc.model.Account;
import com.bankingsystem.mvc.utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {
    public void createAccount(Account a) throws Exception {
        String sql = "INSERT INTO accounts(account_id, account_number, customer_id, account_type, balance, branch, employer_name, employer_address) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, a.getAccountId());
            ps.setString(2, a.getAccountNumber());
            ps.setString(3, a.getCustomerId());
            ps.setString(4, a.getAccountType());
            ps.setDouble(5, a.getBalance());
            ps.setString(6, a.getBranch());
            ps.setString(7, a.getEmployerName());
            ps.setString(8, a.getEmployerAddress());
            ps.executeUpdate();
        }
    }

    public void updateBalance(String accountId, double newBalance) throws Exception {
        String sql = "UPDATE accounts SET balance=? WHERE account_id=?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, newBalance);
            ps.setString(2, accountId);
            ps.executeUpdate();
        }
    }

    public List<Account> findByCustomerId(String customerId) throws Exception {
        String sql = "SELECT * FROM accounts WHERE customer_id=?";
        List<Account> out = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // minimal mapping using Account base; specific behavior can be wrapped later
                            String type = rs.getString("account_type");
                            Account a;
                            if ("SAVINGS".equalsIgnoreCase(type)) {
                                a = new com.bankingsystem.mvc.model.SavingsAccount(rs.getString("customer_id"), rs.getDouble("balance"), rs.getString("branch"));
                            } else if ("INVESTMENT".equalsIgnoreCase(type)) {
                                a = new com.bankingsystem.mvc.model.InvestmentAccount(rs.getString("customer_id"), rs.getDouble("balance"), rs.getString("branch"));
                            } else {
                                a = new com.bankingsystem.mvc.model.ChequeAccount(rs.getString("customer_id"), rs.getDouble("balance"), rs.getString("branch"), rs.getString("employer_name"), rs.getString("employer_address"));
                            }
                            // set ids via setters
                            a.setAccountId(rs.getString("account_id"));
                            a.setAccountNumber(rs.getString("account_number"));
                            a.setEmployerName(rs.getString("employer_name"));
                            a.setEmployerAddress(rs.getString("employer_address"));
                            a.setBalance(rs.getDouble("balance"));
                            out.add(a);
                }
            }
        }
        return out;
    }

    public Account findByAccountNumber(String accountNumber) throws Exception {
        String sql = "SELECT * FROM accounts WHERE account_number=?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String type = rs.getString("account_type");
                    Account a;
                    if ("SAVINGS".equalsIgnoreCase(type)) {
                        a = new com.bankingsystem.mvc.model.SavingsAccount(rs.getString("customer_id"), rs.getDouble("balance"), rs.getString("branch"));
                    } else if ("INVESTMENT".equalsIgnoreCase(type)) {
                        a = new com.bankingsystem.mvc.model.InvestmentAccount(rs.getString("customer_id"), rs.getDouble("balance"), rs.getString("branch"));
                    } else {
                        a = new com.bankingsystem.mvc.model.ChequeAccount(rs.getString("customer_id"), rs.getDouble("balance"), rs.getString("branch"), rs.getString("employer_name"), rs.getString("employer_address"));
                    }
                    a.setAccountId(rs.getString("account_id"));
                    a.setAccountNumber(rs.getString("account_number"));
                    a.setEmployerName(rs.getString("employer_name"));
                    a.setEmployerAddress(rs.getString("employer_address"));
                    a.setBalance(rs.getDouble("balance"));
                    return a;
                }
            }
        }
        return null;
    }
}
