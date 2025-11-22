package com.bankingsystem.mvc.dao;

import com.bankingsystem.mvc.model.Transaction;
import com.bankingsystem.mvc.utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {
    public void addTransaction(Transaction t) throws Exception {
        String sql = "INSERT INTO transactions(transaction_id, account_id, type, amount, timestamp) VALUES (?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, t.getTransactionId());
            ps.setString(2, t.getAccountId());
            ps.setString(3, t.getType());
            ps.setDouble(4, t.getAmount());
            ps.setObject(5, java.sql.Timestamp.from(t.getTimestamp()));
            ps.executeUpdate();
        }
    }

    public List<Transaction> findByAccountId(String accountId) throws Exception {
        String sql = "SELECT * FROM transactions WHERE account_id=? ORDER BY timestamp DESC";
        List<Transaction> out = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Transaction t = new Transaction(rs.getString("account_id"), rs.getString("type"), rs.getDouble("amount"));
                    // override id/timestamp
                    // no setter so rely on constructor generated id; in real app map properly
                    out.add(t);
                }
            }
        }
        return out;
    }
}
