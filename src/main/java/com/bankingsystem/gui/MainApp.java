package com.bankingsystem.gui;

import com.bankingsystem.Bank;
import com.bankingsystem.CompanyCustomer;
import com.bankingsystem.IndividualCustomer;
import com.bankingsystem.mvc.view.LoginView;
import com.bankingsystem.mvc.controller.LoginController;
import com.bankingsystem.mvc.view.StaffDashboardView;
import com.bankingsystem.mvc.view.AccountView;
import com.bankingsystem.mvc.view.DepositView;
import com.bankingsystem.mvc.controller.AccountController;
import com.bankingsystem.mvc.controller.StaffController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    private static Bank bank;

    @Override
    public void start(Stage primaryStage) {
        // try to load persisted bank if available first (faster when data exists)
        try {
            java.io.File f = new java.io.File(System.getProperty("user.home"), "banking-data.json");
            com.bankingsystem.persistence.BankStorage storage = new com.bankingsystem.persistence.BankStorage(f);
            Bank loaded = storage.load();
            if (loaded != null) {
                bank = loaded;
            } else {
                // create a bank and seed some customers for demo
                bank = new Bank("Banking System", "001");
                IndividualCustomer alice = new IndividualCustomer("CUST001", "Alice", "Smith", "123456789");
                alice.setEmployer("Acme Corp", "1 Main St", "FullTime");
                // set demo credentials
                alice.setUsername("alice");
                alice.setPassword("password");
                bank.registerCustomer(alice);
                // seed some accounts for demo
                bank.openAccount(alice, "savings", 1000.0);
                bank.openAccount(alice, "investment", 1000.0);
                bank.openAccount(alice, "cheque", 200.0);
                CompanyCustomer acme = new CompanyCustomer("CUST002", "Acme Corp", "REG-987");
                bank.registerCustomer(acme);
                // seed an account for the company
                bank.openAccount(acme, "savings", 5000.0);
            }
        } catch (com.google.gson.JsonSyntaxException jse) {
            // corrupted JSON file — remove it and continue with seeded data
            try {
                java.io.File f = new java.io.File(System.getProperty("user.home"), "banking-data.json");
                if (f.exists()) {
                    f.delete();
                    System.err.println("Corrupted banking-data.json removed. Continuing with seeded demo data.");
                }
            } catch (Exception ex2) {
                ex2.printStackTrace();
            }
            // create fresh bank after removing bad file
            bank = new Bank("Banking System", "001");
        } catch (Exception ex) {
            // other load errors — print and continue with seeded data
            ex.printStackTrace();
            bank = new Bank("Banking System", "001");
        }

        // Seed some demo registration requests if none exist (helps staff testing)
        try {
            if (bank.getCustomerRequests().isEmpty()) {
                com.bankingsystem.CustomerRegistrationRequest r1 = new com.bankingsystem.CustomerRegistrationRequest(
                    "", "individual", "Bob", "Jones", "987654321",
                    null, null, null, null, null, null, null,
                    java.util.Collections.emptyList(), java.util.Collections.emptyList());
                bank.submitCustomerRegistration(r1);

                com.bankingsystem.CustomerRegistrationRequest r2 = new com.bankingsystem.CustomerRegistrationRequest(
                        "", "company", null, null, null,
                        "Innotech Ltd", "REG-555", "We build software solutions for SMEs.",
                        "Technology", "11-50", "250k-1M", "4-10",
                        java.util.List.of("Alice - 123456789"), java.util.List.of("Alice"));
                bank.submitCustomerRegistration(r2);
            }
        } catch (Exception ex) {
            // ignore seeding errors
            ex.printStackTrace();
        }

        // Replace legacy LoginView with MVC LoginView (new)
        LoginController mvcLoginController = new LoginController();
        LoginView loginView = new LoginView(mvcLoginController, lr -> {
            // route based on role
            if (lr.getRole() == com.bankingsystem.mvc.controller.LoginResult.Role.STAFF) {
                // staff dashboard
                StaffController sc = new StaffController();
                StaffDashboardView sdv = new StaffDashboardView(sc);
                primaryStage.getScene().setRoot(sdv);
                primaryStage.setTitle("Banking System - Staff");
            } else if (lr.getRole() == com.bankingsystem.mvc.controller.LoginResult.Role.CUSTOMER || lr.getRole() == com.bankingsystem.mvc.controller.LoginResult.Role.COMPANY) {
                // customer dashboard - simple account list + deposit
                AccountController ac = new AccountController();
                java.util.List<com.bankingsystem.mvc.model.Account> accounts = ac.listAccountsByCustomer(lr.getCustomer().getCustomerId());
                // show first account in AccountView example
                if (!accounts.isEmpty()) {
                    AccountView av = new AccountView(accounts.get(0));
                    primaryStage.getScene().setRoot(av);
                    primaryStage.setTitle("Banking System - Account");
                } else {
                    // show deposit view but empty
                    DepositView dv = new DepositView(ac);
                    primaryStage.getScene().setRoot(dv);
                    primaryStage.setTitle("Banking System - Deposit");
                }
            }
        });

        primaryStage.setTitle("Banking System - Login");
        Scene scene = new Scene(loginView, 640, 420);
        // load CSS (guard if resource missing)
        java.net.URL cssUrl = getClass().getResource("/style.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        } else {
            System.err.println("Warning: style.css not found on classpath; continuing without stylesheet.");
        }
        primaryStage.setScene(scene);
        primaryStage.show();

        // save on exit
        primaryStage.setOnCloseRequest(e -> {
            try {
                java.io.File f = new java.io.File(System.getProperty("user.home"), "banking-data.json");
                com.bankingsystem.persistence.BankStorage storage = new com.bankingsystem.persistence.BankStorage(f);
                storage.save(bank);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    public static Bank getBank() {
        return bank;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
