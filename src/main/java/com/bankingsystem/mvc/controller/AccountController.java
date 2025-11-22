package com.bankingsystem.mvc.controller;

import com.bankingsystem.mvc.dao.AccountDAO;
import com.bankingsystem.mvc.dao.TransactionDAO;
import com.bankingsystem.mvc.model.Account;
import com.bankingsystem.mvc.model.Transaction;

import java.util.List;

public class AccountController {
    private final AccountDAO accountDAO = new AccountDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();

    public AccountController() {}

    public List<Account> listAccountsByCustomer(String customerId) {
        try {
            return accountDAO.findByCustomerId(customerId);
        } catch (Exception ex) {
            ex.printStackTrace();
            return java.util.Collections.emptyList();
        }
    }

    public String deposit(String accountNumber, double amount) {
        try {
            Account a = accountDAO.findByAccountNumber(accountNumber);
            if (a == null) return "Account not found";
            a.deposit(amount);
            accountDAO.updateBalance(a.getAccountId(), a.getBalance());
            Transaction t = new Transaction(a.getAccountId(), "DEPOSIT", amount);
            transactionDAO.addTransaction(t);
            return "Deposit successful";
        } catch (Exception ex) {
            ex.printStackTrace();
            return "Deposit failed: " + ex.getMessage();
        }
    }

    public String withdraw(String accountNumber, double amount) {
        try {
            Account a = accountDAO.findByAccountNumber(accountNumber);
            if (a == null) return "Account not found";
            try {
                boolean ok = a.withdraw(amount);
                if (!ok) return "Withdraw failed: insufficient funds";
            } catch (UnsupportedOperationException uex) {
                return "Withdraw failed: " + uex.getMessage();
            }
            accountDAO.updateBalance(a.getAccountId(), a.getBalance());
            Transaction t = new Transaction(a.getAccountId(), "WITHDRAW", amount);
            transactionDAO.addTransaction(t);
            return "Withdraw successful";
        } catch (Exception ex) {
            ex.printStackTrace();
            return "Withdraw failed: " + ex.getMessage();
        }
    }
}
