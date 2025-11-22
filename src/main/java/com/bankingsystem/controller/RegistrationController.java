package com.bankingsystem.controller;

import com.bankingsystem.Bank;
import com.bankingsystem.CustomerRegistrationRequest;
import com.bankingsystem.view.LoginView;
import com.bankingsystem.view.RegistrationView;
import java.util.Arrays;
import javafx.stage.Stage;

public class RegistrationController {
    private final Bank bank;
    private final Stage stage;

    public RegistrationController(Bank bank, Stage stage) {
        this.bank = bank;
        this.stage = stage;
    }

    public Bank getBank() { return bank; }
    public Stage getStage() { return stage; }

    public String registerIndividual(String firstName, String lastName, String nationalId, String username, String password) {
        if (firstName == null || firstName.isBlank()) return "First name required";
        if (lastName == null || lastName.isBlank()) return "Last name required";
        if (nationalId == null || !nationalId.matches("\\d{9}")) return "National ID must be 9 digits (Botswana)";
        // We no longer accept credentials at registration time; staff will set username/password on approval.
        // create a registration request for staff to approve (supply all constructor params — company fields are empty)
        CustomerRegistrationRequest req = new CustomerRegistrationRequest(
            "", "individual", firstName, lastName, nationalId,
            null, null, null, null, null, null, null,
            java.util.Collections.emptyList(), java.util.Collections.emptyList());
        CustomerRegistrationRequest submitted = bank.submitCustomerRegistration(req);
        // navigate back to login view
        LoginView lv = new LoginView(new LoginController(bank, stage));
        stage.getScene().setRoot(lv);
        return "Registration submitted (" + submitted.getId() + "). A staff member will verify and activate your account.";
    }

    public String registerCompany(String companyName, String registrationNumber, String businessDetails, String directorsCsv, String signatoriesCsv, String industry, String employeesRange, String revenueRange, String yearsInOperation) {
        if (companyName == null || companyName.isBlank()) return "Company name required";
        if (registrationNumber == null || registrationNumber.isBlank()) return "Registration number required";
        // parse csv lists
        java.util.List<String> directors = directorsCsv == null ? java.util.List.of() : Arrays.asList(directorsCsv.split(";"));
        java.util.List<String> signatories = signatoriesCsv == null ? java.util.List.of() : Arrays.asList(signatoriesCsv.split(";"));
        CustomerRegistrationRequest req = new CustomerRegistrationRequest("", "company", null, null, null, companyName, registrationNumber, businessDetails, industry, employeesRange, revenueRange, yearsInOperation, directors, signatories);
        CustomerRegistrationRequest submitted = bank.submitCustomerRegistration(req);
        LoginView lv = new LoginView(new LoginController(bank, stage));
        stage.getScene().setRoot(lv);
        return "Company registration submitted (" + submitted.getId() + ") — a staff member will review and activate the account.";
    }
}
