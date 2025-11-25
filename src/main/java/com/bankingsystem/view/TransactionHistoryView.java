package com.bankingsystem.view;

import com.bankingsystem.Account;
import com.bankingsystem.Transaction;
import com.bankingsystem.controller.AccountController;
import com.bankingsystem.controller.TransactionController;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

/**
 * View for displaying transaction history.
 * Shows transactions for selected account(s).
 */
public class TransactionHistoryView extends VBox {
    private final AccountController accountController;
    private final TransactionController transactionController;
    private final ComboBox<String> accountCombo = new ComboBox<>();
    private final TableView<TransactionRow> transactionTable = new TableView<>();
    private final Label messageLabel = new Label();

    public TransactionHistoryView(AccountController accountController, TransactionController transactionController) {
        this.accountController = accountController;
        this.transactionController = transactionController;
        buildView();
    }

    private void buildView() {
        setPadding(new Insets(20));
        setSpacing(15);

        // Title
        Label title = new Label("Transaction History");
        title.setFont(Font.font("System", FontWeight.BOLD, 20));
        title.getStyleClass().add("title");

        // Account selection
        Label accountLabel = new Label("Select Account:");
        for (Account acc : accountController.listAccounts()) {
            String accountInfo = String.format("%s - %s",
                acc.getAccountNumber(),
                acc.getClass().getSimpleName().replace("Account", ""));
            accountCombo.getItems().add(accountInfo);
        }
        accountCombo.setPrefWidth(400);
        accountCombo.setOnAction(e -> loadTransactions());

        // Create table columns
        TableColumn<TransactionRow, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
        dateCol.setPrefWidth(150);

        TableColumn<TransactionRow, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
        typeCol.setPrefWidth(100);

        TableColumn<TransactionRow, String> amountCol = new TableColumn<>("Amount (BWP)");
        amountCol.setCellValueFactory(cellData -> cellData.getValue().amountProperty());
        amountCol.setPrefWidth(150);

        TableColumn<TransactionRow, String> balanceCol = new TableColumn<>("Balance After (BWP)");
        balanceCol.setCellValueFactory(cellData -> cellData.getValue().balanceProperty());
        balanceCol.setPrefWidth(150);

        TableColumn<TransactionRow, String> descriptionCol = new TableColumn<>("Description");
        descriptionCol.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        descriptionCol.setPrefWidth(200);

        transactionTable.getColumns().addAll(Arrays.asList(dateCol, typeCol, amountCol, balanceCol, descriptionCol));
        transactionTable.setPrefHeight(400);

        // Buttons
        Button refreshBtn = new Button("Refresh");
        refreshBtn.setOnAction(e -> loadTransactions());
        refreshBtn.getStyleClass().add("primary-button");

        Button backBtn = new Button("Back to Dashboard");
        backBtn.setOnAction(e -> onBack());
        backBtn.getStyleClass().add("secondary-button");

        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(refreshBtn, backBtn);

        // Message label
        messageLabel.setWrapText(true);
        messageLabel.getStyleClass().add("info-label");

        // Layout
        VBox formBox = new VBox(10);
        formBox.setPadding(new Insets(15));
        formBox.getStyleClass().add("panel");
        formBox.getChildren().addAll(accountLabel, accountCombo, transactionTable);

        getChildren().addAll(title, formBox, buttonBox, messageLabel);

        // Load transactions if account is selected
        if (!accountCombo.getItems().isEmpty()) {
            accountCombo.setValue(accountCombo.getItems().get(0));
            loadTransactions();
        }
    }

    private void loadTransactions() {
        String selected = accountCombo.getValue();
        if (selected == null || selected.isEmpty()) {
            transactionTable.getItems().clear();
            messageLabel.setText("Please select an account");
            return;
        }

        String accountNumber = selected.split(" - ")[0];
        java.util.List<Transaction> transactions = transactionController.getTransactionHistory(accountNumber);

        transactionTable.getItems().clear();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (Transaction t : transactions) {
            TransactionRow row = new TransactionRow(
                t.getTimestamp().format(formatter),
                t.getType().toString(),
                String.format("%.2f", t.getAmount()),
                String.format("%.2f", t.getBalanceAfter()),
                t.getDescription() != null ? t.getDescription() : ""
            );
            transactionTable.getItems().add(row);
        }

        if (transactions.isEmpty()) {
            messageLabel.setText("No transactions found for this account");
        } else {
            messageLabel.setText(String.format("Found %d transaction(s)", transactions.size()));
        }
    }

    private void onBack() {
        // Navigate back to dashboard
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Navigation");
        alert.setContentText("Returning to dashboard...");
        alert.show();
    }

    /**
     * Helper class for table rows
     */
    private static class TransactionRow {
        private final javafx.beans.property.SimpleStringProperty date;
        private final javafx.beans.property.SimpleStringProperty type;
        private final javafx.beans.property.SimpleStringProperty amount;
        private final javafx.beans.property.SimpleStringProperty balance;
        private final javafx.beans.property.SimpleStringProperty description;

        public TransactionRow(String date, String type, String amount, String balance, String description) {
            this.date = new javafx.beans.property.SimpleStringProperty(date);
            this.type = new javafx.beans.property.SimpleStringProperty(type);
            this.amount = new javafx.beans.property.SimpleStringProperty(amount);
            this.balance = new javafx.beans.property.SimpleStringProperty(balance);
            this.description = new javafx.beans.property.SimpleStringProperty(description);
        }

        public javafx.beans.property.StringProperty dateProperty() { return date; }
        public javafx.beans.property.StringProperty typeProperty() { return type; }
        public javafx.beans.property.StringProperty amountProperty() { return amount; }
        public javafx.beans.property.StringProperty balanceProperty() { return balance; }
        public javafx.beans.property.StringProperty descriptionProperty() { return description; }
    }
}

