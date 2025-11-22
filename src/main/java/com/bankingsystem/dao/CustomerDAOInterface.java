package com.bankingsystem.dao;

import com.bankingsystem.Customer;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for Customer operations.
 * This interface is Oracle-ready and will be implemented with SQL queries later.
 */
public interface CustomerDAOInterface {
    /**
     * Save a new customer to the database.
     * @param customer The customer to save
     * @return true if successful, false otherwise
     */
    boolean saveCustomer(Customer customer);

    /**
     * Update an existing customer in the database.
     * @param customer The customer to update
     * @return true if successful, false otherwise
     */
    boolean updateCustomer(Customer customer);

    /**
     * Find a customer by their customer ID.
     * @param customerID The customer ID to search for
     * @return Optional containing the customer if found
     */
    Optional<Customer> findCustomerByID(String customerID);

    /**
     * Find a customer by username (for authentication).
     * @param username The username to search for
     * @return Optional containing the customer if found
     */
    Optional<Customer> findCustomerByUsername(String username);

    /**
     * Find a customer by national ID (for IndividualCustomer).
     * @param nationalID The national ID to search for
     * @return Optional containing the customer if found
     */
    Optional<Customer> findCustomerByNationalID(String nationalID);

    /**
     * Find a customer by registration number (for CompanyCustomer).
     * @param registrationNumber The registration number to search for
     * @return Optional containing the customer if found
     */
    Optional<Customer> findCustomerByRegistrationNumber(String registrationNumber);

    /**
     * Get all customers from the database.
     * @return List of all customers
     */
    List<Customer> getAllCustomers();

    /**
     * Delete a customer from the database.
     * @param customerID The customer ID to delete
     * @return true if successful, false otherwise
     */
    boolean deleteCustomer(String customerID);
}

