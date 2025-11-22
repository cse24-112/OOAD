package com.bankingsystem.mvc.dao;

import com.bankingsystem.mvc.model.Customer;
import com.bankingsystem.mvc.utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {
    public void createCustomer(Customer c) throws Exception {
        String sql = "INSERT INTO customers(customer_id, firstname, surname, address, email, phone, customer_type, password) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getCustomerId());
            ps.setString(2, c.getFirstname());
            ps.setString(3, c.getSurname());
            ps.setString(4, c.getAddress());
            ps.setString(5, c.getEmail());
            ps.setString(6, c.getPhone());
            ps.setString(7, c.getCustomerType());
            ps.setString(8, c.getPassword());
            ps.executeUpdate();
        }
    }

    public Customer findByEmailAndPassword(String email, String password) throws Exception {
        String sql = "SELECT * FROM customers WHERE email=? AND password=?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Customer c = new Customer(rs.getString("customer_id"), rs.getString("firstname"), rs.getString("surname"), rs.getString("address"), rs.getString("email"), rs.getString("phone"), rs.getString("customer_type"), rs.getString("password"));
                    return c;
                }
            }
        }
        return null;
    }

    public Customer findById(String id) throws Exception {
        String sql = "SELECT * FROM customers WHERE customer_id=?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return new Customer(rs.getString("customer_id"), rs.getString("firstname"), rs.getString("surname"), rs.getString("address"), rs.getString("email"), rs.getString("phone"), rs.getString("customer_type"), rs.getString("password"));
            }
        }
        return null;
    }

    public List<Customer> listAll() throws Exception {
        String sql = "SELECT * FROM customers";
        List<Customer> out = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                out.add(new Customer(rs.getString("customer_id"), rs.getString("firstname"), rs.getString("surname"), rs.getString("address"), rs.getString("email"), rs.getString("phone"), rs.getString("customer_type"), rs.getString("password")));
            }
        }
        return out;
    }
}
