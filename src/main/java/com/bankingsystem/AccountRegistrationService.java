package com.bankingsystem;

import com.bankingsystem.persistence.AccountDAOImpl;
import com.bankingsystem.persistence.CustomerDAOImpl;
import java.util.UUID;

/**
 * Service for handling new account and customer registration.
 * Validates input, creates customers, opens accounts with PENDING status,
 * and stores approval notifications.
 */
public class AccountRegistrationService {

    /**
     * Register a new individual customer with an account.
     * Account is created in PENDING status and will await staff approval.
     * @return newly created customer or null if registration fails
     */
    public static IndividualCustomer registerIndividualCustomer(
            Bank bank,
            AccountRegistrationData data) {

        if (!data.isValidIndividualRegistration()) {
            return null;
        }

        // Generate unique customer ID
        String customerId = "CUST-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // Create individual customer
        IndividualCustomer customer = new IndividualCustomer(
            customerId,
            data.getFirstName(),
            data.getLastName(),
            data.getNationalID()
        );

        // Set login credentials
        customer.setUsername(data.getUsername());
        if (data.getPin() != null && !data.getPin().isEmpty()) {
            customer.setPin(data.getPin());
        }
        if (data.getPassword() != null && !data.getPassword().isEmpty()) {
            customer.setPassword(data.getPassword());
        }

        // Open account with initial deposit
        String accountType = data.getAccountType();
        double initialDeposit = data.getInitialDeposit();

        if ("Cheque".equalsIgnoreCase(accountType)) {
            if (!data.isValidChequeEmploymentInfo()) {
                return null; // Invalid employment info
            }
            customer.setEmployer(
                data.getEmployerName(),
                data.getEmployerAddress(),
                data.getEmploymentType()
            );
        }

        Account account = customer.openAccount(accountType, initialDeposit);
        if (account == null) {
            return null; // Account creation failed
        }

        // Add customer to bank
        bank.registerCustomer(customer);

        // Persist customer and account
        try {
            CustomerDAOImpl customerDAO = new CustomerDAOImpl();
            customerDAO.saveCustomer(customer);
        } catch (Exception e) {
            System.err.println("Failed to persist new customer: " + e.getMessage());
        }

        try {
            AccountDAOImpl accountDAO = new AccountDAOImpl();
            accountDAO.saveAccount(account);
        } catch (Exception e) {
            System.err.println("Failed to persist new account: " + e.getMessage());
        }

        return customer;
    }

    /**
     * Register a new company customer with an account.
     * @return newly created company customer or null if registration fails
     */
    public static CompanyCustomer registerCompanyCustomer(
            Bank bank,
            String companyName,
            String registrationNumber,
            String username,
            String password,
            CompanyRegistration companyInfo) {

        if (companyName == null || companyName.trim().isEmpty()
            || registrationNumber == null || registrationNumber.trim().isEmpty()
            || companyInfo == null
            || username == null || username.trim().isEmpty()
            || password == null || password.trim().isEmpty()) {
            return null;
        }

        // Generate unique customer ID
        String customerId = "COMP-" + registrationNumber;

        // Create company customer
        CompanyCustomer customer = new CompanyCustomer(customerId, companyName, registrationNumber);
        customer.setCompanyRegistration(companyInfo);
        
        // Set username and password for login
        customer.setUsername(username);
        customer.setPassword(password);

        // Open Savings account by default for company
        Account account = bank.openAccount(customer, "Savings", 0.0);
        if (account == null) {
            return null;
        }

        // Add customer to bank
        bank.registerCustomer(customer);

        // Persist customer and account
        try {
            CustomerDAOImpl customerDAO = new CustomerDAOImpl();
            customerDAO.saveCustomer(customer);
        } catch (Exception e) {
            System.err.println("Failed to persist new company customer: " + e.getMessage());
        }

        try {
            AccountDAOImpl accountDAO = new AccountDAOImpl();
            accountDAO.saveAccount(account);
        } catch (Exception e) {
            System.err.println("Failed to persist new company account: " + e.getMessage());
        }

        return customer;
    }

    /**
     * Check if username is already taken
     */
    public static boolean isUsernameTaken(Bank bank, String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        
        for (Customer customer : bank.getCustomers()) {
            if (username.equals(customer.getUsername())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Validate PIN format (numeric only, 4-6 digits)
     */
    public static boolean isValidPin(String pin) {
        if (pin == null || pin.isEmpty()) {
            return false;
        }
        return pin.matches("^\\d{4,6}$");
    }

    /**
     * Validate password format (at least 6 characters)
     */
    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    /**
     * Validate National ID format (reasonable length)
     */
    public static boolean isValidNationalID(String nationalID) {
        return nationalID != null && nationalID.length() >= 8 && nationalID.length() <= 20;
    }
}
