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
 * View for withdrawing money from an account.
 * Note: Savings accounts do not allow withdrawals.
 */
public class WithdrawView extends VBox {
    private final AccountController accountController;
    private final TransactionController transactionController;
    private final ComboBox<String> accountCombo = new ComboBox<>();
    private final TextField amountField = new TextField();
    private final Label balanceLabel = new Label();
    private final Label messageLabel = new Label();

    public WithdrawView(AccountController accountController, TransactionController transactionController) {
        this.accountController = accountController;
        this.transactionController = transactionController;
        buildView();
    }

    private void buildView() {
        setPadding(new Insets(20));
        setSpacing(15);

        // Title
        Label title = new Label("Withdraw Money");
        title.setFont(Font.font("System", FontWeight.BOLD, 20));
        title.getStyleClass().add("title");

        // Account selection (only withdrawable accounts)
        Label accountLabel = new Label("Select Account:");
        for (Account acc : accountController.listAccounts()) {
            // Only show accounts that allow withdrawals
            if (acc instanceof com.bankingsystem.Withdrawable) {
                String accountInfo = String.format("%s - %s (BWP %.2f)",
                    acc.getAccountNumber(),
                    acc.getClass().getSimpleName().replace("Account", ""),
                    acc.getBalance());
                accountCombo.getItems().add(accountInfo);
            }
        }
        accountCombo.setPrefWidth(400);
        accountCombo.setOnAction(e -> updateBalanceDisplay());

        if (accountCombo.getItems().isEmpty()) {
            Label noAccountsLabel = new Label("No withdrawable accounts available. Savings accounts do not allow withdrawals.");
            noAccountsLabel.getStyleClass().add("info-label");
            getChildren().addAll(title, noAccountsLabel);
            return;
        }

        // Current balance display
        balanceLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        balanceLabel.getStyleClass().add("info-label");

        // Amount input
        Label amountLabel = new Label("Withdrawal Amount (BWP):");
        amountField.setPromptText("Enter amount to withdraw");
        amountField.setPrefWidth(200);

        // Buttons
        Button withdrawBtn = new Button("Withdraw");
        withdrawBtn.setOnAction(e -> onWithdraw());
        withdrawBtn.getStyleClass().add("primary-button");

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setOnAction(e -> onCancel());
        cancelBtn.getStyleClass().add("secondary-button");

        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(withdrawBtn, cancelBtn);

        // Message label
        messageLabel.setWrapText(true);
        messageLabel.getStyleClass().add("info-label");

        // Layout
        VBox formBox = new VBox(10);
        formBox.setPadding(new Insets(15));
        formBox.getStyleClass().add("panel");
        formBox.getChildren().addAll(
            accountLabel, accountCombo,
            balanceLabel,
            amountLabel, amountField
        );

        getChildren().addAll(title, formBox, buttonBox, messageLabel);
        
        // Update balance when account is selected
        updateBalanceDisplay();
    }

    private void updateBalanceDisplay() {
        String selected = accountCombo.getValue();
        if (selected != null && !selected.isEmpty()) {
            String accountNumber = selected.split(" - ")[0];
            Double balance = transactionController.getBalance(accountNumber);
            if (balance != null) {
                balanceLabel.setText(String.format("Current Balance: BWP %.2f", balance));
            }
        } else {
            balanceLabel.setText("Select an account to view balance");
        }
    }

    private void onWithdraw() {
        String selected = accountCombo.getValue();
        if (selected == null || selected.isEmpty()) {
            messageLabel.setText("Please select an account");
            return;
        }

        String accountNumber = selected.split(" - ")[0];
        String amountText = amountField.getText();
        
        if (amountText == null || amountText.trim().isEmpty()) {
            messageLabel.setText("Please enter a withdrawal amount");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            messageLabel.setText("Invalid amount. Please enter a valid number.");
            return;
        }

        if (amount <= 0) {
            messageLabel.setText("Withdrawal amount must be greater than zero");
            return;
        }

        // Perform withdrawal via TransactionController
        String result = transactionController.withdraw(accountNumber, amount);
        messageLabel.setText(result);

        // Update balance display and clear amount field on success
        if (result.contains("Successfully")) {
            updateBalanceDisplay();
            amountField.clear();
            // Refresh account combo
            accountCombo.getItems().clear();
            for (Account acc : accountController.listAccounts()) {
                if (acc instanceof com.bankingsystem.Withdrawable) {
                    String accountInfo = String.format("%s - %s (BWP %.2f)",
                        acc.getAccountNumber(),
                        acc.getClass().getSimpleName().replace("Account", ""),
                        acc.getBalance());
                    accountCombo.getItems().add(accountInfo);
                }
            }
            if (accountCombo.getItems().contains(selected)) {
                accountCombo.setValue(selected);
            }
        }
    }

    private void onCancel() {
        // Navigate back to dashboard
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Cancel");
        alert.setContentText("Returning to dashboard...");
        alert.show();
    }
}

