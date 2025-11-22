package com.bankingsystem.view;

import com.bankingsystem.Customer;
import com.bankingsystem.Account;
import com.bankingsystem.controller.AccountController;
import com.bankingsystem.controller.AdminController;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Dashboard view that displays different UI based on user role.
 * For Bank Staff: Shows admin functions
 * For Customers: Shows account overview and quick actions
 */
public class DashboardView extends BorderPane {
    private final Object controller; // Can be AccountController or AdminController
    private final boolean isStaff;

    public DashboardView(AccountController accountController) {
        this.controller = accountController;
        this.isStaff = false;
        buildCustomerDashboard();
    }

    public DashboardView(AdminController adminController) {
        this.controller = adminController;
        this.isStaff = true;
        buildStaffDashboard();
    }

    private void buildCustomerDashboard() {
        AccountController ac = (AccountController) controller;
        setPadding(new Insets(20));
        
        // Title
        Label title = new Label("Customer Dashboard");
        title.setFont(Font.font("System", FontWeight.BOLD, 24));
        title.getStyleClass().add("title");
        
        // Account summary section
        VBox accountSummary = new VBox(10);
        accountSummary.setPadding(new Insets(15));
        accountSummary.getStyleClass().add("panel");
        
        Label summaryTitle = new Label("Your Accounts");
        summaryTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        
        ListView<String> accountsList = new ListView<>();
        for (Account acc : ac.listAccounts()) {
            String accountInfo = String.format("%s - %s: BWP %.2f", 
                acc.getAccountNumber(), 
                acc.getClass().getSimpleName().replace("Account", ""),
                acc.getBalance());
            accountsList.getItems().add(accountInfo);
        }
        
        accountSummary.getChildren().addAll(summaryTitle, accountsList);
        
        // Quick actions section
        VBox quickActions = new VBox(10);
        quickActions.setPadding(new Insets(15));
        quickActions.getStyleClass().add("panel");
        
        Label actionsTitle = new Label("Quick Actions");
        actionsTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        
        Button viewBalanceBtn = new Button("View Balance");
        Button depositBtn = new Button("Deposit");
        Button withdrawBtn = new Button("Withdraw");
        Button transferBtn = new Button("Transfer");
        Button transactionHistoryBtn = new Button("Transaction History");
        Button openAccountBtn = new Button("Open New Account");
        
        viewBalanceBtn.setOnAction(e -> navigateToViewBalance());
        depositBtn.setOnAction(e -> navigateToDeposit());
        withdrawBtn.setOnAction(e -> navigateToWithdraw());
        transferBtn.setOnAction(e -> navigateToTransfer());
        transactionHistoryBtn.setOnAction(e -> navigateToTransactionHistory());
        openAccountBtn.setOnAction(e -> navigateToOpenAccount());
        
        // Style buttons
        viewBalanceBtn.getStyleClass().add("action-button");
        depositBtn.getStyleClass().add("action-button");
        withdrawBtn.getStyleClass().add("action-button");
        transferBtn.getStyleClass().add("action-button");
        transactionHistoryBtn.getStyleClass().add("action-button");
        openAccountBtn.getStyleClass().add("action-button");
        
        quickActions.getChildren().addAll(actionsTitle, viewBalanceBtn, depositBtn, 
                                         withdrawBtn, transferBtn, transactionHistoryBtn, openAccountBtn);
        
        // Layout
        HBox mainContent = new HBox(20);
        mainContent.getChildren().addAll(accountSummary, quickActions);
        
        setTop(title);
        setCenter(mainContent);
    }

    private void buildStaffDashboard() {
        AdminController ac = (AdminController) controller;
        setPadding(new Insets(20));
        
        // Title
        Label title = new Label("Bank Staff Dashboard");
        title.setFont(Font.font("System", FontWeight.BOLD, 24));
        title.getStyleClass().add("title");
        
        // Staff actions section
        VBox staffActions = new VBox(10);
        staffActions.setPadding(new Insets(15));
        staffActions.getStyleClass().add("panel");
        
        Label actionsTitle = new Label("Staff Functions");
        actionsTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        
        Button registerCustomerBtn = new Button("Register New Customer");
        Button openAccountBtn = new Button("Open Account for Customer");
        Button applyInterestBtn = new Button("Apply Monthly Interest");
        Button viewCustomerAccountsBtn = new Button("View Customer Accounts");
        Button pendingRequestsBtn = new Button("Pending Account Requests");
        
        registerCustomerBtn.setOnAction(e -> navigateToRegisterCustomer());
        openAccountBtn.setOnAction(e -> navigateToOpenAccount());
        applyInterestBtn.setOnAction(e -> onApplyInterest());
        viewCustomerAccountsBtn.setOnAction(e -> navigateToViewCustomerAccounts());
        pendingRequestsBtn.setOnAction(e -> navigateToPendingRequests());
        
        // Style buttons
        registerCustomerBtn.getStyleClass().add("action-button");
        openAccountBtn.getStyleClass().add("action-button");
        applyInterestBtn.getStyleClass().add("action-button");
        viewCustomerAccountsBtn.getStyleClass().add("action-button");
        pendingRequestsBtn.getStyleClass().add("action-button");
        
        staffActions.getChildren().addAll(actionsTitle, registerCustomerBtn, openAccountBtn,
                                         applyInterestBtn, viewCustomerAccountsBtn, pendingRequestsBtn);
        
        setTop(title);
        setCenter(staffActions);
    }

    // Navigation methods (these would be handled by a navigation controller in a real app)
    private void navigateToViewBalance() {
        // Would navigate to ViewBalanceView
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Navigation");
        alert.setContentText("Navigate to View Balance");
        alert.show();
    }

    private void navigateToDeposit() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Navigation");
        alert.setContentText("Navigate to Deposit");
        alert.show();
    }

    private void navigateToWithdraw() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Navigation");
        alert.setContentText("Navigate to Withdraw");
        alert.show();
    }

    private void navigateToTransfer() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Navigation");
        alert.setContentText("Navigate to Transfer");
        alert.show();
    }

    private void navigateToTransactionHistory() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Navigation");
        alert.setContentText("Navigate to Transaction History");
        alert.show();
    }

    private void navigateToOpenAccount() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Navigation");
        alert.setContentText("Navigate to Open Account");
        alert.show();
    }

    private void navigateToRegisterCustomer() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Navigation");
        alert.setContentText("Navigate to Register Customer");
        alert.show();
    }

    private void navigateToViewCustomerAccounts() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Navigation");
        alert.setContentText("Navigate to View Customer Accounts");
        alert.show();
    }

    private void navigateToPendingRequests() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Navigation");
        alert.setContentText("Navigate to Pending Requests");
        alert.show();
    }

    private void onApplyInterest() {
        if (controller instanceof AdminController) {
            AdminController ac = (AdminController) controller;
            // Apply interest logic would be in AdminController
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Interest Applied");
            alert.setContentText("Monthly interest has been applied to all eligible accounts.");
            alert.show();
        }
    }
}

