package com.bankingsystem.mvc.controller;

import com.bankingsystem.*;
import com.bankingsystem.mvc.model.Customer;

/**
 * Handles login for Customer, Company and Staff
 * Supports both password and PIN-based authentication
 */
public class LoginController {

    public LoginController() {}

    /**
     * Login using username and password/PIN
     * @param username Username or email
     * @param credential Password or PIN
     * @param userType One of: "Individual", "Company", "Staff"
     * @return LoginResult if successful, null otherwise
     */
    public LoginResult login(String username, String credential, String userType) {
        try {
            // First try to authenticate against core Bank system
            Bank bank = com.bankingsystem.gui.MainApp.getBank();
            
            if ("Staff".equalsIgnoreCase(userType)) {
                // Try staff authentication
                if (bank.authenticateStaff(username, credential)) {
                    LoginResult lr = new LoginResult(LoginResult.Role.STAFF);
                    return lr;
                }
            } else {
                // Try customer authentication (username + PIN or password)
                com.bankingsystem.Customer cust = findCustomerByCredentials(bank, username, credential);
                if (cust != null) {
                    LoginResult lr = new LoginResult(
                        cust instanceof CompanyCustomer ? LoginResult.Role.COMPANY : LoginResult.Role.CUSTOMER
                    );
                    lr.setCoreBankCustomer(cust);
                    
                    // Convert to MVC model if needed
                    if (cust instanceof IndividualCustomer) {
                        IndividualCustomer ic = (IndividualCustomer) cust;
                        Customer mvcCustomer = new Customer(
                            ic.getCustomerID(),
                            ic.getFirstName(),
                            ic.getLastName(),
                            ic.getNationalID(),
                            ic.getEmail(),
                            ic.getPhone(),
                            "INDIVIDUAL",
                            ic.getPassword()
                        );
                        lr.setCustomer(mvcCustomer);
                    } else if (cust instanceof CompanyCustomer) {
                        CompanyCustomer cc = (CompanyCustomer) cust;
                        Customer mvcCustomer = new Customer(
                            cc.getCustomerID(),
                            "",
                            cc.getCompanyName(),
                            cc.getRegistrationNumber(),
                            "",
                            "",
                            "COMPANY",
                            ""
                        );
                        lr.setCustomer(mvcCustomer);
                    }
                    
                    return lr;
                }
            }
            return null;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Find a customer by username and credential (password or PIN)
     */
    private com.bankingsystem.Customer findCustomerByCredentials(Bank bank, String username, String credential) {
        for (com.bankingsystem.Customer c : bank.getCustomers()) {
            if (c instanceof IndividualCustomer) {
                IndividualCustomer ic = (IndividualCustomer) c;
                if (username.equals(ic.getUsername())) {
                    // Try password first
                    if (ic.getPassword() != null && ic.getPassword().equals(credential)) {
                        return ic;
                    }
                    // Try PIN
                    if (ic.getPin() != null && ic.getPin().equals(credential)) {
                        return ic;
                    }
                }
            }
        }
        return null;
    }
}


