package com.bankingsystem.gui;

import com.bankingsystem.*;
import com.bankingsystem.mvc.view.LoginView;
import com.bankingsystem.mvc.controller.LoginController;
import com.bankingsystem.mvc.view.StaffDashboardView;
import com.bankingsystem.mvc.view.CustomerDashboardView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main Application entry point for the JavaFX Banking System
 * Initializes demo data and manages the UI workflow
 */
public class MainApp extends Application {
    private static Bank bank;

    @Override
    public void start(Stage primaryStage) {
        // Initialize or load bank data
        initializeBank();
        
        // Create login view with callback to route to appropriate dashboard.
        // Use a scene holder to avoid referencing the view before it's initialized.
        final Scene[] loginSceneHolder = new Scene[1];
        LoginController loginController = new LoginController();

        java.util.function.Consumer<com.bankingsystem.mvc.controller.LoginResult> onSuccess = loginResult -> {
            if (loginResult.getRole() == com.bankingsystem.mvc.controller.LoginResult.Role.STAFF) {
                // Staff dashboard for account approval - pass primaryStage and loginSceneHolder for logout
                StaffDashboardView staffDashboard = new StaffDashboardView(bank, primaryStage, loginSceneHolder[0]);
                Scene staffScene = new Scene(staffDashboard, 1000, 600);
                primaryStage.setScene(staffScene);
                primaryStage.setTitle("Banking System - Staff Dashboard");
            } else if (loginResult.getRole() == com.bankingsystem.mvc.controller.LoginResult.Role.CUSTOMER 
                    || loginResult.getRole() == com.bankingsystem.mvc.controller.LoginResult.Role.COMPANY) {
                // Customer dashboard - pass primaryStage and loginSceneHolder for logout
                com.bankingsystem.Customer coreBankCustomer = loginResult.getCoreBankCustomer();
                if (coreBankCustomer != null) {
                    CustomerDashboardView customerDashboard = new CustomerDashboardView(coreBankCustomer, primaryStage, loginSceneHolder[0]);
                    Scene customerScene = new Scene(customerDashboard, 1000, 600);
                    primaryStage.setScene(customerScene);
                    primaryStage.setTitle("Banking System - Customer Dashboard");
                } else {
                    // Fallback: go back to login scene (use holder)
                    if (loginSceneHolder[0] != null) {
                        primaryStage.setScene(loginSceneHolder[0]);
                    }
                }
            }
        };

        Runnable onRegister = () -> {
            // Show registration window
            Stage regStage = new Stage();
            com.bankingsystem.mvc.view.NewAccountRegistrationView regView = 
                new com.bankingsystem.mvc.view.NewAccountRegistrationView(bank, regStage);
            Scene regScene = new Scene((javafx.scene.Parent)regView, 900, 700);
            regStage.setScene(regScene);
            regStage.setTitle("Create New Account");
            regStage.show();
        };

        LoginView loginView = new LoginView(loginController, onSuccess, onRegister);
        loginSceneHolder[0] = new Scene(loginView, 700, 600);
        primaryStage.setTitle("Banking System - Login");
        primaryStage.setScene(loginSceneHolder[0]);
        primaryStage.show();
        
        // Save on exit
        primaryStage.setOnCloseRequest(e -> saveBank());
    }

    private void initializeBank() {
        // Try to load persisted bank data
        try {
            java.io.File dataFile = new java.io.File(System.getProperty("user.home"), "banking-data.json");
            if (dataFile.exists()) {
                com.bankingsystem.persistence.BankStorage storage = new com.bankingsystem.persistence.BankStorage(dataFile);
                Bank loaded = storage.load();
                if (loaded != null) {
                    bank = loaded;
                    System.out.println("Loaded bank data from file: " + dataFile.getAbsolutePath());
                    return;
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to load persisted bank data: " + e.getMessage());
        }
        
        // Create new bank with demo data
        bank = new Bank("Botswana Banking System", "001");
        seedDemoData();
    }

    private void seedDemoData() {
        // Demo Customer 1: Alice Smith (APPROVED and PENDING accounts)
        IndividualCustomer alice = new IndividualCustomer("CUST001", "Alice", "Smith", "123456789101");
        alice.setUsername("alice");
        alice.setPassword("password");
        alice.setPin("1234");
        alice.setEmail("alice@example.com");
        alice.setPhone("+267-71-234567");
        alice.setEmployer("Acme Corporation", "Plot 123, Gaborone", "Permanent");
        bank.registerCustomer(alice);
        
        // Alice's accounts - 2 APPROVED, 1 PENDING
        Account savingsAcc1 = alice.openAccount("savings", 2500.0);  // Approved account
        AccountApprovalService.approveAccount(savingsAcc1, "system");
        
        Account investmentAcc1 = alice.openAccount("investment", 5000.0);  // Approved account
        AccountApprovalService.approveAccount(investmentAcc1, "system");
        
        alice.openAccount("cheque", 1000.0);  // PENDING - chequeAcc1
        
        System.out.println("Alice: " + alice.getAccounts().stream()
            .filter(a -> a.getStatus() == AccountStatus.APPROVED).count() + " approved, " +
            alice.getAccounts().stream()
            .filter(a -> a.getStatus() == AccountStatus.PENDING).count() + " pending");
        
        // Demo Customer 2: Bob Johnson (Mixed accounts)
        IndividualCustomer bob = new IndividualCustomer("CUST002", "Bob", "Johnson", "234567890112");
        bob.setUsername("bob");
        bob.setPassword("password");
        bob.setPin("5678");
        bob.setEmail("bob@example.com");
        bob.setPhone("+267-71-345678");
        bob.setEmployer("Tech Solutions Ltd", "Plot 456, Francistown", "Contract");
        bank.registerCustomer(bob);
        
        // Bob's accounts - 1 APPROVED, 1 PENDING
        Account savingsAcc2 = bob.openAccount("savings", 1500.0);  // Approved
        AccountApprovalService.approveAccount(savingsAcc2, "system");
        
        bob.openAccount("investment", 2000.0);  // PENDING - investmentAcc2
        
        System.out.println("Bob: " + bob.getAccounts().stream()
            .filter(a -> a.getStatus() == AccountStatus.APPROVED).count() + " approved, " +
            bob.getAccounts().stream()
            .filter(a -> a.getStatus() == AccountStatus.PENDING).count() + " pending");
        
        // Demo Customer 3: Charlie Williams (All PENDING)
        IndividualCustomer charlie = new IndividualCustomer("CUST003", "Charlie", "Williams", "345678901123");
        charlie.setUsername("charlie");
        charlie.setPassword("password");
        charlie.setPin("9999");
        charlie.setEmail("charlie@example.com");
        charlie.setPhone("+267-71-456789");
        charlie.setEmployer("Education Department", "Plot 789, Molepolole", "Government");
        bank.registerCustomer(charlie);
        
        // Charlie's accounts - ALL PENDING
        charlie.openAccount("savings", 600.0);  // PENDING - savingsAcc3
        charlie.openAccount("investment", 800.0);  // PENDING (also insufficient balance) - investmentAcc3
        charlie.openAccount("cheque", 750.0);  // PENDING - chequeAcc3
        
        System.out.println("Charlie: " + charlie.getAccounts().stream()
            .filter(a -> a.getStatus() == AccountStatus.APPROVED).count() + " approved, " +
            charlie.getAccounts().stream()
            .filter(a -> a.getStatus() == AccountStatus.PENDING).count() + " pending");
        
        // Demo Customer 4: Diana Evans (Individual, New)
        IndividualCustomer diana = new IndividualCustomer("CUST005", "Diana", "Evans", "456789012345");
        diana.setUsername("diana");
        diana.setPassword("password");
        diana.setPin("1111");
        diana.setEmail("diana@example.com");
        diana.setEmployer("Finance Consultants", "Plot 321, Gaborone", "Permanent");
        bank.registerCustomer(diana);
        
        // Diana's account - APPROVED
        Account savingsAcc4 = diana.openAccount("savings", 3200.0);
        AccountApprovalService.approveAccount(savingsAcc4, "system");
        
        System.out.println("Diana: " + diana.getAccounts().stream()
            .filter(a -> a.getStatus() == AccountStatus.APPROVED).count() + " approved, " +
            diana.getAccounts().stream()
            .filter(a -> a.getStatus() == AccountStatus.PENDING).count() + " pending");
        
        // Demo Customer 5: Company - Botswana Tech Ltd (PENDING Company Account with Details)
        CompanyCustomer techCompany = new CompanyCustomer("COMP001", "Botswana Tech Ltd", "REG-2024-001");
        techCompany.setNationalID("techcompany"); // Use national ID field for company username
        techCompany.setEmail("info@botswana-tech.com");
        techCompany.setPhone("+267-31-123456");
        
        // Create and set company registration details for review
        CompanyRegistration techReg = new CompanyRegistration(
            "Botswana Tech Ltd",
            "REG-2024-001",
            "TAX-2024-0001",
            "Technology",
            45,
            "Mr. John Smith",
            "john.smith@botswana-tech.com",
            "+267-71-123456",
            "Plot 500, Tech Park, Gaborone",
            "Software Development"
        );
        techCompany.setCompanyRegistration(techReg);
        bank.registerCustomer(techCompany);
        
        // Tech company accounts - 1 APPROVED, 1 PENDING
        Account companySavings1 = bank.openAccount(techCompany, "savings", 8000.0);  // APPROVED
        AccountApprovalService.approveAccount(companySavings1, "system");
        
        bank.openAccount(techCompany, "investment", 15000.0);  // PENDING - companyInvestment1
        
        System.out.println("Botswana Tech Ltd: " + techCompany.getAccounts().stream()
            .filter(a -> a.getStatus() == AccountStatus.APPROVED).count() + " approved, " +
            techCompany.getAccounts().stream()
            .filter(a -> a.getStatus() == AccountStatus.PENDING).count() + " pending");
        
        // Demo Customer 6: Another Company - Mining Services Corp (PENDING)
        CompanyCustomer miningCompany = new CompanyCustomer("COMP002", "Mining Services Corp", "REG-2024-002");
        miningCompany.setNationalID("miningcorp"); // Use national ID field for company username
        miningCompany.setEmail("contact@mining-services.com");
        miningCompany.setPhone("+267-31-234567");
        
        CompanyRegistration miningReg = new CompanyRegistration(
            "Mining Services Corp",
            "REG-2024-002",
            "TAX-2024-0002",
            "Mining & Resources",
            120,
            "Ms. Sarah Johnson",
            "sarah.j@mining-services.com",
            "+267-71-234567",
            "Plot 100, Industrial Area, Palapye",
            "Mining Support Services"
        );
        miningCompany.setCompanyRegistration(miningReg);
        bank.registerCustomer(miningCompany);
        
        // Mining company - ALL PENDING
        bank.openAccount(miningCompany, "savings", 5000.0);  // PENDING - companySavings2
        bank.openAccount(miningCompany, "investment", 20000.0);  // PENDING - companyInvestment2
        
        System.out.println("Mining Services Corp: " + miningCompany.getAccounts().stream()
            .filter(a -> a.getStatus() == AccountStatus.APPROVED).count() + " approved, " +
            miningCompany.getAccounts().stream()
            .filter(a -> a.getStatus() == AccountStatus.PENDING).count() + " pending");
        
        // Staff already created in Bank constructor with default credentials (staff1/adminpass)
        
        System.out.println("\n=== DEMO DATA INITIALIZED ===");
        System.out.println("Demo Customers Created:");
        System.out.println("1. Alice Smith (alice / 1234) - 2 approved, 1 pending");
        System.out.println("2. Bob Johnson (bob / 5678) - 1 approved, 1 pending");
        System.out.println("3. Charlie Williams (charlie / 9999) - 0 approved, 3 pending");
        System.out.println("4. Diana Evans (diana / 1111) - 1 approved");
        System.out.println("5. Botswana Tech Ltd (techcompany/password) - 1 approved, 1 pending");
        System.out.println("6. Mining Services Corp (miningcorp/password) - 0 approved, 2 pending");
        System.out.println("Staff: staff1 / adminpass");
        System.out.println("\nTotal Approved Accounts: " + bank.getAccounts().stream()
            .filter(a -> a.getStatus() == AccountStatus.APPROVED).count());
        System.out.println("Total Pending Accounts: " + bank.getAccounts().stream()
            .filter(a -> a.getStatus() == AccountStatus.PENDING).count());
        System.out.println("============================\n");
    }

    private void saveBank() {
        try {
            java.io.File dataFile = new java.io.File(System.getProperty("user.home"), "banking-data.json");
            com.bankingsystem.persistence.BankStorage storage = new com.bankingsystem.persistence.BankStorage(dataFile);
            storage.save(bank);
            System.out.println("Bank data saved to: " + dataFile.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("Failed to save bank data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Bank getBank() {
        return bank;
    }

    public static void main(String[] args) {
        launch(args);
    }
}

