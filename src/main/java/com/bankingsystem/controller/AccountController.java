package com.bankingsystem.controller;

import com.bankingsystem.Account;
import com.bankingsystem.Bank;
import com.bankingsystem.Customer;
import com.bankingsystem.IndividualCustomer;
import com.bankingsystem.EmploymentInfo;
import com.bankingsystem.AccountRequest;

import java.util.List;

/**
 * Controller for account operations. Handles calls from the View and uses the model.
 */
public class AccountController {
    private final Bank bank;
    private final Customer customer;

    public AccountController(Bank bank, Customer customer) {
        this.bank = bank;
        this.customer = customer;
    }

    public List<Account> listAccounts() {
        return customer.getAccounts();
    }

    public String depositTo(String accountNumber, double amount) {
        Account a = bank.findAccount(accountNumber);
        if (a == null) return "Account not found";
        try {
            a.deposit(amount);
            return "Deposited " + amount;
        } catch (Exception ex) {
            return "Deposit failed: " + ex.getMessage();
        }
    }

    public String withdrawFrom(String accountNumber, double amount) {
        Account a = bank.findAccount(accountNumber);
        if (a == null) return "Account not found";
        if (!(a instanceof com.bankingsystem.Withdrawable)) return "Account not withdrawable";
        boolean ok = a.withdraw(amount);
        return ok ? "Withdrawn " + amount : "Withdraw failed";
    }

    public void applyInterest() {
        // delegate to bank which applies interest globally
        bank.applyInterest();
    }

    /**
     * Customer requests an account open. This will create a pending request that staff approves.
     */
    public String openAccount(String type, double initialDeposit) {
        EmploymentInfo info = null;
        if ("cheque".equalsIgnoreCase(type) && customer instanceof IndividualCustomer) {
            // use employer info if present
            IndividualCustomer ic = (IndividualCustomer) customer;
            if (ic.getEmployerName() != null) info = new EmploymentInfo(ic.getEmployerName(), "unknown", "employee");
        }
        AccountRequest req = bank.requestAccountOpen(customer, type, initialDeposit, info);
        return "Account request submitted (" + req.getId() + ") and pending staff approval.";
    }

    /**
     * Overload to accept explicit employment information (used for cheque account applications)
     */
    public String openAccount(String type, double initialDeposit, EmploymentInfo employmentInfo) {
        AccountRequest req = bank.requestAccountOpen(customer, type, initialDeposit, employmentInfo);
        return "Account request submitted (" + req.getId() + ") and pending staff approval.";
    }
}
