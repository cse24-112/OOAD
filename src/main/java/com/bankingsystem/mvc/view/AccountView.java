package com.bankingsystem.mvc.view;

import com.bankingsystem.mvc.model.Account;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class AccountView extends VBox {
    private final Account account;

    public AccountView(Account account) {
        this.account = account;
        setPadding(new Insets(12));
        setSpacing(8);
        Label num = new Label("Account: " + account.getAccountNumber());
        Label type = new Label("Type: " + account.getAccountType());
        Label branch = new Label("Branch: " + account.getBranch());
        Label bal = new Label("Balance: " + account.getBalance());
        getChildren().addAll(num, type, branch, bal);
    }
}
