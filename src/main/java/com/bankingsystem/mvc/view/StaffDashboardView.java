package com.bankingsystem.mvc.view;

import com.bankingsystem.*;
import com.bankingsystem.persistence.AccountDAOImpl;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.stage.Stage;

import java.util.List;
import java.util.Arrays;

/**
 * Staff Dashboard for approving and rejecting pending accounts
 */
public class StaffDashboardView extends BorderPane {
    private final Bank bank;
    private final TableView<AccountRow> accountTable;
    private final TableView<AccountRow> approvedTable;
    private final Label statusLabel;
    private final Stage primaryStage;
    private final Scene loginScene;
    private final AccountDAOImpl accountDAO;

    public StaffDashboardView(Bank bank) {
        this(bank, null, null);
    }

    public StaffDashboardView(Bank bank, Stage primaryStage, Scene loginScene) {
        this.bank = bank;
        this.primaryStage = primaryStage;
        this.loginScene = loginScene;
        this.accountTable = new TableView<>();
        this.approvedTable = new TableView<>();
        this.statusLabel = new Label("Pending Accounts");
        this.accountDAO = new AccountDAOImpl();
        
        initializeUI();
        loadPendingAccounts();
    }

    private void initializeUI() {
        setPadding(new Insets(15));
        setStyle("-fx-font-size: 11;");
        
        // Top: Title and controls
        VBox topBox = createTopSection();
        setTop(topBox);
        
        // Center: Tabs for Pending and Approved accounts
        setupTable();
        // Setup approved table (same columns)
        // duplicate of setupTable for approvedTable
        approvedTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        approvedTable.setPrefHeight(400);

        TableColumn<AccountRow, String> customerColA = new TableColumn<>("Customer");
        customerColA.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        customerColA.setPrefWidth(120);

        TableColumn<AccountRow, String> typeColA = new TableColumn<>("Type");
        typeColA.setCellValueFactory(new PropertyValueFactory<>("accountType"));
        typeColA.setPrefWidth(80);

        TableColumn<AccountRow, String> balanceColA = new TableColumn<>("Balance (P)");
        balanceColA.setCellValueFactory(new PropertyValueFactory<>("balance"));
        balanceColA.setPrefWidth(90);

        TableColumn<AccountRow, String> interestColA = new TableColumn<>("Monthly Interest (P)");
        interestColA.setCellValueFactory(new PropertyValueFactory<>("monthlyInterest"));
        interestColA.setPrefWidth(120);

        TableColumn<AccountRow, String> statusColA = new TableColumn<>("Status");
        statusColA.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusColA.setPrefWidth(80);

        TableColumn<AccountRow, String> dateColA = new TableColumn<>("Date Created");
        dateColA.setCellValueFactory(new PropertyValueFactory<>("dateCreated"));
        dateColA.setPrefWidth(120);

        approvedTable.getColumns().addAll(Arrays.asList(customerColA, typeColA, balanceColA, interestColA, statusColA, dateColA));

        ScrollPane pendingScroll = new ScrollPane(accountTable);
        pendingScroll.setFitToWidth(true);
        pendingScroll.setPrefHeight(400);

        ScrollPane approvedScroll = new ScrollPane(approvedTable);
        approvedScroll.setFitToWidth(true);
        approvedScroll.setPrefHeight(400);

        TabPane tabPane = new TabPane();
        Tab pendingTab = new Tab("Pending", pendingScroll);
        pendingTab.setClosable(false);
        Tab approvedTab = new Tab("Approved", approvedScroll);
        approvedTab.setClosable(false);
        tabPane.getTabs().addAll(pendingTab, approvedTab);
        setCenter(tabPane);
        
        // Bottom: Status and buttons
        VBox bottomBox = createBottomSection();
        setBottom(bottomBox);
    }

    private VBox createTopSection() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(0, 0, 15, 0));
        
        Label titleLabel = new Label("Staff Dashboard - Account Approvals");
        titleLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #1a5490;");
        
        Label descLabel = new Label("Review and approve pending customer accounts");
        descLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #666;");
        
        vbox.getChildren().addAll(titleLabel, descLabel);
        return vbox;
    }

    private void setupTable() {
        accountTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        accountTable.setPrefHeight(400);
        
        // Column: Customer Name
        TableColumn<AccountRow, String> customerCol = new TableColumn<>("Customer");
        customerCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        customerCol.setPrefWidth(120);
        
        // Column: Account Type
        TableColumn<AccountRow, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("accountType"));
        typeCol.setPrefWidth(80);
        
        // Column: Balance
        TableColumn<AccountRow, String> balanceCol = new TableColumn<>("Balance (P)");
        balanceCol.setCellValueFactory(new PropertyValueFactory<>("balance"));
        balanceCol.setPrefWidth(90);
        
        // Column: Monthly Interest
        TableColumn<AccountRow, String> interestCol = new TableColumn<>("Monthly Interest (P)");
        interestCol.setCellValueFactory(new PropertyValueFactory<>("monthlyInterest"));
        interestCol.setPrefWidth(120);
        
        // Column: Status
        TableColumn<AccountRow, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(80);
        
        // Column: Date Created
        TableColumn<AccountRow, String> dateCol = new TableColumn<>("Date Created");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("dateCreated"));
        dateCol.setPrefWidth(120);
        
        accountTable.getColumns().addAll(Arrays.asList(customerCol, typeCol, balanceCol, interestCol, statusCol, dateCol));
    }

    private VBox createBottomSection() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(15, 0, 0, 0));
        vbox.setBorder(new Border(
            new BorderStroke(
                Color.web("#ddd"),
                BorderStrokeStyle.SOLID,
                null,
                new BorderWidths(1, 0, 0, 0)
            )
        ));
        
        // Status label
        statusLabel.setStyle("-fx-text-fill: #666;");
        
        // Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_LEFT);
        
        Button approveButton = new Button("Approve Selected");
        approveButton.setPrefWidth(120);
        approveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 8; -fx-font-size: 11;");
        approveButton.setOnAction(e -> handleApprove());
        
        Button rejectButton = new Button("Reject Selected");
        rejectButton.setPrefWidth(120);
        rejectButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-padding: 8; -fx-font-size: 11;");
        rejectButton.setOnAction(e -> handleReject());
        
        Button closeAccountButton = new Button("Close Account");
        closeAccountButton.setPrefWidth(120);
        closeAccountButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-padding: 8; -fx-font-size: 11;");
        closeAccountButton.setOnAction(e -> handleCloseAccount());
        
        Button newCustomerButton = new Button("New Customer");
        newCustomerButton.setPrefWidth(120);
        newCustomerButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-padding: 8; -fx-font-size: 11;");
        newCustomerButton.setOnAction(e -> handleNewCustomer());
        
        Button refreshButton = new Button("Refresh");
        refreshButton.setPrefWidth(100);
        refreshButton.setStyle("-fx-padding: 8; -fx-font-size: 11;");
        refreshButton.setOnAction(e -> { loadPendingAccounts(); loadApprovedAccounts(); });
        
        Button logoutButton = new Button("Logout");
        logoutButton.setPrefWidth(100);
        logoutButton.setStyle("-fx-padding: 8; -fx-font-size: 11;");
        logoutButton.setOnAction(e -> handleLogout());
        
        buttonBox.getChildren().addAll(approveButton, rejectButton, closeAccountButton, newCustomerButton, refreshButton, logoutButton);
        
        vbox.getChildren().addAll(statusLabel, buttonBox);
        return vbox;
    }

    private void loadPendingAccounts() {
        accountTable.getItems().clear();
        List<Account> pendingAccounts = accountDAO.getPendingAccounts();
        
        for (Account account : pendingAccounts) {
            String customerName = account.getOwner().getFirstName() + " " + account.getOwner().getLastName();
            String accountType = getAccountType(account);
            String balance = String.format("%.2f", account.getBalance());
            String status = account.getStatus().getDisplayName();
            String dateCreated = account.getDateOpened().toString();
            
            // Calculate monthly interest if applicable
            String monthlyInterest = "N/A";
            if (account instanceof PayInterest) {
                double interest = account.calculateInterest();
                monthlyInterest = String.format("%.2f", interest);
            }
            
            AccountRow row = new AccountRow(account, customerName, accountType, balance, monthlyInterest, status, dateCreated);
            accountTable.getItems().add(row);
        }
        
        statusLabel.setText(String.format("Pending Accounts: %d", pendingAccounts.size()));
    }

    private void loadApprovedAccounts() {
        approvedTable.getItems().clear();
        List<Account> approvedAccounts = accountDAO.getApprovedAccounts();

        for (Account account : approvedAccounts) {
            String customerName = account.getOwner().getFirstName() + " " + account.getOwner().getLastName();
            String accountType = getAccountType(account);
            String balance = String.format("%.2f", account.getBalance());
            String status = account.getStatus().getDisplayName();
            String dateCreated = account.getDateOpened().toString();

            String monthlyInterest = "N/A";
            if (account instanceof PayInterest) {
                double interest = account.calculateInterest();
                monthlyInterest = String.format("%.2f", interest);
            }

            AccountRow row = new AccountRow(account, customerName, accountType, balance, monthlyInterest, status, dateCreated);
            approvedTable.getItems().add(row);
        }
    }

    private String getAccountType(Account account) {
        if (account instanceof SavingsAccount) return "Savings";
        if (account instanceof InvestmentAccount) return "Investment";
        if (account instanceof ChequeAccount) return "Cheque";
        return "Unknown";
    }

    private void handleApprove() {
        AccountRow selected = accountTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an account to approve");
            return;
        }
        
        Account account = selected.getAccount();
        Customer owner = account.getOwner();

        // Check eligibility
        if (!AccountApprovalService.isAccountEligibleForApproval(account)) {
            if (account instanceof InvestmentAccount) {
                showAlert(Alert.AlertType.WARNING, "Ineligible Account", 
                    "Investment account requires minimum balance of P1000 for approval.\n" +
                    "Current balance: P" + String.format("%.2f", account.getBalance()));
            }
            return;
        }

        // If company account, show company info for review
        if (owner instanceof CompanyCustomer) {
            CompanyCustomer companyCustomer = (CompanyCustomer) owner;
            CompanyRegistration companyReg = companyCustomer.getCompanyRegistration();
            
            if (companyReg != null) {
                Alert reviewAlert = new Alert(Alert.AlertType.INFORMATION);
                reviewAlert.setTitle("Company Account Review");
                reviewAlert.setHeaderText("Review Company Information");
                reviewAlert.setContentText(companyReg.getFormattedInfo());
                reviewAlert.showAndWait();
            }
        }

        // Show account number assignment dialog
        TextInputDialog accountNumberDialog = new TextInputDialog();
        accountNumberDialog.setTitle("Assign Account Number");
        accountNumberDialog.setHeaderText("Enter Account Number");
        accountNumberDialog.setContentText("Auto-generated or custom account number:");
        
        // Suggest auto-generated account number
        String suggestedNumber = generateAccountNumber(account);
        accountNumberDialog.getEditor().setText(suggestedNumber);
        
        accountNumberDialog.showAndWait().ifPresent(accountNumber -> {
            if (accountNumber.trim().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Invalid", "Account number cannot be empty");
                return;
            }
            
            // Manually set the account number (for demo purposes)
            // In production, this would be done through a proper setter
            try {
                java.lang.reflect.Field field = Account.class.getDeclaredField("accountNumber");
                field.setAccessible(true);
                field.set(account, accountNumber.trim());
            } catch (Exception e) {
                // Fallback: just approve without manual assignment if reflection fails
            }
            
            // Approve account
            boolean success = AccountApprovalService.approveAccount(account, "staff1");
            if (success) {
                // Persist approval to database
                try {
                    accountDAO.updateAccount(account);
                } catch (Exception ex) {
                    System.err.println("Failed to persist approved account: " + ex.getMessage());
                }
                showAlert(Alert.AlertType.INFORMATION, "Success", 
                    "Account approved!\n\n" +
                    "Customer: " + account.getOwner().getFirstName() + " " + account.getOwner().getLastName() + "\n" +
                    "Account Type: " + getAccountType(account) + "\n" +
                    "Account Number: " + account.getAccountNumber() + "\n" +
                    "Balance: P" + String.format("%.2f", account.getBalance()));
                loadPendingAccounts();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to approve account");
            }
        });
    }

    private String generateAccountNumber(Account account) {
        String prefix;
        if (account instanceof SavingsAccount) {
            prefix = "SAV";
        } else if (account instanceof InvestmentAccount) {
            prefix = "INV";
        } else if (account instanceof ChequeAccount) {
            prefix = "CHQ";
        } else {
            prefix = "ACC";
        }
        return prefix + "-" + System.currentTimeMillis() % 1000000;
    }

    private void handleReject() {
        AccountRow selected = accountTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an account to reject");
            return;
        }
        
        // Confirm rejection
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Rejection");
        confirmAlert.setHeaderText("Reject Account?");
        confirmAlert.setContentText("Are you sure you want to reject this account?");
        
        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            Account account = selected.getAccount();
            boolean success = AccountApprovalService.rejectAccount(account, "staff1");
            if (success) {
                    // Persist rejection
                    try { accountDAO.updateAccount(account); } catch (Exception ex) { System.err.println("Failed to persist rejected account: " + ex.getMessage()); }
                    showAlert(Alert.AlertType.INFORMATION, "Success", 
                    "Account rejected for customer: " + account.getOwner().getFirstName());
                loadPendingAccounts();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to reject account");
            }
        }
    }

    private void handleCloseAccount() {
        AccountRow selected = accountTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an account to close");
            return;
        }
        
        Account account = selected.getAccount();
        
        // Confirm closure
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Account Closure");
        confirmAlert.setHeaderText("Close Account?");
        confirmAlert.setContentText("Are you sure you want to close this account?\n" +
                                    "Customer: " + account.getOwner().getFirstName() + " " + account.getOwner().getLastName() + "\n" +
                                    "Account Number: " + (account.getAccountNumber() != null ? account.getAccountNumber() : "(Pending)") + "\n" +
                                    "Balance: P" + String.format("%.2f", account.getBalance()) + "\n\n" +
                                    "This action cannot be undone.");
        
        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            // Mark account as closed (we'll use REJECTED status for closed accounts)
            account.reject("staff1");
            try { accountDAO.updateAccount(account); } catch (Exception ex) { System.err.println("Failed to persist closed account: " + ex.getMessage()); }
            showAlert(Alert.AlertType.INFORMATION, "Success", 
                "Account closed successfully for customer: " + account.getOwner().getFirstName());
            loadPendingAccounts();
        }
    }

    private void handleNewCustomer() {
        Dialog<Void> customerDialog = new Dialog<>();
        customerDialog.setTitle("Create New Customer");
        customerDialog.setHeaderText("Register a new customer");
        customerDialog.setWidth(600);
        
        VBox content = new VBox(10);
        content.setPadding(new Insets(15));
        
        // Customer type selection
        Label typeLabel = new Label("Customer Type:");
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("Individual", "Company");
        typeCombo.setValue("Individual");
        typeCombo.setPrefWidth(250);
        
        // Common fields
        Label fnameLabel = new Label("First Name:");
        TextField fnameField = new TextField();
        fnameField.setPromptText("Enter first name");
        fnameField.setPrefWidth(250);
        
        Label lnameLabel = new Label("Last Name:");
        TextField lnameField = new TextField();
        lnameField.setPromptText("Enter last name");
        lnameField.setPrefWidth(250);
        
        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();
        emailField.setPromptText("Enter email address");
        emailField.setPrefWidth(250);
        
        Label phoneLabel = new Label("Phone:");
        TextField phoneField = new TextField();
        phoneField.setPromptText("Enter phone number");
        phoneField.setPrefWidth(250);
        
        // Individual-specific fields
        Label nidLabel = new Label("National ID:");
        TextField nidField = new TextField();
        nidField.setPromptText("Enter national ID");
        nidField.setPrefWidth(250);
        
        // Company-specific fields
        Label coNameLabel = new Label("Company Name:");
        TextField coNameField = new TextField();
        coNameField.setPromptText("Enter company name");
        coNameField.setPrefWidth(250);
        coNameField.setVisible(false);
        
        Label coRegLabel = new Label("Registration Number:");
        TextField coRegField = new TextField();
        coRegField.setPromptText("Enter registration number");
        coRegField.setPrefWidth(250);
        coRegField.setVisible(false);
        
        // Toggle visibility based on type
        typeCombo.setOnAction(e -> {
            boolean isCompany = typeCombo.getValue().equals("Company");
            fnameLabel.setText(isCompany ? "Director Name:" : "First Name:");
            lnameLabel.setVisible(!isCompany);
            lnameField.setVisible(!isCompany);
            nidLabel.setVisible(!isCompany);
            nidField.setVisible(!isCompany);
            coNameLabel.setVisible(isCompany);
            coNameField.setVisible(isCompany);
            coRegLabel.setVisible(isCompany);
            coRegField.setVisible(isCompany);
        });
        
        content.getChildren().addAll(
            typeLabel, typeCombo,
            fnameLabel, fnameField,
            lnameLabel, lnameField,
            nidLabel, nidField,
            coNameLabel, coNameField,
            coRegLabel, coRegField,
            emailLabel, emailField,
            phoneLabel, phoneField
        );
        
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        customerDialog.getDialogPane().setContent(scrollPane);
        
        ButtonType createBtn = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        customerDialog.getDialogPane().getButtonTypes().addAll(createBtn, ButtonType.CANCEL);
        
        customerDialog.setResultConverter(dialogButton -> {
            if (dialogButton == createBtn) {
                try {
                    String type = typeCombo.getValue();
                    String fname = fnameField.getText().trim();
                    String lname = lnameField.getText().trim();
                    String email = emailField.getText().trim();
                    String phone = phoneField.getText().trim();
                    
                    if (fname.isEmpty() || email.isEmpty()) {
                        showAlert(Alert.AlertType.WARNING, "Missing Fields", "First Name and Email are required");
                        return null;
                    }
                    
                    if (type.equals("Individual")) {
                        String nid = nidField.getText().trim();
                        if (nid.isEmpty()) {
                            showAlert(Alert.AlertType.WARNING, "Missing Fields", "National ID is required for individuals");
                            return null;
                        }
                        
                        String custId = "CUST" + (1000 + bank.getCustomers().size());
                        IndividualCustomer newCustomer = new IndividualCustomer(custId, fname, lname, nid);
                        newCustomer.setEmail(email);
                        newCustomer.setPhone(phone);
                        newCustomer.setUsername(fname.toLowerCase() + System.nanoTime() % 1000);
                        newCustomer.setPassword("temppass123");
                        bank.registerCustomer(newCustomer);
                        
                        showAlert(Alert.AlertType.INFORMATION, "Success", 
                            "Individual customer created:\n" +
                            "Customer ID: " + custId + "\n" +
                            "Name: " + fname + " " + lname);
                    } else {
                        String coName = coNameField.getText().trim();
                        String coReg = coRegField.getText().trim();
                        
                        if (coName.isEmpty() || coReg.isEmpty()) {
                            showAlert(Alert.AlertType.WARNING, "Missing Fields", "Company Name and Registration Number are required");
                            return null;
                        }
                        
                        String custId = "COMP" + (1000 + bank.getCustomers().size());
                        CompanyCustomer newCompany = new CompanyCustomer(custId, coName, coReg);
                        newCompany.setEmail(email);
                        newCompany.setPhone(phone);
                        bank.registerCustomer(newCompany);
                        
                        showAlert(Alert.AlertType.INFORMATION, "Success", 
                            "Company customer created:\n" +
                            "Customer ID: " + custId + "\n" +
                            "Company: " + coName);
                    }
                    
                    loadPendingAccounts();
                } catch (Exception ex) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to create customer: " + ex.getMessage());
                }
            }
            return null;
        });
        
        customerDialog.showAndWait();
    }

    private void handleLogout() {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Logout");
        confirmAlert.setHeaderText("Logout?");
        confirmAlert.setContentText("Are you sure you want to logout?");
        
        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            if (primaryStage != null && loginScene != null) {
                primaryStage.setScene(loginScene);
                primaryStage.setTitle("Banking System - Login");
            } else {
                // Fallback: close the window
                Stage stage = (Stage) getScene().getWindow();
                if (stage != null) {
                    stage.close();
                }
            }
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Inner class representing a row in the accounts table
     */
    public static class AccountRow {
        private final Account account;
        private final String customerName;
        private final String accountType;
        private final String balance;
        private final String monthlyInterest;
        private final String status;
        private final String dateCreated;

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
