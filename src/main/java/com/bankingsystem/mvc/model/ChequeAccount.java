package com.bankingsystem.mvc.model;

public class ChequeAccount extends Account {
    public ChequeAccount(String customerId, double balance, String branch, String employerName, String employerAddress) {
        super(customerId, "CHEQUE", balance, branch);
        this.employerName = employerName;
        this.employerAddress = employerAddress;
    }

    @Override
    public double calculateInterest() {
        return 0.0; // no interest by default
    }
}
