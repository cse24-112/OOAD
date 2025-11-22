package com.bankingsystem.mvc.view;

import com.bankingsystem.mvc.controller.AccountController;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class DepositView extends VBox {
    private final AccountController controller;
    private final ComboBox<String> accountBox = new ComboBox<>();
    private final TextField amountField = new TextField();
    private final Label info = new Label();

    public DepositView(AccountController controller) {
        this.controller = controller;
        setPadding(new Insets(12));
        setSpacing(8);
        accountBox.setPromptText("Select account number");
        amountField.setPromptText("Amount to deposit");
        Button confirm = new Button("Confirm");
        confirm.setOnAction(e -> onConfirm());
        getChildren().addAll(new Label("Deposit"), accountBox, amountField, confirm, info);
    }

    public void setAccounts(java.util.List<String> accounts) {
        accountBox.setItems(javafx.collections.FXCollections.observableArrayList(accounts));
    }

    private void onConfirm() {
        String acc = accountBox.getValue();
        if (acc == null) { info.setText("Select account"); return; }
        try {
            double amt = Double.parseDouble(amountField.getText());
            String res = controller.deposit(acc, amt);
            info.setText(res);
        } catch (NumberFormatException ex) { info.setText("Invalid amount"); }
    }
}
