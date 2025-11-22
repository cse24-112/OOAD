package com.bankingsystem.mvc.controller;

import com.bankingsystem.mvc.dao.TransactionDAO;
import com.bankingsystem.mvc.model.Transaction;

public class DepositController {
    private final TransactionDAO transactionDAO = new TransactionDAO();

    public DepositController() {}

    public String performDeposit(String accountId, double amount) {
        try {
            Transaction t = new Transaction(accountId, "DEPOSIT", amount);
            transactionDAO.addTransaction(t);
            return "Deposit recorded";
        } catch (Exception ex) {
            ex.printStackTrace();
            return "Deposit failed: " + ex.getMessage();
        }
    }
}
