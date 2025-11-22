package com.bankingsystem.mvc.view;

import com.bankingsystem.mvc.controller.StaffController;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class StaffDashboardView extends BorderPane {
    private final StaffController controller;

    public StaffDashboardView(StaffController controller) {
        this.controller = controller;
        setPadding(new Insets(12));
        Label title = new Label("Staff Dashboard");
        title.getStyleClass().add("title");
        Button createCustomer = new Button("Create Customer");
        Button createAccount = new Button("Create Account");
        Button listCustomers = new Button("List Customers");
        createCustomer.setOnAction(e -> onCreateCustomer());
        createAccount.setOnAction(e -> onCreateAccount());
        listCustomers.setOnAction(e -> onListCustomers());
        HBox top = new HBox(8, createCustomer, createAccount, listCustomers);
        top.setPadding(new Insets(8));
        setTop(top);
    }

    private void onCreateCustomer() {
        Dialog<ButtonType> dlg = new Dialog<>();
        dlg.setTitle("Create Customer");
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        VBox v = new VBox(8);
        TextField id = new TextField(); id.setPromptText("Customer ID");
        TextField fn = new TextField(); fn.setPromptText("First name");
        TextField sn = new TextField(); sn.setPromptText("Surname");
        TextField email = new TextField(); email.setPromptText("Email");
        TextField phone = new TextField(); phone.setPromptText("Phone");
        v.getChildren().addAll(id, fn, sn, email, phone);
        dlg.getDialogPane().setContent(v);
        dlg.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.OK) {
                com.bankingsystem.mvc.model.Customer c = new com.bankingsystem.mvc.model.Customer(id.getText(), fn.getText(), sn.getText(), "", email.getText(), phone.getText(), "INDIVIDUAL", "password");
                String res = controller.createCustomer(c);
                Alert a = new Alert(Alert.AlertType.INFORMATION, res);
                a.showAndWait();
            }
        });
    }

    private void onCreateAccount() {
        Dialog<ButtonType> dlg = new Dialog<>();
        dlg.setTitle("Create Account");
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        VBox v = new VBox(8);
        TextField cust = new TextField(); cust.setPromptText("Customer ID");
        ComboBox<String> type = new ComboBox<>(); type.getItems().addAll("SAVINGS","INVESTMENT","CHEQUE"); type.setValue("SAVINGS");
        TextField balance = new TextField(); balance.setPromptText("Initial deposit");
        TextField emp = new TextField(); emp.setPromptText("Employer (for cheque)");
        TextField empAddr = new TextField(); empAddr.setPromptText("Employer address");
        v.getChildren().addAll(cust, type, balance, emp, empAddr);
        dlg.getDialogPane().setContent(v);
        dlg.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.OK) {
                String cid = cust.getText();
                double amt = Double.parseDouble(balance.getText());
                com.bankingsystem.mvc.model.Account a;
                switch (type.getValue()) {
                    case "SAVINGS": a = new com.bankingsystem.mvc.model.SavingsAccount(cid, amt, "Main"); break;
                    case "INVESTMENT": a = new com.bankingsystem.mvc.model.InvestmentAccount(cid, amt, "Main"); break;
                    default: a = new com.bankingsystem.mvc.model.ChequeAccount(cid, amt, "Main", emp.getText(), empAddr.getText()); break;
                }
                String res = controller.createAccount(a);
                Alert al = new Alert(Alert.AlertType.INFORMATION, res);
                al.showAndWait();
            }
        });
    }

    private void onListCustomers() {
        try {
            com.bankingsystem.mvc.dao.CustomerDAO dao = new com.bankingsystem.mvc.dao.CustomerDAO();
            java.util.List<com.bankingsystem.mvc.model.Customer> list = dao.listAll();
            StringBuilder sb = new StringBuilder();
            for (com.bankingsystem.mvc.model.Customer c : list) sb.append(c.getCustomerId()).append(" - ").append(c.getFirstname()).append(" ").append(c.getSurname()).append("\n");
            Alert a = new Alert(Alert.AlertType.INFORMATION, sb.toString()); a.setHeaderText("Customers"); a.showAndWait();
        } catch (Exception ex) { ex.printStackTrace(); }
    }
}
