package com.bankingsystem.mvc.controller;

import com.bankingsystem.mvc.dao.AccountDAO;
import com.bankingsystem.mvc.dao.CustomerDAO;
import com.bankingsystem.mvc.model.Account;
import com.bankingsystem.mvc.model.Customer;

public class StaffController {
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final AccountDAO accountDAO = new AccountDAO();

    public String createCustomer(Customer c) {
        try {
            customerDAO.createCustomer(c);
            return "Customer created";
        } catch (Exception ex) {
            ex.printStackTrace();
            return "Failed to create customer: " + ex.getMessage();
        }
    }

    public String createAccount(Account a) {
        try {
            // rules: investment requires 500 min
            if ("INVESTMENT".equalsIgnoreCase(a.getAccountType())) {
                if (!com.bankingsystem.mvc.model.InvestmentAccount.meetsOpeningDeposit(a.getBalance())) {
                    return "Investment account requires minimum BWP 500 opening deposit";
                }
            }
            if ("CHEQUE".equalsIgnoreCase(a.getAccountType())) {
                if (a.getEmployerName() == null || a.getEmployerName().isBlank() || a.getEmployerAddress() == null || a.getEmployerAddress().isBlank()) {
                    return "Cheque account requires employer name and address";
                }
            }
            // persist
            accountDAO.createAccount(a);
            return "Account created";
        } catch (Exception ex) {
            ex.printStackTrace();
            return "Failed to create account: " + ex.getMessage();
        }
    }
}
