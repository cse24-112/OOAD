package com.bankingsystem.controller;

import com.bankingsystem.Bank;
import com.bankingsystem.Customer;
import com.bankingsystem.view.AccountView;
import javafx.stage.Stage;

/**
 * Controller for login boundary. Mediates between LoginView and the model.
 */
public class LoginController {
    private final Bank bank;
    private final Stage stage;

    public LoginController(Bank bank, Stage stage) {
        this.bank = bank;
        this.stage = stage;
    }

    /**
     * Attempt to login using a customer ID. If found, navigates to AccountView.
     * Returns a user-facing message (not business logic).
     */
    public String login(String username, String password) {
        if (username == null || username.isBlank()) return "Username required";
        // check staff first
        if (bank.authenticateStaff(username, password)) {
            // navigate to admin
            com.bankingsystem.controller.AdminController ac = new com.bankingsystem.controller.AdminController(bank, stage);
            com.bankingsystem.view.AdminView av = new com.bankingsystem.view.AdminView(ac);
            stage.getScene().setRoot(av);
            stage.setTitle("Banking System - Admin");
            return "Staff logged in";
        }
        // find customer who authenticates
        Customer found = bank.getCustomers().stream().filter(c -> c.authenticate(username, password)).findFirst().orElse(null);
        if (found == null) return "Invalid username or password";
        // create account controller + view and navigate
        AccountController ac = new AccountController(bank, found);
        AccountView view = new AccountView(ac);
        stage.getScene().setRoot(view);
        stage.setTitle("Banking System - " + found);
        return "Welcome " + found;
    }

    public void showRegistration() {
        RegistrationController rc = new RegistrationController(bank, stage);
        com.bankingsystem.view.RegistrationView rv = new com.bankingsystem.view.RegistrationView(rc);
        stage.getScene().setRoot(rv);
    }
}
