package com.bankingsystem.mvc.view;

import com.bankingsystem.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.Scene;

import java.util.List;
import java.util.Arrays;

/**
 * Customer Dashboard showing account details, approval status, and balances
 * Displays approval notification popups when account is approved
 */
public class CustomerDashboardView extends BorderPane {
    private final com.bankingsystem.Customer customer;
    private final TableView<AccountDetailRow> accountTable;
    private final Label messageLabel;
    private final Stage primaryStage;
    private final Scene loginScene;
    private Button addAccountButton;

    public CustomerDashboardView(com.bankingsystem.Customer customer) {
        this(customer, null, null);
    }

    public CustomerDashboardView(com.bankingsystem.Customer customer, Stage primaryStage, Scene loginScene) {
        this.customer = customer;
        this.primaryStage = primaryStage;
        this.loginScene = loginScene;
        this.accountTable = new TableView<>();
        this.messageLabel = new Label();
        
        initializeUI();
        loadAccountsAndCheckApproval();
    }

    private void initializeUI() {
        setPadding(new Insets(15));
        setStyle("-fx-font-size: 11;");
        
        // Top: Title and customer info
        VBox topBox = createTopSection();
        setTop(topBox);
        
        // Center: Table of accounts
        setupTable();
        ScrollPane tableScroll = new ScrollPane(accountTable);
        tableScroll.setFitToWidth(true);
        tableScroll.setPrefHeight(400);
        setCenter(tableScroll);
        
        // Bottom: Action buttons
        VBox bottomBox = createBottomSection();
        setBottom(bottomBox);
    }

    private VBox createTopSection() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(0, 0, 15, 0));
        
        String custName = customer.getFirstName() + " " + customer.getLastName();
        Label titleLabel = new Label("Customer Dashboard - " + custName);
        titleLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #1a5490;");
        
        String custId = customer.getCustomerID();
        String nationalId = customer.getNationalID() != null ? customer.getNationalID() : "N/A";
        Label detailsLabel = new Label("Customer ID: " + custId + " | National ID: " + nationalId);
        detailsLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #666;");
        
        messageLabel.setStyle("-fx-text-fill: #000; -fx-font-size: 11;");
        messageLabel.setWrapText(true);
        
        vbox.getChildren().addAll(titleLabel, detailsLabel, messageLabel);
        return vbox;
    }

    private void setupTable() {
        accountTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        accountTable.setPrefHeight(400);
        
        // Column: Account Number
        TableColumn<AccountDetailRow, String> acctNumCol = new TableColumn<>("Account Number");
        acctNumCol.setCellValueFactory(cv -> new javafx.beans.property.SimpleStringProperty(cv.getValue().getAccountNumber()));
        acctNumCol.setPrefWidth(120);
        
        // Column: Account Type
        TableColumn<AccountDetailRow, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(cv -> new javafx.beans.property.SimpleStringProperty(cv.getValue().getAccountType()));
        typeCol.setPrefWidth(80);
        
        // Column: Status
        TableColumn<AccountDetailRow, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cv -> new javafx.beans.property.SimpleStringProperty(cv.getValue().getStatus()));
        statusCol.setPrefWidth(100);
        
        // Column: Balance
        TableColumn<AccountDetailRow, String> balanceCol = new TableColumn<>("Balance (P)");
        balanceCol.setCellValueFactory(cv -> new javafx.beans.property.SimpleStringProperty(cv.getValue().getBalance()));
        balanceCol.setPrefWidth(100);
        
        // Column: Date Opened
        TableColumn<AccountDetailRow, String> dateCol = new TableColumn<>("Date Opened");
        dateCol.setCellValueFactory(cv -> new javafx.beans.property.SimpleStringProperty(cv.getValue().getDateOpened()));
        dateCol.setPrefWidth(120);
        
        accountTable.getColumns().addAll(Arrays.asList(acctNumCol, typeCol, statusCol, balanceCol, dateCol));
    }

    private VBox createBottomSection() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(15, 0, 0, 0));
        vbox.setBorder(new Border(
            new BorderStroke(
                Color.web("#ddd"),
                BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY,
                new BorderWidths(1, 0, 0, 0)
            )
        ));
        
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_LEFT);
        
        Button depositButton = new Button("Deposit");
        depositButton.setPrefWidth(100);
        depositButton.setStyle("-fx-padding: 8; -fx-font-size: 11;");
        depositButton.setOnAction(e -> handleDeposit());
        
        Button withdrawButton = new Button("Withdraw");
        withdrawButton.setPrefWidth(100);
        withdrawButton.setStyle("-fx-padding: 8; -fx-font-size: 11;");
        withdrawButton.setOnAction(e -> handleWithdraw());
        
        Button transferButton = new Button("Transfer");
            addAccountButton = new Button("Add Account");
            addAccountButton.setPrefWidth(110);
            addAccountButton.setStyle("-fx-padding: 8; -fx-font-size: 11;");
            addAccountButton.setOnAction(e -> showAddAccountDialog());
            // Disable if customer already has 3 or more accounts
            addAccountButton.setDisable(customer.getAccounts().size() >= 3);
        transferButton.setPrefWidth(100);
        transferButton.setStyle("-fx-padding: 8; -fx-font-size: 11;");
        transferButton.setOnAction(e -> handleTransfer());
        
        Button historyButton = new Button("View History");
        historyButton.setPrefWidth(100);
        historyButton.setStyle("-fx-padding: 8; -fx-font-size: 11;");
        historyButton.setOnAction(e -> handleViewHistory());
        
        Button refreshButton = new Button("Refresh");
        refreshButton.setPrefWidth(100);
        refreshButton.setStyle("-fx-padding: 8; -fx-font-size: 11;");
        refreshButton.setOnAction(e -> loadAccountsAndCheckApproval());
        
        Button logoutButton = new Button("Logout");
        logoutButton.setPrefWidth(100);
        logoutButton.setStyle("-fx-padding: 8; -fx-font-size: 11;");
        logoutButton.setOnAction(e -> handleLogout());
        
        buttonBox.getChildren().addAll(depositButton, withdrawButton, transferButton, addAccountButton, historyButton, refreshButton, logoutButton);
        vbox.getChildren().add(buttonBox);
        
        return vbox;
    }

    private void loadAccountsAndCheckApproval() {
        accountTable.getItems().clear();
        
        List<Account> accounts = customer.getAccounts();
        for (Account account : accounts) {
            String acctNum = account.getAccountNumber() != null ? account.getAccountNumber() : "(Pending)";
            String acctType = getAccountType(account);
            String status = account.getStatus().getDisplayName();
            String balance = String.format("%.2f", account.getBalance());
            String dateOpened = account.getDateOpened().toString();
            
            AccountDetailRow row = new AccountDetailRow(account, acctNum, acctType, status, balance, dateOpened);
            accountTable.getItems().add(row);
            
            // Check for approval notification
            if (AccountApprovalService.hasPendingApprovalNotification(account)) {
                String message = "Account Approved!\n\n" +
                    "Congratulations! Your " + acctType + " account has been approved.\n\n" +
                    "Account Number: " + account.getAccountNumber() + "\n" +
                    "Current Balance: P" + balance;
                
                showApprovalAlert("Account Approved", message);
                AccountApprovalService.clearApprovalNotification(account);
            }
        }
        
        if (accounts.isEmpty()) {
            messageLabel.setText("You have no accounts yet. Please visit a branch to open an account.");
        } else {
            messageLabel.setText(accounts.size() + " account(s) found");
        }
        // Update add-account button state
        if (addAccountButton != null) {
            addAccountButton.setDisable(accounts.size() >= 3);
        }
    }

    private String getAccountType(Account account) {
        if (account instanceof SavingsAccount) return "Savings";
        if (account instanceof InvestmentAccount) return "Investment";
        if (account instanceof ChequeAccount) return "Cheque";
        return "Unknown";
    }

    private void handleDeposit() {
        AccountDetailRow selected = accountTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an account to deposit to");
            return;
        }
        
        Account account = selected.getAccount();
        
        // Show deposit dialog
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Deposit Funds");
        dialog.setHeaderText("Enter amount to deposit (Pula):");
        dialog.setContentText("Amount:");
        
        dialog.showAndWait().ifPresent(amountStr -> {
            try {
                double amount = Double.parseDouble(amountStr);
                if (amount <= 0) {
                    showAlert(Alert.AlertType.ERROR, "Invalid Amount", "Deposit amount must be positive");
                    return;
                }
                
                account.deposit(amount);
                showAlert(Alert.AlertType.INFORMATION, "Deposit Successful", 
                    "Deposited: P" + String.format("%.2f", amount) + "\n" +
                    "New Balance: P" + String.format("%.2f", account.getBalance()));
                loadAccountsAndCheckApproval();
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid number");
            }
        });
    }

    private void handleWithdraw() {
        AccountDetailRow selected = accountTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an account to withdraw from");
            return;
        }
        
        Account account = selected.getAccount();

        // Prevent withdrawals from accounts pending approval
        if (account.getStatus() == AccountStatus.PENDING) {
            showAlert(Alert.AlertType.WARNING, "Pending Account", "Cannot withdraw from an account pending approval");
            return;
        }
        
        // Check if account supports withdrawals
        if (account instanceof SavingsAccount) {
            showAlert(Alert.AlertType.WARNING, "Cannot Withdraw", "Savings accounts do not allow withdrawals");
            return;
        }
        
        if (!(account instanceof Withdrawable)) {
            showAlert(Alert.AlertType.WARNING, "Cannot Withdraw", "This account type does not support withdrawals");
            return;
        }
        
        // Show withdrawal dialog
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Withdraw Funds");
        dialog.setHeaderText("Enter amount to withdraw (Pula):");
        dialog.setContentText("Amount:");
        
        dialog.showAndWait().ifPresent(amountStr -> {
            try {
                double amount = Double.parseDouble(amountStr);
                if (amount <= 0) {
                    showAlert(Alert.AlertType.ERROR, "Invalid Amount", "Withdrawal amount must be positive");
                    return;
                }
                
                boolean success = account.withdraw(amount);
                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Withdrawal Successful", 
                        "Withdrawn: P" + String.format("%.2f", amount) + "\n" +
                        "New Balance: P" + String.format("%.2f", account.getBalance()));
                    loadAccountsAndCheckApproval();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Withdrawal Failed", 
                        "Insufficient funds. Current balance: P" + String.format("%.2f", account.getBalance()));
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid number");
            }
        });
    }

    private void handleTransfer() {
        AccountDetailRow selected = accountTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an account to transfer from");
            return;
        }
        
        Account fromAccount = selected.getAccount();

        // Prevent transfers from pending accounts
        if (fromAccount.getStatus() == AccountStatus.PENDING) {
            showAlert(Alert.AlertType.WARNING, "Pending Account", "Cannot transfer from an account pending approval");
            return;
        }
        
        // Check if account supports transfers
        if (fromAccount instanceof SavingsAccount) {
            showAlert(Alert.AlertType.WARNING, "Cannot Transfer", "Savings accounts cannot be used for transfers");
            return;
        }
        
        List<Account> otherAccounts = customer.getAccounts();
        if (otherAccounts.size() < 2) {
            showAlert(Alert.AlertType.WARNING, "Cannot Transfer", "You need at least 2 accounts to transfer between");
            return;
        }
        
        // Create transfer dialog
        Dialog<Void> transferDialog = new Dialog<>();
        transferDialog.setTitle("Transfer Funds");
        transferDialog.setHeaderText("Transfer between your accounts");
        
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        
        Label fromLabel = new Label("From: " + selected.getAccountNumber() + " (" + selected.getAccountType() + ")");
        
        Label toLabel = new Label("To Account:");
        ComboBox<Account> toAccountCombo = new ComboBox<>();
        for (Account acc : otherAccounts) {
            if (!acc.equals(fromAccount) && !(acc instanceof SavingsAccount) && acc.getStatus() != AccountStatus.PENDING) {
                toAccountCombo.getItems().add(acc);
            }
        }
        toAccountCombo.setCellFactory(param -> new ListCell<Account>() {
            @Override
            protected void updateItem(Account item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String type = "Unknown";
                    if (item instanceof InvestmentAccount) type = "Investment";
                    else if (item instanceof ChequeAccount) type = "Cheque";
                    setText((item.getAccountNumber() != null ? item.getAccountNumber() : "(Pending)") + " (" + type + ")");
                }
            }
        });
        toAccountCombo.setButtonCell(new ListCell<Account>() {
            @Override
            protected void updateItem(Account item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String type = "Unknown";
                    if (item instanceof InvestmentAccount) type = "Investment";
                    else if (item instanceof ChequeAccount) type = "Cheque";
                    setText((item.getAccountNumber() != null ? item.getAccountNumber() : "(Pending)") + " (" + type + ")");
                }
            }
        });
        toAccountCombo.setPrefWidth(250);
        
        Label amountLabel = new Label("Amount:");
        TextField amountField = new TextField();
        amountField.setPromptText("Enter amount in Pula");
        amountField.setPrefWidth(250);
        
        content.getChildren().addAll(fromLabel, toLabel, toAccountCombo, amountLabel, amountField);
        transferDialog.getDialogPane().setContent(content);
        
        ButtonType transferBtn = new ButtonType("Transfer", ButtonBar.ButtonData.OK_DONE);
        transferDialog.getDialogPane().getButtonTypes().addAll(transferBtn, ButtonType.CANCEL);
        
        transferDialog.setResultConverter(dialogButton -> {
            if (dialogButton == transferBtn) {
                try {
                    Account toAccount = toAccountCombo.getValue();
                    if (toAccount == null) {
                        showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a destination account");
                        return null;
                    }
                    
                    double amount = Double.parseDouble(amountField.getText());
                    if (amount <= 0) {
                        showAlert(Alert.AlertType.ERROR, "Invalid Amount", "Transfer amount must be positive");
                        return null;
                    }
                    
                    boolean success = fromAccount.transferTo(toAccount, amount);
                    if (success) {
                        showAlert(Alert.AlertType.INFORMATION, "Transfer Successful", 
                            "Transferred: P" + String.format("%.2f", amount) + "\n" +
                            "From: " + selected.getAccountNumber() + "\n" +
                            "To: " + (toAccount.getAccountNumber() != null ? toAccount.getAccountNumber() : "(Pending)"));
                        loadAccountsAndCheckApproval();
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Transfer Failed", "Insufficient funds or transfer not allowed");
                    }
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid amount");
                }
            }
            return null;
        });
        
        transferDialog.showAndWait();
    }

    private void handleViewHistory() {
        AccountDetailRow selected = accountTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an account to view history");
            return;
        }
        
        Account account = selected.getAccount();
        List<Transaction> transactions = account.getTransactions();
        
        Dialog<Void> historyDialog = new Dialog<>();
        historyDialog.setTitle("Transaction History");
        historyDialog.setHeaderText("Transaction History for " + selected.getAccountNumber() + 
                                   " (" + selected.getAccountType() + ")");
        historyDialog.setWidth(800);
        historyDialog.setHeight(600);
        
        TableView<Transaction> transactionTable = new TableView<>();
        transactionTable.setPrefHeight(400);
        
        TableColumn<Transaction, String> timestampCol = new TableColumn<>("Date/Time");
        timestampCol.setCellValueFactory(cv -> new javafx.beans.property.SimpleStringProperty(
            cv.getValue().getTimestamp().toString()));
        timestampCol.setPrefWidth(180);
        
        TableColumn<Transaction, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(cv -> new javafx.beans.property.SimpleStringProperty(
            cv.getValue().getType().toString()));
        typeCol.setPrefWidth(90);
        
        TableColumn<Transaction, String> amountCol = new TableColumn<>("Amount (P)");
        amountCol.setCellValueFactory(cv -> new javafx.beans.property.SimpleStringProperty(
            String.format("%.2f", cv.getValue().getAmount())));
        amountCol.setPrefWidth(100);
        
        TableColumn<Transaction, String> balanceCol = new TableColumn<>("Balance After (P)");
        balanceCol.setCellValueFactory(cv -> new javafx.beans.property.SimpleStringProperty(
            String.format("%.2f", cv.getValue().getBalanceAfter())));
        balanceCol.setPrefWidth(130);
        
        TableColumn<Transaction, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(cv -> new javafx.beans.property.SimpleStringProperty(
            cv.getValue().getDescription() != null ? cv.getValue().getDescription() : "-"));
        descCol.setPrefWidth(150);
        
        transactionTable.getColumns().addAll(Arrays.asList(timestampCol, typeCol, amountCol, balanceCol, descCol));
        transactionTable.getItems().addAll(transactions);
        
        ScrollPane scrollPane = new ScrollPane(transactionTable);
        scrollPane.setFitToWidth(true);
        
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        
        Label accountInfoLabel = new Label("Account: " + selected.getAccountNumber() + " | Type: " + selected.getAccountType() + 
                                          " | Current Balance: P" + String.format("%.2f", account.getBalance()));
        accountInfoLabel.setStyle("-fx-font-weight: bold;");
        
        // Show calculated interest for applicable accounts
        String interestInfo = "";
        if (account instanceof PayInterest) {
            double monthlyInterest = account.calculateInterest();
            interestInfo = " | Calculated Monthly Interest: P" + String.format("%.2f", monthlyInterest);
        }
        Label interestLabel = new Label(interestInfo);
        interestLabel.setStyle("-fx-text-fill: #2196F3;");
        
        VBox infoBox = new VBox(5);
        infoBox.getChildren().addAll(accountInfoLabel, interestLabel);
        
        content.getChildren().addAll(infoBox, new Separator(), scrollPane);
        historyDialog.getDialogPane().setContent(content);
        historyDialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        
        historyDialog.showAndWait();
    }

    private void showAddAccountDialog() {
        if (customer.getAccounts().size() >= 3) {
            showAlert(Alert.AlertType.WARNING, "Limit Reached", "You may have a maximum of 3 accounts.");
            return;
        }

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Add New Account");
        dialog.setHeaderText("Create a new account");

        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        Label typeLabel = new Label("Account Type:");
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("Savings", "Investment", "Cheque");
        typeCombo.setValue("Savings");

        Label depositLabel = new Label("Initial Deposit (P):");
        TextField depositField = new TextField();
        depositField.setPromptText("Enter amount");

        content.getChildren().addAll(typeLabel, typeCombo, depositLabel, depositField);
        dialog.getDialogPane().setContent(content);

        ButtonType createBtn = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createBtn, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == createBtn) {
                try {
                    String type = typeCombo.getValue();
                    double deposit = Double.parseDouble(depositField.getText());

                    Account newAcc = null;
                    // Only individual customers may open accounts via dashboard UI
                    if (customer instanceof IndividualCustomer) {
                        newAcc = ((IndividualCustomer) customer).openAccount(type.toLowerCase(), deposit);
                    } else {
                        showAlert(Alert.AlertType.WARNING, "Not Supported", "Company accounts must be opened at a branch. Please contact the bank.");
                        return null;
                    }

                    if (newAcc == null) {
                        showAlert(Alert.AlertType.ERROR, "Creation Failed", "Failed to create account. Check eligibility and deposit amounts.");
                        return null;
                    }

                    // Persist the new account to DB
                    com.bankingsystem.persistence.AccountDAOImpl dao = new com.bankingsystem.persistence.AccountDAOImpl();
                    dao.saveAccount(newAcc);

                    showAlert(Alert.AlertType.INFORMATION, "Success", "Account created and queued for approval.");
                    loadAccountsAndCheckApproval();
                } catch (NumberFormatException ex) {
                    showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid deposit amount");
                }
            }
            return null;
        });

        dialog.showAndWait();
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

    private void showApprovalAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText("Excellent News!");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Inner class representing account details for table display
     */
    public static class AccountDetailRow {
        private final Account account;
        private final String accountNumber;
        private final String accountType;
        private final String status;
        private final String balance;
        private final String dateOpened;

        public AccountDetailRow(Account account, String accountNumber, String accountType, 
                               String status, String balance, String dateOpened) {
            this.account = account;
            this.accountNumber = accountNumber;
            this.accountType = accountType;
            this.status = status;
            this.balance = balance;
            this.dateOpened = dateOpened;
        }

        public Account getAccount() { return account; }
        public String getAccountNumber() { return accountNumber; }
        public String getAccountType() { return accountType; }
        public String getStatus() { return status; }
        public String getBalance() { return balance; }
        public String getDateOpened() { return dateOpened; }
    }
}
