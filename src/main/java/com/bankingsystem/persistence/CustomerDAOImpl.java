package com.bankingsystem.persistence;

import com.bankingsystem.Customer;
import com.bankingsystem.IndividualCustomer;
import com.bankingsystem.CompanyCustomer;
import com.bankingsystem.utils.IDGenerator;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * CustomerDAOImpl - JDBC implementation for Customer persistence
 * Handles CRUD operations for customers in H2 database
 */
public class CustomerDAOImpl {

    public CustomerDAOImpl() {
    }

    /**
     * Save a new customer to database
     */
    public boolean saveCustomer(Customer customer) {
        if (customer == null) return false;

        // Ensure individual customers have a national ID (generate if missing)
        if (customer instanceof IndividualCustomer) {
            IndividualCustomer ic = (IndividualCustomer) customer;
            if (ic.getNationalID() == null || ic.getNationalID().isBlank()) {
                ic.setNationalID(IDGenerator.generateNationalId());
            }
        }

        String sql = "INSERT INTO customers (customer_id, first_name, last_name, national_id, customer_type, " +
                "username, password, email, phone, company_name, registration_number) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, customer.getCustomerID());

            if (customer instanceof IndividualCustomer) {
                IndividualCustomer ic = (IndividualCustomer) customer;
                pstmt.setString(2, ic.getFirstName());
                pstmt.setString(3, ic.getLastName());
                pstmt.setString(4, ic.getNationalID());
                pstmt.setString(5, "INDIVIDUAL");
                pstmt.setString(6, ic.getUsername());
                pstmt.setString(7, ic.getPassword());
                pstmt.setString(8, ic.getEmail());
                pstmt.setString(9, ic.getPhone());
                pstmt.setString(10, null);
                pstmt.setString(11, null);
            } else if (customer instanceof CompanyCustomer) {
                CompanyCustomer cc = (CompanyCustomer) customer;
                pstmt.setString(2, null);
                pstmt.setString(3, null);
                pstmt.setString(4, null);
                pstmt.setString(5, "COMPANY");
                pstmt.setString(6, null);
                pstmt.setString(7, null);
                pstmt.setString(8, cc.getEmail());
                pstmt.setString(9, cc.getPhone());
                pstmt.setString(10, cc.getCompanyName());
                pstmt.setString(11, cc.getRegistrationNumber());
            }

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error saving customer: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Update existing customer
     */
    public boolean updateCustomer(Customer customer) {
        if (customer == null) return false;

        String sql = "UPDATE customers SET first_name=?, last_name=?, national_id=?, username=?, password=?, " +
                "email=?, phone=?, company_name=?, registration_number=? WHERE customer_id=?";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (customer instanceof IndividualCustomer) {
                IndividualCustomer ic = (IndividualCustomer) customer;
                pstmt.setString(1, ic.getFirstName());
                pstmt.setString(2, ic.getLastName());
                pstmt.setString(3, ic.getNationalID());
                pstmt.setString(4, ic.getUsername());
                pstmt.setString(5, ic.getPassword());
                pstmt.setString(6, ic.getEmail());
                pstmt.setString(7, ic.getPhone());
                pstmt.setString(8, null);
                pstmt.setString(9, null);
            } else if (customer instanceof CompanyCustomer) {
                CompanyCustomer cc = (CompanyCustomer) customer;
                pstmt.setString(1, null);
                pstmt.setString(2, null);
                pstmt.setString(3, cc.getRegistrationNumber());
                pstmt.setString(4, cc.getNationalID());
                pstmt.setString(5, null);
                pstmt.setString(6, cc.getEmail());
                pstmt.setString(7, cc.getPhone());
                pstmt.setString(8, cc.getCompanyName());
                pstmt.setString(9, cc.getRegistrationNumber());
            }

            pstmt.setString(10, customer.getCustomerID());
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating customer: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Find customer by ID
     */
    public Optional<Customer> findCustomerById(String customerId) {
        if (customerId == null) return Optional.empty();

        String sql = "SELECT * FROM customers WHERE customer_id = ?";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, customerId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRowToCustomer(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding customer: " + e.getMessage());
            e.printStackTrace();
        }

        return Optional.empty();
    }

    /**
     * Find customer by username
     */
    public Optional<Customer> findCustomerByUsername(String username) {
        if (username == null) return Optional.empty();

        String sql = "SELECT * FROM customers WHERE username = ?";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRowToCustomer(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding customer by username: " + e.getMessage());
            e.printStackTrace();
        }

        return Optional.empty();
    }

    /**
     * Get all customers
     */
    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers ORDER BY creation_date DESC";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                customers.add(mapRowToCustomer(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all customers: " + e.getMessage());
            e.printStackTrace();
        }

        return customers;
    }

    /**
     * Delete customer
     */
    public boolean deleteCustomer(String customerId) {
        if (customerId == null) return false;

        String sql = "DELETE FROM customers WHERE customer_id = ?";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, customerId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting customer: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Map database row to Customer object
     */
    private Customer mapRowToCustomer(ResultSet rs) throws SQLException {
        String customerType = rs.getString("customer_type");
        String customerId = rs.getString("customer_id");

        if ("INDIVIDUAL".equals(customerType)) {
            IndividualCustomer customer = new IndividualCustomer(
                    customerId,
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("national_id")
            );
            customer.setUsername(rs.getString("username"));
            customer.setPassword(rs.getString("password"));
            customer.setEmail(rs.getString("email"));
            customer.setPhone(rs.getString("phone"));
            return customer;
        } else if ("COMPANY".equals(customerType)) {
            CompanyCustomer customer = new CompanyCustomer(
                    customerId,
                    rs.getString("company_name"),
                    rs.getString("registration_number")
            );
            customer.setNationalID(rs.getString("username")); // Store username in national_id for company
            customer.setEmail(rs.getString("email"));
            customer.setPhone(rs.getString("phone"));
            return customer;
        }

        return null;
    }
}
