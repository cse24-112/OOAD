package com.bankingsystem.mvc.view;

import com.bankingsystem.mvc.controller.AccountController;
import com.bankingsystem.mvc.model.Account;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class CustomerDashboardView extends BorderPane {
    private final AccountController controller;
    private final ListView<String> accountList = new ListView<>();
    private final Label info = new Label();

    public CustomerDashboardView(AccountController controller) {
        this.controller = controller;
        setPadding(new Insets(12));
        VBox left = new VBox(8);
        left.setPadding(new Insets(8));
        Button refresh = new Button("Refresh Accounts");
        Button deposit = new Button("Deposit");
        Button withdraw = new Button("Withdraw");
        refresh.setOnAction(e -> refresh());
        deposit.setOnAction(e -> onDeposit());
        withdraw.setOnAction(e -> onWithdraw());
        left.getChildren().addAll(refresh, deposit, withdraw, info);
        setLeft(left);
        setCenter(accountList);
        refresh();
    }

    private void refresh() {
        accountList.getItems().clear();
        List<Account> accts = controller.listAccountsByCustomer("CURRENT_CUSTOMER");
        for (Account a : accts) {
            accountList.getItems().add(a.getAccountNumber() + " - " + a.getAccountType() + " - " + a.getBalance());
        }
    }

    private void onDeposit() {
        String sel = accountList.getSelectionModel().getSelectedItem();
        if (sel == null) { info.setText("Select account"); return; }
        String accNum = sel.split(" - ")[0];
        TextInputDialog d = new TextInputDialog(); d.setHeaderText("Amount to deposit");
        d.showAndWait().ifPresent(s -> {
            try {
                double amt = Double.parseDouble(s);
                String res = controller.deposit(accNum, amt);
                info.setText(res);
            } catch (NumberFormatException ex) { info.setText("Invalid number"); }
        });
        refresh();
    }

    private void onWithdraw() {
        String sel = accountList.getSelectionModel().getSelectedItem();
        if (sel == null) { info.setText("Select account"); return; }
        String accNum = sel.split(" - ")[0];
        TextInputDialog d = new TextInputDialog(); d.setHeaderText("Amount to withdraw");
        d.showAndWait().ifPresent(s -> {
            try {
                double amt = Double.parseDouble(s);
                String res = controller.withdraw(accNum, amt);
                info.setText(res);
            } catch (NumberFormatException ex) { info.setText("Invalid number"); }
        });
        refresh();
    }
}
