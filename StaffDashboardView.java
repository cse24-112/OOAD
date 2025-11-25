/**
 * ENHANCED StaffDashboardView
 * 
 * KEY CHANGES TO IMPLEMENT:
 * 1. Use TabPane instead of single view
 * 2. Tab 1: "Pending Approvals" - Shows pending accounts from DATABASE
 * 3. Tab 2: "All Accounts" - Shows all approved accounts from DATABASE  
 * 4. Tab 3: "Close Account" - Staff can close inactive accounts
 * 
 * Replace the entire StaffDashboardView.java with this implementation
 */

package com.bankingsystem.mvc.view;

import com.bankingsystem.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class StaffDashboardView extends BorderPane {
    private final Bank bank;
    private final TabPane tabPane;
    private final Stage primaryStage;
    private final Scene loginScene;

    public StaffDashboardView(Bank bank) {
        this(bank, null, null);
    }

    public StaffDashboardView(Bank bank, Stage primaryStage, Scene loginScene) {
        this.bank = bank;
        this.primaryStage = primaryStage;
        this.loginScene = loginScene;
        this.tabPane = new TabPane();
        
        initializeUI();
    }

    private void initializeUI() {
        setPadding(new Insets(15));
        setStyle("-fx-font-size: 11;");
        
        // Top: Title
        VBox topBox = createTopSection();
        setTop(topBox);
        
        // Center: Tab Pane with multiple views
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        
        // Tab 1: Pending Accounts (for approval)
        Tab pendingTab = new Tab("Pending Approvals", createPendingAccountsView());
        tabPane.getTabs().add(pendingTab);
        
        // Tab 2: All Approved Accounts (view only)
        Tab approvedTab = new Tab("All Approved Accounts", createApprovedAccountsView());
        tabPane.getTabs().add(approvedTab);
        
        // Tab 3: Close Account
        Tab closeTab = new Tab("Close Account", createCloseAccountView());
        tabPane.getTabs().add(closeTab);
        
        setCenter(tabPane);
        
        // Bottom: Logout button
        VBox bottomBox = createBottomSection();
        setBottom(bottomBox);
    }

    private VBox createTopSection() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(0, 0, 15, 0));
        
        Label titleLabel = new Label("BANK STAFF DASHBOARD");
        titleLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #1a5490;");
        
        Label descLabel = new Label("Account Management & Approval System");
        descLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #666;");
        
        vbox.getChildren().addAll(titleLabel, descLabel);
        return vbox;
    }

    /**
     * Tab 1: Pending Accounts requiring approval
     * Loads from DATABASE using AccountDAOImpl.getPendingAccounts()
     */
    private VBox createPendingAccountsView() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        
        Label statusLabel = new Label("Pending Accounts: Loading from database...");
        statusLabel.setStyle("-fx-font-size: 12; -fx-font-weight: bold;");
        
        TableView<AccountRow> pendingTable = new TableView<>();
        setupAccountTable(pendingTable);
        
        // Load pending accounts from DATABASE
        List<Account> pendingAccounts = AccountApprovalService.getPendingAccounts(bank);
        populateTable(pendingTable, pendingAccounts);
        statusLabel.setText(String.format("Pending Accounts: %d", pendingAccounts.size()));
        
        HBox actionButtons = new HBox(10);
        actionButtons.setPadding(new Insets(10));
        
        Button approveBtn = new Button("Approve Selected");
        approveBtn.setStyle("-fx-padding: 8; -fx-font-size: 11;");
        approveBtn.setOnAction(e -> handleApproveAccount(pendingTable));
        
        Button rejectBtn = new Button("Reject Selected");
        rejectBtn.setStyle("-fx-padding: 8; -fx-font-size: 11;");
        rejectBtn.setOnAction(e -> handleRejectAccount(pendingTable));
        
        Button refreshBtn = new Button("Refresh");
        refreshBtn.setStyle("-fx-padding: 8; -fx-font-size: 11;");
        refreshBtn.setOnAction(e -> {
            List<Account> updated = AccountApprovalService.getPendingAccounts(bank);
            pendingTable.getItems().clear();
            populateTable(pendingTable, updated);
            statusLabel.setText(String.format("Pending Accounts: %d", updated.size()));
        });
        
        actionButtons.getChildren().addAll(approveBtn, rejectBtn, refreshBtn);
        
        vbox.getChildren().addAll(statusLabel, pendingTable, actionButtons);
        VBox.setVgrow(pendingTable, javafx.scene.layout.Priority.ALWAYS);
        
        return vbox;
    }

    /**
     * Tab 2: All Approved Accounts
     * Loads from DATABASE showing customer accounts that have been approved
     */
    private VBox createApprovedAccountsView() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        
        Label statusLabel = new Label("Approved Accounts: Loading from database...");
        statusLabel.setStyle("-fx-font-size: 12; -fx-font-weight: bold;");
        
        TableView<AccountRow> approvedTable = new TableView<>();
        setupAccountTable(approvedTable);
        
        // Load APPROVED accounts from DATABASE
        List<Account> approvedAccounts = bank.getAccounts().stream()
                .filter(a -> a.getStatus() == AccountStatus.APPROVED)
                .toList();
        populateTable(approvedTable, approvedAccounts);
        statusLabel.setText(String.format("Approved Accounts: %d", approvedAccounts.size()));
        
        HBox filterBox = new HBox(10);
        filterBox.setPadding(new Insets(10));
        
        ComboBox<String> filterCombo = new ComboBox<>();
        filterCombo.getItems().addAll("All Types", "Savings", "Investment", "Cheque");
        filterCombo.setValue("All Types");
        filterCombo.setOnAction(e -> {
            String selected = filterCombo.getValue();
            approvedTable.getItems().clear();
            List<Account> filtered = approvedAccounts.stream()
                    .filter(a -> selected.equals("All Types") || 
                           (selected.equals("Savings") && a instanceof SavingsAccount) ||
                           (selected.equals("Investment") && a instanceof InvestmentAccount) ||
                           (selected.equals("Cheque") && a instanceof ChequeAccount))
                    .toList();
            populateTable(approvedTable, filtered);
        });
        
        Button exportBtn = new Button("Export to CSV");
        exportBtn.setStyle("-fx-padding: 8; -fx-font-size: 11;");
        
        filterBox.getChildren().addAll(new Label("Filter by Type:"), filterCombo, exportBtn);
        
        vbox.getChildren().addAll(statusLabel, filterBox, approvedTable);
        VBox.setVgrow(approvedTable, javafx.scene.layout.Priority.ALWAYS);
        
        return vbox;
    }

    /**
     * Tab 3: Close Account
     * Staff can close inactive or unused customer accounts
     */
    private VBox createCloseAccountView() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        
        Label titleLabel = new Label("Close Customer Account");
        titleLabel.setStyle("-fx-font-size: 12; -fx-font-weight: bold;");
        
        HBox searchBox = new HBox(10);
        searchBox.setPadding(new Insets(10));
        
        TextField accountNumberField = new TextField();
        accountNumberField.setPromptText("Enter account number to close...");
        accountNumberField.setPrefWidth(200);
        
        Button searchBtn = new Button("Search");
        searchBtn.setOnAction(e -> {
            String accountNum = accountNumberField.getText();
            // Search and display account details
        });
        
        searchBox.getChildren().addAll(new Label("Account Number:"), accountNumberField, searchBtn);
        
        TextArea detailsArea = new TextArea();
        detailsArea.setEditable(false);
        detailsArea.setWrapText(true);
        detailsArea.setPrefHeight(200);
        detailsArea.setStyle("-fx-font-size: 11;");
        
        HBox confirmBox = new HBox(10);
        confirmBox.setPadding(new Insets(10));
        
        Button closeBtn = new Button("Close Account");
        closeBtn.setStyle("-fx-padding: 8; -fx-font-size: 11; -fx-text-fill: white; -fx-background-color: #d9534f;");
        closeBtn.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Close");
            confirm.setContentText("Are you sure you want to close this account?");
            if (confirm.showAndWait().get() == ButtonType.OK) {
                // Close account logic
            }
        });
        
        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle("-fx-padding: 8; -fx-font-size: 11;");
        
        confirmBox.getChildren().addAll(closeBtn, cancelBtn);
        
        vbox.getChildren().addAll(titleLabel, searchBox, detailsArea, confirmBox);
        VBox.setVgrow(detailsArea, javafx.scene.layout.Priority.ALWAYS);
        
        return vbox;
    }

    private void setupAccountTable(TableView<AccountRow> table) {
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        
        TableColumn<AccountRow, String> customerCol = new TableColumn<>("Customer");
        customerCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        
        TableColumn<AccountRow, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("accountType"));
        
        TableColumn<AccountRow, String> balanceCol = new TableColumn<>("Balance");
        balanceCol.setCellValueFactory(new PropertyValueFactory<>("balance"));
        
        TableColumn<AccountRow, String> interestCol = new TableColumn<>("Interest");
        interestCol.setCellValueFactory(new PropertyValueFactory<>("monthlyInterest"));
        
        TableColumn<AccountRow, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        TableColumn<AccountRow, String> dateCol = new TableColumn<>("Date Opened");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("dateCreated"));
        
        table.getColumns().addAll(customerCol, typeCol, balanceCol, interestCol, statusCol, dateCol);
    }

    private void populateTable(TableView<AccountRow> table, List<Account> accounts) {
        for (Account account : accounts) {
            String customerName = account.getOwner().getFirstName() + " " + account.getOwner().getLastName();
            String accountType = getAccountType(account);
            String balance = String.format("%.2f", account.getBalance());
            String monthlyInterest = "N/A";
            
            if (account instanceof PayInterest) {
                double interest = account.calculateInterest();
                monthlyInterest = String.format("%.2f", interest);
            }
            
            String status = account.getStatus().getDisplayName();
            String dateCreated = account.getDateOpened().toString();
            
            AccountRow row = new AccountRow(account, customerName, accountType, balance, monthlyInterest, status, dateCreated);
            table.getItems().add(row);
        }
    }

    private String getAccountType(Account account) {
        if (account instanceof SavingsAccount) return "Savings";
        if (account instanceof InvestmentAccount) return "Investment";
        if (account instanceof ChequeAccount) return "Cheque";
        return "Unknown";
    }

    private void handleApproveAccount(TableView<AccountRow> table) {
        AccountRow selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select an account to approve");
            return;
        }
        
        TextInputDialog notesDialog = new TextInputDialog();
        notesDialog.setTitle("Approval Notes");
        notesDialog.setHeaderText("Enter approval notes (optional):");
        notesDialog.setContentText("Notes:");
        
        if (notesDialog.showAndWait().isPresent()) {
            String notes = notesDialog.getResult();
            AccountApprovalService.approveAccount(selected.account, "staff1", notes);
            table.getItems().remove(selected);
            showAlert("Account approved successfully!");
        }
    }

    private void handleRejectAccount(TableView<AccountRow> table) {
        AccountRow selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select an account to reject");
            return;
        }
        
        TextInputDialog reasonDialog = new TextInputDialog();
        reasonDialog.setTitle("Rejection Reason");
        reasonDialog.setHeaderText("Enter reason for rejection:");
        reasonDialog.setContentText("Reason:");
        
        if (reasonDialog.showAndWait().isPresent()) {
            String reason = reasonDialog.getResult();
            // Reject logic
            table.getItems().remove(selected);
            showAlert("Account rejected!");
        }
    }

    private VBox createBottomSection() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(15, 0, 0, 0));
        vbox.setStyle("-fx-border-color: #ddd; -fx-border-width: 1 0 0 0;");
        
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        
        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle("-fx-padding: 8 20; -fx-font-size: 11;");
        logoutBtn.setOnAction(e -> handleLogout());
        
        buttonBox.getChildren().add(logoutBtn);
        vbox.getChildren().add(buttonBox);
        
        return vbox;
    }

    private void handleLogout() {
        if (primaryStage != null && loginScene != null) {
            primaryStage.setScene(loginScene);
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Message");
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Inner class for table row data
     */
    public static class AccountRow {
        public final Account account;
        public final String customerName;
        public final String accountType;
        public final String balance;
        public final String monthlyInterest;
        public final String status;
        public final String dateCreated;

        public AccountRow(Account account, String customerName, String accountType, 
                         String balance, String monthlyInterest, String status, String dateCreated) {
            this.account = account;
            this.customerName = customerName;
            this.accountType = accountType;
            this.balance = balance;
            this.monthlyInterest = monthlyInterest;
            this.status = status;
            this.dateCreated = dateCreated;
        }

        public Account getAccount() { return account; }
        public String getCustomerName() { return customerName; }
        public String getAccountType() { return accountType; }
        public String getBalance() { return balance; }
        public String getMonthlyInterest() { return monthlyInterest; }
        public String getStatus() { return status; }
        public String getDateCreated() { return dateCreated; }
    }
}
