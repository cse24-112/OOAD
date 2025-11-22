package com.bankingsystem.view;

import com.bankingsystem.Account;
import com.bankingsystem.Transaction;
import com.bankingsystem.controller.AccountController;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Boundary class: AccountView
 * Displays account information and collects user input for account operations.
 * All actions are delegated to AccountController; no business logic here.
 */
public class AccountView extends BorderPane {
    private final AccountController controller;
    private final ListView<String> accountsList = new ListView<>();
    private final ListView<String> transactionsList = new ListView<>();
    private final TextField amountField = new TextField();
    private final Label infoLabel = new Label();

    public AccountView(AccountController controller) {
        this.controller = controller;
        setPadding(new Insets(6));

        Label title = new Label("Account Dashboard");
        title.setStyle("-fx-font-size:16px; -fx-font-weight:bold;");

        // Left: accounts list
        VBox left = new VBox(6, new Label("Accounts"), accountsList);
        left.setPrefWidth(260);
        VBox.setVgrow(accountsList, javafx.scene.layout.Priority.ALWAYS);

        // Right: transactions list
        VBox right = new VBox(6, new Label("Transactions"), transactionsList);
        right.setPrefWidth(360);
        VBox.setVgrow(transactionsList, javafx.scene.layout.Priority.ALWAYS);

        // Center: actions - arranged compactly
        amountField.setPromptText("Amount");
        amountField.setPrefWidth(160);
        HBox.setHgrow(amountField, javafx.scene.layout.Priority.ALWAYS);

        Button deposit = new Button("Deposit");
        Button withdraw = new Button("Withdraw");
        Button applyInterest = new Button("Apply Interest");
        Button openAccountBtn = new Button("Open Account");

        deposit.setOnAction(e -> onDeposit());
        withdraw.setOnAction(e -> onWithdraw());
        applyInterest.setOnAction(e -> onApplyInterest());
        openAccountBtn.setOnAction(e -> onOpenAccount());

        HBox row1 = new HBox(8, amountField, deposit, withdraw);
        HBox row2 = new HBox(8, applyInterest, openAccountBtn);
        VBox actions = new VBox(10, row1, row2, infoLabel);
        VBox.setVgrow(actions, javafx.scene.layout.Priority.ALWAYS);

        setTop(title);
        setLeft(left);
        setCenter(actions);
        setRight(right);
        setBottom(infoLabel);

        refreshAccounts();
    }

    public void refreshAccounts() {
        accountsList.getItems().clear();
        for (Account a : controller.listAccounts()) {
            accountsList.getItems().add(a.getAccountNumber() + " - " + a.getClass().getSimpleName() + " : " + String.format("%.2f", a.getBalance()));
        }
    }

    private void refreshTransactionsFor(String accountNumber) {
        transactionsList.getItems().clear();
        Account a = controller.listAccounts().stream().filter(ac -> ac.getAccountNumber().equals(accountNumber)).findFirst().orElse(null);
        if (a == null) return;
        for (Transaction t : a.getTransactions()) {
            transactionsList.getItems().add(t.toString());
        }
    }

    private void onDeposit() {
        String sel = accountsList.getSelectionModel().getSelectedItem();
        if (sel == null) {
            infoLabel.setText("Select an account first");
            return;
        }
        double amount;
        try { amount = Double.parseDouble(amountField.getText()); } catch (Exception ex) { infoLabel.setText("Enter a valid amount"); return; }
        String res = controller.depositTo(sel.split(" - ")[0], amount);
        infoLabel.setText(res);
        refreshAccounts();
        refreshTransactionsFor(sel.split(" - ")[0]);
    }

    private void onWithdraw() {
        String sel = accountsList.getSelectionModel().getSelectedItem();
        if (sel == null) { infoLabel.setText("Select an account first"); return; }
        double amount;
        try { amount = Double.parseDouble(amountField.getText()); } catch (Exception ex) { infoLabel.setText("Enter a valid amount"); return; }
        String res = controller.withdrawFrom(sel.split(" - ")[0], amount);
        infoLabel.setText(res);
        refreshAccounts();
        refreshTransactionsFor(sel.split(" - ")[0]);
    }

    private void onApplyInterest() {
        controller.applyInterest();
        infoLabel.setText("Interest applied where applicable");
        refreshAccounts();
        // refresh transactions for selected account if any
        String sel = accountsList.getSelectionModel().getSelectedItem();
        if (sel != null) refreshTransactionsFor(sel.split(" - ")[0]);
    }

    // Add handler to open a new account
    private void onOpenAccount() {
        // simple dialog
        TextInputDialog dlg = new TextInputDialog();
        dlg.setHeaderText("Open Account");
        dlg.setContentText("Enter type and initial deposit (e.g. savings,1000):");
        java.util.Optional<String> res = dlg.showAndWait();
        if (res.isPresent()) {
            String[] parts = res.get().split(",");
            if (parts.length < 2) { infoLabel.setText("Invalid input"); return; }
            String type = parts[0].trim();
            double amt;
            try { amt = Double.parseDouble(parts[1].trim()); } catch (Exception ex) { infoLabel.setText("Invalid amount"); return; }
            String msg = controller.openAccount(type, amt);
            infoLabel.setText(msg);
            refreshAccounts();
        }
    }
}
