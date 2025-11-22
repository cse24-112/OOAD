package com.bankingsystem.controller;

import com.bankingsystem.AccountRequest;
import com.bankingsystem.CustomerRegistrationRequest;
import com.bankingsystem.Bank;
import javafx.stage.Stage;

import java.util.List;

public class AdminController {
    private final Bank bank;
    private final Stage stage;

    public AdminController(Bank bank, Stage stage) {
        this.bank = bank;
        this.stage = stage;
    }

    public List<AccountRequest> listRequests() {
        return bank.getRequests();
    }

    public java.util.List<CustomerRegistrationRequest> listCustomerRequests() {
        return bank.getCustomerRequests();
    }

    public java.util.List<CustomerRegistrationRequest> listCustomerHistory() {
        return bank.getCustomerRequests().stream().filter(r -> r.getStatus() != CustomerRegistrationRequest.Status.PENDING).toList();
    }

    public String approve(String requestId) {
        boolean ok = bank.approveRequest(requestId, "staff1");
        return ok ? "Request approved" : "Approve failed";
    }

    public String approveRequest(String requestId, String staffUser) {
        boolean ok = bank.approveRequest(requestId, staffUser);
        return ok ? "Request approved" : "Approve failed";
    }

    public String approveCustomer(String requestId) {
        boolean ok = bank.approveCustomerRegistration(requestId, "staff1", null, null, "Approved via admin UI");
        return ok ? "Customer registration approved" : "Approve failed";
    }

    public String approveCustomer(String requestId, String username, String password, String notes) {
        boolean ok = bank.approveCustomerRegistration(requestId, "staff1", username, password, notes);
        return ok ? "Customer registration approved" : "Approve failed";
    }

    public String rejectCustomer(String requestId, String reason) {
        boolean ok = bank.rejectCustomerRegistration(requestId, "staff1", reason);
        return ok ? "Customer registration rejected" : "Reject failed";
    }

    public String rejectRequest(String requestId, String reason) {
        boolean ok = bank.rejectAccountRequest(requestId, "staff1", reason);
        return ok ? "Account request rejected" : "Reject failed";
    }

    public Bank getBank() { return bank; }
    public Stage getStage() { return stage; }
}
