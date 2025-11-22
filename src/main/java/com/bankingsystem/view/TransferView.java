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
 * View for transferring money between accounts.
 */
public class TransferView extends VBox {
    private final AccountController accountController;
    private final TransactionController transactionController;
    private final ComboBox<String> fromAccountCombo = new ComboBox<>();
    private final ComboBox<String> toAccountCombo = new ComboBox<>();
    private final TextField amountField = new TextField();
    private final TextField toAccountNumberField = new TextField(); // For external transfers
    private final RadioButton internalTransferRadio = new RadioButton("Transfer to my account");
    private final RadioButton externalTransferRadio = new RadioButton("Transfer to another account");
    private final Label messageLabel = new Label();

    public TransferView(AccountController accountController, TransactionController transactionController) {
        this.accountController = accountController;
        this.transactionController = transactionController;
        buildView();
    }

    private void buildView() {
        setPadding(new Insets(20));
        setSpacing(15);

        // Title
        Label title = new Label("Transfer Money");
        title.setFont(Font.font("System", FontWeight.BOLD, 20));
        title.getStyleClass().add("title");

        // Transfer type selection
        ToggleGroup transferTypeGroup = new ToggleGroup();
        internalTransferRadio.setToggleGroup(transferTypeGroup);
        externalTransferRadio.setToggleGroup(transferTypeGroup);
        internalTransferRadio.setSelected(true);
        internalTransferRadio.setOnAction(e -> updateTransferType(true));
        externalTransferRadio.setOnAction(e -> updateTransferType(false));

        // From account selection
        Label fromAccountLabel = new Label("From Account:");
        for (Account acc : accountController.listAccounts()) {
            // Only show withdrawable accounts
            if (acc instanceof com.bankingsystem.Withdrawable) {
                String accountInfo = String.format("%s - %s (BWP %.2f)",
                    acc.getAccountNumber(),
                    acc.getClass().getSimpleName().replace("Account", ""),
                    acc.getBalance());
                fromAccountCombo.getItems().add(accountInfo);
            }
        }
        fromAccountCombo.setPrefWidth(400);

        // To account selection (for internal transfers)
        Label toAccountLabel = new Label("To Account:");
        for (Account acc : accountController.listAccounts()) {
            String accountInfo = String.format("%s - %s (BWP %.2f)",
                acc.getAccountNumber(),
                acc.getClass().getSimpleName().replace("Account", ""),
                acc.getBalance());
            toAccountCombo.getItems().add(accountInfo);
        }
        toAccountCombo.setPrefWidth(400);
        toAccountCombo.setVisible(true);

        // External account number input (for external transfers)
        Label toAccountNumberLabel = new Label("To Account Number:");
        toAccountNumberField.setPromptText("Enter account number");
        toAccountNumberField.setPrefWidth(400);
        toAccountNumberField.setVisible(false);

        // Amount input
        Label amountLabel = new Label("Transfer Amount (BWP):");
        amountField.setPromptText("Enter amount to transfer");
        amountField.setPrefWidth(200);

        // Buttons
        Button transferBtn = new Button("Transfer");
        transferBtn.setOnAction(e -> onTransfer());
        transferBtn.getStyleClass().add("primary-button");

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setOnAction(e -> onCancel());
        cancelBtn.getStyleClass().add("secondary-button");

        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(transferBtn, cancelBtn);

        // Message label
        messageLabel.setWrapText(true);
        messageLabel.getStyleClass().add("info-label");

        // Layout
        VBox formBox = new VBox(10);
        formBox.setPadding(new Insets(15));
        formBox.getStyleClass().add("panel");
        formBox.getChildren().addAll(
            internalTransferRadio, externalTransferRadio,
            fromAccountLabel, fromAccountCombo,
            toAccountLabel, toAccountCombo,
            toAccountNumberLabel, toAccountNumberField,
            amountLabel, amountField
        );

        getChildren().addAll(title, formBox, buttonBox, messageLabel);
    }

    private void updateTransferType(boolean isInternal) {
        toAccountCombo.setVisible(isInternal);
        toAccountNumberField.setVisible(!isInternal);
    }

    private void onTransfer() {
        String fromSelected = fromAccountCombo.getValue();
        if (fromSelected == null || fromSelected.isEmpty()) {
            messageLabel.setText("Please select a source account");
            return;
        }

        String fromAccountNumber = fromSelected.split(" - ")[0];
        String toAccountNumber;

        if (internalTransferRadio.isSelected()) {
            String toSelected = toAccountCombo.getValue();
            if (toSelected == null || toSelected.isEmpty()) {
                messageLabel.setText("Please select a destination account");
                return;
            }
            toAccountNumber = toSelected.split(" - ")[0];
        } else {
            toAccountNumber = toAccountNumberField.getText();
            if (toAccountNumber == null || toAccountNumber.trim().isEmpty()) {
                messageLabel.setText("Please enter a destination account number");
                return;
            }
        }

        if (fromAccountNumber.equals(toAccountNumber)) {
            messageLabel.setText("Cannot transfer to the same account");
            return;
        }

        String amountText = amountField.getText();
        if (amountText == null || amountText.trim().isEmpty()) {
            messageLabel.setText("Please enter a transfer amount");
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
            messageLabel.setText("Transfer amount must be greater than zero");
            return;
        }

        // Perform transfer via TransactionController
        String result = transactionController.transfer(fromAccountNumber, toAccountNumber, amount);
        messageLabel.setText(result);

        // Clear amount field on success
        if (result.contains("Successfully")) {
            amountField.clear();
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

