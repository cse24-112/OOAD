package com.bankingsystem.dao;

import com.bankingsystem.Customer;
import com.bankingsystem.IndividualCustomer;
import com.bankingsystem.CompanyCustomer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of CustomerDAOInterface.
 * Currently uses in-memory storage (placeholder).
 * TODO: Replace with Oracle SQL queries when database is ready.
 * 
 * Example Oracle SQL structure:
 * - Table: CUSTOMERS (customer_id, first_name, last_name, address, email, phone, 
 *                     national_id, registration_number, customer_type, created_date)
 * - PreparedStatement with parameterized queries for security
 */
public class CustomerDAO implements CustomerDAOInterface {
    // In-memory storage (placeholder for database)
    private List<Customer> customers = new ArrayList<>();

    @Override
    public boolean saveCustomer(Customer customer) {
        if (customer == null) return false;
        // TODO: Replace with SQL INSERT
        // String sql = "INSERT INTO CUSTOMERS (customer_id, first_name, last_name, ...) VALUES (?, ?, ?, ...)";
        // Use PreparedStatement with customer.getCustomerID(), customer.getFirstName(), etc.
        
        // Check if customer already exists
        if (findCustomerByID(customer.getCustomerID()).isPresent()) {
            return false; // Customer already exists
        }
        customers.add(customer);
        return true;
    }

    @Override
    public boolean updateCustomer(Customer customer) {
        if (customer == null) return false;
        // TODO: Replace with SQL UPDATE
        // String sql = "UPDATE CUSTOMERS SET first_name=?, last_name=?, ... WHERE customer_id=?";
        
        Optional<Customer> existing = findCustomerByID(customer.getCustomerID());
        if (existing.isPresent()) {
            customers.remove(existing.get());
            customers.add(customer);
            return true;
        }
        return false;
    }

    @Override
    public Optional<Customer> findCustomerByID(String customerID) {
        if (customerID == null) return Optional.empty();
        // TODO: Replace with SQL SELECT
        // String sql = "SELECT * FROM CUSTOMERS WHERE customer_id=?";
        
        return customers.stream()
                .filter(c -> c.getCustomerID().equals(customerID))
                .findFirst();
    }

    @Override
    public Optional<Customer> findCustomerByUsername(String username) {
        if (username == null) return Optional.empty();
        // TODO: Replace with SQL SELECT
        // String sql = "SELECT * FROM CUSTOMERS WHERE username=?";
        // Note: username field would need to be added to database schema
        
        return customers.stream()
                .filter(c -> c instanceof IndividualCustomer)
                .map(c -> (IndividualCustomer) c)
                .filter(c -> username.equals(c.getUsername()))
                .map(c -> (Customer) c)
                .findFirst();
    }

    @Override
    public Optional<Customer> findCustomerByNationalID(String nationalID) {
        if (nationalID == null) return Optional.empty();
        // TODO: Replace with SQL SELECT
        // String sql = "SELECT * FROM CUSTOMERS WHERE national_id=? AND customer_type='INDIVIDUAL'";
        
        return customers.stream()
                .filter(c -> c instanceof IndividualCustomer)
                .map(c -> (IndividualCustomer) c)
                .filter(c -> nationalID.equals(c.getNationalId()))
                .map(c -> (Customer) c)
                .findFirst();
    }

    @Override
    public Optional<Customer> findCustomerByRegistrationNumber(String registrationNumber) {
        if (registrationNumber == null) return Optional.empty();
        // TODO: Replace with SQL SELECT
        // String sql = "SELECT * FROM CUSTOMERS WHERE registration_number=? AND customer_type='COMPANY'";
        
        return customers.stream()
                .filter(c -> c instanceof CompanyCustomer)
                .map(c -> (CompanyCustomer) c)
                .filter(c -> registrationNumber.equals(c.getRegistrationNumber()))
                .map(c -> (Customer) c)
                .findFirst();
    }

    @Override
    public List<Customer> getAllCustomers() {
        // TODO: Replace with SQL SELECT
        // String sql = "SELECT * FROM CUSTOMERS ORDER BY created_date DESC";
        
        return new ArrayList<>(customers);
    }

    @Override
    public boolean deleteCustomer(String customerID) {
        // TODO: Replace with SQL DELETE
        // String sql = "DELETE FROM CUSTOMERS WHERE customer_id=?";
        
        Optional<Customer> customer = findCustomerByID(customerID);
        if (customer.isPresent()) {
            customers.remove(customer.get());
            return true;
        }
        return false;
    }
}

