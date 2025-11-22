package com.bankingsystem.view;

import com.bankingsystem.Account;
import com.bankingsystem.controller.AccountController;
import com.bankingsystem.controller.TransactionController;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * View for displaying account balances.
 * Shows all accounts with their current balances.
 */
public class ViewBalanceView extends VBox {
    private final AccountController accountController;
    private final TransactionController transactionController;
    private final TableView<AccountBalanceRow> balanceTable = new TableView<>();

    public ViewBalanceView(AccountController accountController, TransactionController transactionController) {
        this.accountController = accountController;
        this.transactionController = transactionController;
        buildView();
    }

    private void buildView() {
        setPadding(new Insets(20));
        setSpacing(15);

        // Title
        Label title = new Label("Account Balances");
        title.setFont(Font.font("System", FontWeight.BOLD, 20));
        title.getStyleClass().add("title");

        // Create table columns
        TableColumn<AccountBalanceRow, String> accountNumberCol = new TableColumn<>("Account Number");
        accountNumberCol.setCellValueFactory(cellData -> cellData.getValue().accountNumberProperty());
        accountNumberCol.setPrefWidth(200);

        TableColumn<AccountBalanceRow, String> accountTypeCol = new TableColumn<>("Account Type");
        accountTypeCol.setCellValueFactory(cellData -> cellData.getValue().accountTypeProperty());
        accountTypeCol.setPrefWidth(150);

        TableColumn<AccountBalanceRow, String> balanceCol = new TableColumn<>("Balance (BWP)");
        balanceCol.setCellValueFactory(cellData -> cellData.getValue().balanceProperty());
        balanceCol.setPrefWidth(150);

        balanceTable.getColumns().addAll(accountNumberCol, accountTypeCol, balanceCol);
        balanceTable.setPrefHeight(400);

        // Populate table
        refreshBalances();

        // Refresh button
        Button refreshBtn = new Button("Refresh");
        refreshBtn.setOnAction(e -> refreshBalances());
        refreshBtn.getStyleClass().add("primary-button");

        // Back button
        Button backBtn = new Button("Back to Dashboard");
        backBtn.setOnAction(e -> onBack());
        backBtn.getStyleClass().add("secondary-button");

        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(refreshBtn, backBtn);

        // Layout
        VBox contentBox = new VBox(10);
        contentBox.setPadding(new Insets(15));
        contentBox.getStyleClass().add("panel");
        contentBox.getChildren().addAll(balanceTable);

        getChildren().addAll(title, contentBox, buttonBox);
    }

    private void refreshBalances() {
        balanceTable.getItems().clear();
        for (Account acc : accountController.listAccounts()) {
            Double balance = transactionController.getBalance(acc.getAccountNumber());
            if (balance != null) {
                AccountBalanceRow row = new AccountBalanceRow(
                    acc.getAccountNumber(),
                    acc.getClass().getSimpleName().replace("Account", ""),
                    balance
                );
                balanceTable.getItems().add(row);
            }
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
    private static class AccountBalanceRow {
        private final javafx.beans.property.SimpleStringProperty accountNumber;
        private final javafx.beans.property.SimpleStringProperty accountType;
        private final javafx.beans.property.SimpleStringProperty balance;

        public AccountBalanceRow(String accountNumber, String accountType, double balance) {
            this.accountNumber = new javafx.beans.property.SimpleStringProperty(accountNumber);
            this.accountType = new javafx.beans.property.SimpleStringProperty(accountType);
            this.balance = new javafx.beans.property.SimpleStringProperty(String.format("%.2f", balance));
        }

        public javafx.beans.property.StringProperty accountNumberProperty() { return accountNumber; }
        public javafx.beans.property.StringProperty accountTypeProperty() { return accountType; }
        public javafx.beans.property.StringProperty balanceProperty() { return balance; }
    }
}

