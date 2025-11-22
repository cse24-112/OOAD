package com.bankingsystem.mvc.dao;

import com.bankingsystem.mvc.model.Staff;
import com.bankingsystem.mvc.utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class StaffDAO {
    public void createStaff(Staff s) throws Exception {
        String sql = "INSERT INTO staff(staff_id, username, password) VALUES (?,?,?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getStaffId());
            ps.setString(2, s.getUsername());
            ps.setString(3, s.getPassword());
            ps.executeUpdate();
        }
    }

    public Staff findByUsernameAndPassword(String username, String password) throws Exception {
        String sql = "SELECT * FROM staff WHERE username=? AND password=?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Staff(rs.getString("staff_id"), rs.getString("username"), rs.getString("password"));
                }
            }
        }
        return null;
    }

    public List<Staff> listAll() throws Exception {
        String sql = "SELECT * FROM staff";
        List<Staff> out = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                out.add(new Staff(rs.getString("staff_id"), rs.getString("username"), rs.getString("password")));
            }
        }
        return out;
    }
}
