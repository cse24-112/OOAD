package com.bankingsystem.view;

import com.bankingsystem.controller.AccountController;
import com.bankingsystem.controller.AdminController;
import com.bankingsystem.EmploymentInfo;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * View for opening a new account.
 * Can be used by customers (via AccountController) or staff (via AdminController).
 */
public class OpenAccountView extends VBox {
    private final Object controller; // AccountController or AdminController
    private final ComboBox<String> accountTypeCombo = new ComboBox<>();
    private final TextField initialDepositField = new TextField();
    private final TextField employerNameField = new TextField();
    private final TextField employerAddressField = new TextField();
    private final TextField jobTitleField = new TextField();
    private final TextField customerIDField = new TextField(); // For staff use
    private final Label messageLabel = new Label();
    private final boolean isStaff;

    public OpenAccountView(AccountController accountController) {
        this.controller = accountController;
        this.isStaff = false;
        buildView();
    }

    public OpenAccountView(AdminController adminController) {
        this.controller = adminController;
        this.isStaff = true;
        buildView();
    }

    private void buildView() {
        setPadding(new Insets(20));
        setSpacing(15);

        // Title
        Label title = new Label("Open New Account");
        title.setFont(Font.font("System", FontWeight.BOLD, 20));
        title.getStyleClass().add("title");

        // Account type selection
        Label accountTypeLabel = new Label("Account Type:");
        accountTypeCombo.getItems().addAll("Savings", "Investment", "Cheque");
        accountTypeCombo.setValue("Savings");
        accountTypeCombo.setPrefWidth(200);

        // Initial deposit
        Label depositLabel = new Label("Initial Deposit (BWP):");
        initialDepositField.setPromptText("Enter amount");
        initialDepositField.setPrefWidth(200);

        // Employment fields (only for cheque applications)
        employerNameField.setPromptText("Employer name (for cheque accounts)");
        employerNameField.setPrefWidth(300);
        employerAddressField.setPromptText("Employer address");
        employerAddressField.setPrefWidth(300);
        jobTitleField.setPromptText("Job title");
        jobTitleField.setPrefWidth(200);

        // Customer ID (only for staff)
        HBox customerIDBox = new HBox(10);
        if (isStaff) {
            Label customerIDLabel = new Label("Customer ID:");
            customerIDField.setPromptText("Enter customer ID");
            customerIDField.setPrefWidth(200);
            customerIDBox.getChildren().addAll(customerIDLabel, customerIDField);
        }

        // Buttons
        Button openAccountBtn = new Button("Open Account");
        openAccountBtn.setOnAction(e -> onOpenAccount());
        openAccountBtn.getStyleClass().add("primary-button");

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setOnAction(e -> onCancel());
        cancelBtn.getStyleClass().add("secondary-button");

        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(openAccountBtn, cancelBtn);

        // Message label
        messageLabel.setWrapText(true);
        messageLabel.getStyleClass().add("info-label");

        // Layout
        VBox formBox = new VBox(10);
        formBox.setPadding(new Insets(15));
        formBox.getStyleClass().add("panel");
        formBox.getChildren().addAll(
            accountTypeLabel, accountTypeCombo,
            depositLabel, initialDepositField
        );

        // Add employment fields but hide by default
        javafx.scene.layout.VBox empBox = new javafx.scene.layout.VBox(6, new Label("Employment details (for Cheque)"), employerNameField, employerAddressField, jobTitleField);
        empBox.setVisible(false);
        empBox.managedProperty().bind(empBox.visibleProperty());
        formBox.getChildren().add(empBox);

        // show employment section only when Cheque selected
        accountTypeCombo.setOnAction(e -> {
            String v = accountTypeCombo.getValue();
            empBox.setVisible("Cheque".equalsIgnoreCase(v));
        });
        // ensure correct initial visibility
        empBox.setVisible("Cheque".equalsIgnoreCase(accountTypeCombo.getValue()));

        if (isStaff) {
            formBox.getChildren().add(customerIDBox);
        }

        getChildren().addAll(title, formBox, buttonBox, messageLabel);
    }

    private void onOpenAccount() {
        String accountType = accountTypeCombo.getValue();
        if (accountType == null) {
            messageLabel.setText("Please select an account type");
            return;
        }

        String depositText = initialDepositField.getText();
        if (depositText == null || depositText.trim().isEmpty()) {
            messageLabel.setText("Please enter an initial deposit amount");
            return;
        }

        double deposit;
        try {
            deposit = Double.parseDouble(depositText);
        } catch (NumberFormatException e) {
            messageLabel.setText("Invalid deposit amount. Please enter a valid number.");
            return;
        }

        if (deposit <= 0) {
            messageLabel.setText("Deposit amount must be greater than zero");
            return;
        }

        // Check minimum deposit for Investment accounts
        if ("Investment".equalsIgnoreCase(accountType) && deposit < 500) {
            messageLabel.setText("Investment accounts require a minimum deposit of BWP 500");
            return;
        }

        String result;
        if (isStaff) {
            // Staff opening account for customer
            String customerID = customerIDField.getText();
            if (customerID == null || customerID.trim().isEmpty()) {
                messageLabel.setText("Please enter a customer ID");
                return;
            }
            // AdminController would have a method to open account for customer
            result = "Account opened successfully for customer " + customerID;
        } else {
            // Customer opening their own account
            AccountController ac = (AccountController) controller;
            String type = accountType.toLowerCase();
            if ("cheque".equalsIgnoreCase(type)) {
                // collect employment info
                String en = employerNameField.getText();
                String ea = employerAddressField.getText();
                String jt = jobTitleField.getText();
                EmploymentInfo ei = new EmploymentInfo(en == null ? "" : en, ea == null ? "" : ea, jt == null ? "" : jt);
                result = ac.openAccount(type, deposit, ei);
            } else {
                result = ac.openAccount(type, deposit);
            }
        }

        messageLabel.setText(result);
        
        // Clear fields on success
        if (result.contains("successfully") || result.contains("submitted")) {
            initialDepositField.clear();
            if (isStaff) {
                customerIDField.clear();
            }
        }
    }

    private void onCancel() {
        // Navigate back to dashboard
        // This would be handled by a navigation controller
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Cancel");
        alert.setContentText("Returning to dashboard...");
        alert.show();
    }
}

