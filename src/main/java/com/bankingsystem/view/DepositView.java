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
 * View for depositing money into an account.
 * Displays account selection and deposit amount input.
 */
public class DepositView extends VBox {
    private final AccountController accountController;
    private final TransactionController transactionController;
    private final ComboBox<String> accountCombo = new ComboBox<>();
    private final TextField amountField = new TextField();
    private final Label balanceLabel = new Label();
    private final Label messageLabel = new Label();

    public DepositView(AccountController accountController, TransactionController transactionController) {
        this.accountController = accountController;
        this.transactionController = transactionController;
        buildView();
    }

    private void buildView() {
        setPadding(new Insets(20));
        setSpacing(15);

        // Title
        Label title = new Label("Deposit Money");
        title.setFont(Font.font("System", FontWeight.BOLD, 20));
        title.getStyleClass().add("title");

        // Account selection
        Label accountLabel = new Label("Select Account:");
        for (Account acc : accountController.listAccounts()) {
            String accountInfo = String.format("%s - %s (BWP %.2f)",
                acc.getAccountNumber(),
                acc.getClass().getSimpleName().replace("Account", ""),
                acc.getBalance());
            accountCombo.getItems().add(accountInfo);
        }
        accountCombo.setPrefWidth(400);
        accountCombo.setOnAction(e -> updateBalanceDisplay());

        // Current balance display
        balanceLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        balanceLabel.getStyleClass().add("info-label");

        // Amount input
        Label amountLabel = new Label("Deposit Amount (BWP):");
        amountField.setPromptText("Enter amount to deposit");
        amountField.setPrefWidth(200);

        // Buttons
        Button depositBtn = new Button("Deposit");
        depositBtn.setOnAction(e -> onDeposit());
        depositBtn.getStyleClass().add("primary-button");

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setOnAction(e -> onCancel());
        cancelBtn.getStyleClass().add("secondary-button");

        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(depositBtn, cancelBtn);

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
            // Extract account number from selection
            String accountNumber = selected.split(" - ")[0];
            Double balance = transactionController.getBalance(accountNumber);
            if (balance != null) {
                balanceLabel.setText(String.format("Current Balance: BWP %.2f", balance));
            }
        } else {
            balanceLabel.setText("Select an account to view balance");
        }
    }

    private void onDeposit() {
        String selected = accountCombo.getValue();
        if (selected == null || selected.isEmpty()) {
            messageLabel.setText("Please select an account");
            return;
        }

        String accountNumber = selected.split(" - ")[0];
        String amountText = amountField.getText();
        
        if (amountText == null || amountText.trim().isEmpty()) {
            messageLabel.setText("Please enter a deposit amount");
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
            messageLabel.setText("Deposit amount must be greater than zero");
            return;
        }

        // Perform deposit via TransactionController
        String result = transactionController.deposit(accountNumber, amount);
        messageLabel.setText(result);

        // Update balance display and clear amount field on success
        if (result.contains("Successfully")) {
            updateBalanceDisplay();
            amountField.clear();
            // Refresh account combo
            accountCombo.getItems().clear();
            for (Account acc : accountController.listAccounts()) {
                String accountInfo = String.format("%s - %s (BWP %.2f)",
                    acc.getAccountNumber(),
                    acc.getClass().getSimpleName().replace("Account", ""),
                    acc.getBalance());
                accountCombo.getItems().add(accountInfo);
            }
            accountCombo.setValue(selected);
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

