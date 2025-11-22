package com.bankingsystem.controller;

import com.bankingsystem.Customer;
import com.bankingsystem.IndividualCustomer;
import com.bankingsystem.CompanyCustomer;
import com.bankingsystem.dao.CustomerDAOInterface;
import com.bankingsystem.dao.CustomerDAO;
import com.bankingsystem.utils.ValidationHelper;
import com.bankingsystem.utils.IDGenerator;
import java.util.List;
import java.util.Optional;

/**
 * Controller for customer-related operations.
 * Handles customer registration, validation, and retrieval.
 * Follows MVC pattern: View -> Controller -> DAO -> Model
 */
public class CustomerController {
    private final CustomerDAOInterface customerDAO;

    public CustomerController() {
        // Initialize with DAO implementation
        this.customerDAO = new CustomerDAO();
    }

    public CustomerController(CustomerDAOInterface customerDAO) {
        // Allow dependency injection for testing
        this.customerDAO = customerDAO;
    }

    /**
     * Register a new individual customer.
     * @param firstName First name
     * @param lastName Last name
     * @param nationalID National ID
     * @param address Address
     * @param email Email
     * @param phone Phone number
     * @param username Username for login
     * @param password Password for login
     * @return Result message (success or error)
     */
    public String registerIndividualCustomer(String firstName, String lastName, String nationalID,
                                            String address, String email, String phone,
                                            String username, String password) {
        // Validate inputs
        String error = ValidationHelper.validateRequired(firstName, "First name");
        if (error != null) return error;

        error = ValidationHelper.validateRequired(lastName, "Last name");
        if (error != null) return error;

        error = ValidationHelper.validateNationalID(nationalID);
        if (error != null) return error;

        error = ValidationHelper.validateEmail(email);
        if (error != null) return error;

        error = ValidationHelper.validatePhone(phone);
        if (error != null) return error;

        error = ValidationHelper.validateUsername(username);
        if (error != null) return error;

        error = ValidationHelper.validatePassword(password);
        if (error != null) return error;

        // Check if username already exists
        Optional<Customer> existing = customerDAO.findCustomerByUsername(username);
        if (existing.isPresent()) {
            return "Username already exists";
        }

        // Check if national ID already exists
        Optional<Customer> existingByID = customerDAO.findCustomerByNationalID(nationalID);
        if (existingByID.isPresent()) {
            return "Customer with this National ID already exists";
        }

        // Create new customer
        String customerID = IDGenerator.generateCustomerID();
        IndividualCustomer customer = new IndividualCustomer(customerID, firstName, lastName, nationalID);
        customer.setUsername(username);
        customer.setPassword(password);
        customer.setAddress(address);
        customer.setEmail(email);
        customer.setPhone(phone);

        // Save to database via DAO
        if (customerDAO.saveCustomer(customer)) {
            return "Customer registered successfully. Customer ID: " + customerID;
        } else {
            return "Failed to register customer";
        }
    }

    /**
     * Register a new company customer.
     * @param companyName Company name
     * @param registrationNumber Registration number
     * @param address Address
     * @param email Email
     * @param phone Phone number
     * @return Result message (success or error)
     */
    public String registerCompanyCustomer(String companyName, String registrationNumber,
                                         String address, String email, String phone) {
        // Validate inputs
        String error = ValidationHelper.validateRequired(companyName, "Company name");
        if (error != null) return error;

        error = ValidationHelper.validateRequired(registrationNumber, "Registration number");
        if (error != null) return error;

        error = ValidationHelper.validateEmail(email);
        if (error != null) return error;

        error = ValidationHelper.validatePhone(phone);
        if (error != null) return error;

        // Check if registration number already exists
        Optional<Customer> existing = customerDAO.findCustomerByRegistrationNumber(registrationNumber);
        if (existing.isPresent()) {
            return "Company with this registration number already exists";
        }

        // Create new company customer
        String customerID = IDGenerator.generateCustomerID();
        CompanyCustomer customer = new CompanyCustomer(customerID, companyName, registrationNumber);
        customer.setAddress(address);
        customer.setEmail(email);
        customer.setPhone(phone);

        // Save to database via DAO
        if (customerDAO.saveCustomer(customer)) {
            return "Company registered successfully. Customer ID: " + customerID;
        } else {
            return "Failed to register company";
        }
    }

    /**
     * Find a customer by customer ID.
     * @param customerID The customer ID
     * @return Optional containing the customer if found
     */
    public Optional<Customer> findCustomerByID(String customerID) {
        return customerDAO.findCustomerByID(customerID);
    }

    /**
     * Find a customer by username.
     * @param username The username
     * @return Optional containing the customer if found
     */
    public Optional<Customer> findCustomerByUsername(String username) {
        return customerDAO.findCustomerByUsername(username);
    }

    /**
     * Get all customers.
     * @return List of all customers
     */
    public List<Customer> getAllCustomers() {
        return customerDAO.getAllCustomers();
    }

    /**
     * Update customer information.
     * @param customer The customer to update
     * @return true if successful, false otherwise
     */
    public boolean updateCustomer(Customer customer) {
        if (customer == null) return false;
        return customerDAO.updateCustomer(customer);
    }
}

