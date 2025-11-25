package com.bankingsystem.mvc.view;

import com.bankingsystem.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Registration form for new customers to create accounts.
 * Supports individual and company account registration.
 * Allows account type selection and displays employment info for Cheque accounts.
 */
public class NewAccountRegistrationView extends BorderPane {
    private final Bank bank;
    private final Stage stage;
    private final Label messageLabel = new Label();
    private final TabPane tabPane = new TabPane();
    private boolean registrationSuccessful = false;

    public NewAccountRegistrationView(Bank bank, Stage stage) {
        this.bank = bank;
        this.stage = stage;
        initializeUI();
    }

    private void initializeUI() {
        setPadding(new Insets(15));
        setStyle("-fx-font-size: 11;");

        // Top: Title
        VBox topBox = new VBox(10);
        topBox.setPadding(new Insets(0, 0, 15, 0));
        Label titleLabel = new Label("Create New Account");
        titleLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #1a5490;");
        Label descLabel = new Label("Register as Individual or Company to open an account");
        descLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #666;");
        topBox.getChildren().addAll(titleLabel, descLabel);
        setTop(topBox);

        // Center: Tabs for Individual and Company registration
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.getTabs().addAll(
            createIndividualRegistrationTab(),
            createCompanyRegistrationTab()
        );
        setCenter(tabPane);

        // Bottom: Message and buttons
        VBox bottomBox = new VBox(10);
        bottomBox.setPadding(new Insets(15, 0, 0, 0));
        messageLabel.setStyle("-fx-text-fill: #cc0000; -fx-font-size: 11;");
        messageLabel.setWrapText(true);

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        Button backButton = new Button("Back to Login");
        backButton.setPrefWidth(120);
        backButton.setStyle("-fx-padding: 8; -fx-font-size: 11;");
        backButton.setOnAction(e -> handleBackToLogin());
        buttonBox.getChildren().add(backButton);

        bottomBox.getChildren().addAll(messageLabel, buttonBox);
        setBottom(bottomBox);
    }

    private Tab createIndividualRegistrationTab() {
        Tab tab = new Tab();
        tab.setText("Individual Registration");

        VBox formBox = new VBox(10);
        formBox.setPadding(new Insets(20));
        formBox.setStyle("-fx-border-color: #ddd; -fx-border-radius: 5;");

        // Personal Information Section
        Label personalLabel = new Label("Personal Information");
        personalLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13;");

        TextField firstNameField = new TextField();
        firstNameField.setPromptText("First Name");
        firstNameField.setPrefWidth(300);

        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Last Name");
        lastNameField.setPrefWidth(300);

        TextField nationalIDField = new TextField();
        nationalIDField.setPromptText("National ID (Omang/Passport)");
        nationalIDField.setPrefWidth(300);

        // Login Credentials Section
        Label credentialsLabel = new Label("Login Credentials");
        credentialsLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13; -fx-padding: 10 0 0 0;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setPrefWidth(300);

        PasswordField pinField = new PasswordField();
        pinField.setPromptText("PIN (4-6 digits)");
        pinField.setPrefWidth(300);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password (6+ characters, optional if using PIN)");
        passwordField.setPrefWidth(300);

        // Account Information Section
        Label accountLabel = new Label("Account Information");
        accountLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13; -fx-padding: 10 0 0 0;");

        ComboBox<String> accountTypeCombo = new ComboBox<>();
        accountTypeCombo.getItems().addAll("Savings", "Investment", "Cheque");
        accountTypeCombo.setValue("Savings");
        accountTypeCombo.setPrefWidth(300);
        accountTypeCombo.setStyle("-fx-padding: 5;");

        Spinner<Double> depositSpinner = new Spinner<>(0.0, 100000.0, 100.0, 100.0);
        depositSpinner.setPrefWidth(300);
        Label depositLabel = new Label("Initial Deposit (P):");

        // Employment Information Section (for Cheque accounts)
        VBox employmentBox = new VBox(8);
        employmentBox.setStyle("-fx-border-color: #ddd; -fx-border-radius: 3; -fx-padding: 10; -fx-background-color: #f9f9f9;");
        Label employmentLabel = new Label("Employment Information (Required for Cheque Account)");
        employmentLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12;");

        TextField employerNameField = new TextField();
        employerNameField.setPromptText("Employer Name");
        employerNameField.setPrefWidth(300);

        TextField employerAddressField = new TextField();
        employerAddressField.setPromptText("Employer Address");
        employerAddressField.setPrefWidth(300);

        TextField jobTitleField = new TextField();
        jobTitleField.setPromptText("Job Title");
        jobTitleField.setPrefWidth(300);

        ComboBox<String> employmentTypeCombo = new ComboBox<>();
        employmentTypeCombo.getItems().addAll("Employee", "Self-employed", "Contract Worker");
        employmentTypeCombo.setValue("Employee");
        employmentTypeCombo.setPrefWidth(300);
        employmentTypeCombo.setStyle("-fx-padding: 5;");

        employmentBox.getChildren().addAll(employmentLabel, employerNameField, employerAddressField,
            jobTitleField, employmentTypeCombo);
        employmentBox.setVisible(false); // Hidden by default

        // Show/hide employment info based on account type
        accountTypeCombo.setOnAction(e -> {
            boolean isCheque = "Cheque".equals(accountTypeCombo.getValue());
            employmentBox.setVisible(isCheque);
            employmentBox.setManaged(isCheque);
        });

        // Register button
        Button registerButton = new Button("Register & Create Account");
        registerButton.setPrefWidth(200);
        registerButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 10; -fx-font-size: 12;");
        registerButton.setOnAction(e -> handleIndividualRegistration(
            firstNameField.getText(),
            lastNameField.getText(),
            nationalIDField.getText(),
            usernameField.getText(),
            pinField.getText(),
            passwordField.getText(),
            accountTypeCombo.getValue(),
            depositSpinner.getValue(),
            employerNameField.getText(),
            employerAddressField.getText(),
            jobTitleField.getText(),
            employmentTypeCombo.getValue()
        ));

        // Add all components
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);

        VBox scrollContent = new VBox(12);
        scrollContent.setPadding(new Insets(10));
        scrollContent.getChildren().addAll(
            personalLabel, firstNameField, lastNameField, nationalIDField,
            credentialsLabel, usernameField, pinField, passwordField,
            accountLabel, new Label("Account Type:"), accountTypeCombo,
            depositLabel, depositSpinner,
            employmentBox,
            registerButton
        );
        scrollPane.setContent(scrollContent);
        formBox.getChildren().add(scrollPane);

        tab.setContent(formBox);
        return tab;
    }

    private Tab createCompanyRegistrationTab() {
        Tab tab = new Tab();
        tab.setText("Company Registration");

        VBox formBox = new VBox(10);
        formBox.setPadding(new Insets(20));
        formBox.setStyle("-fx-border-color: #ddd; -fx-border-radius: 5;");

        // Company Information Section
        Label companyLabel = new Label("Company Information");
        companyLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13;");

        TextField companyNameField = new TextField();
        companyNameField.setPromptText("Company Name");
        companyNameField.setPrefWidth(300);

        TextField regNumberField = new TextField();
        regNumberField.setPromptText("Registration Number");
        regNumberField.setPrefWidth(300);

        TextField taxIDField = new TextField();
        taxIDField.setPromptText("Tax ID");
        taxIDField.setPrefWidth(300);

        ComboBox<String> industryCombo = new ComboBox<>();
        industryCombo.getItems().addAll(
            "Banking & Finance", "Technology", "Manufacturing", "Retail", "Services",
            "Hospitality", "Healthcare", "Education", "Construction", "Agriculture", "Other"
        );
        industryCombo.setValue("Services");
        industryCombo.setPrefWidth(300);
        industryCombo.setStyle("-fx-padding: 5;");

        Spinner<Integer> employeeSpinner = new Spinner<>(1, 10000, 10);
        employeeSpinner.setPrefWidth(300);

        ComboBox<String> businessTypeCombo = new ComboBox<>();
        businessTypeCombo.getItems().addAll(
            "Manufacturing", "Services", "Retail", "Technology", "Distribution",
            "Trading", "Professional Services", "Other"
        );
        businessTypeCombo.setValue("Services");
        businessTypeCombo.setPrefWidth(300);
        businessTypeCombo.setStyle("-fx-padding: 5;");

        // Contact Information Section
        Label contactLabel = new Label("Contact Information");
        contactLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13; -fx-padding: 10 0 0 0;");

        TextField contactNameField = new TextField();
        contactNameField.setPromptText("Contact Person Name");
        contactNameField.setPrefWidth(300);

        TextField contactEmailField = new TextField();
        contactEmailField.setPromptText("Contact Email");
        contactEmailField.setPrefWidth(300);

        TextField contactPhoneField = new TextField();
        contactPhoneField.setPromptText("Contact Phone");
        contactPhoneField.setPrefWidth(300);

        TextArea addressArea = new TextArea();
        addressArea.setPromptText("Business Address");
        addressArea.setPrefHeight(60);
        addressArea.setPrefWidth(300);
        addressArea.setWrapText(true);

        // Login Credentials Section
        Label credentialsLabel = new Label("Login Credentials");
        credentialsLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13; -fx-padding: 10 0 0 0;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username for Company Account");
        usernameField.setPrefWidth(300);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password (6+ characters)");
        passwordField.setPrefWidth(300);

        // Register button
        Button registerButton = new Button("Register & Create Company Account");
        registerButton.setPrefWidth(250);
        registerButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 10; -fx-font-size: 12;");
        registerButton.setOnAction(e -> handleCompanyRegistration(
            companyNameField.getText(),
            regNumberField.getText(),
            taxIDField.getText(),
            industryCombo.getValue(),
            employeeSpinner.getValue(),
            contactNameField.getText(),
            contactEmailField.getText(),
            contactPhoneField.getText(),
            addressArea.getText(),
            businessTypeCombo.getValue(),
            usernameField.getText(),
            passwordField.getText()
        ));

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);

        VBox scrollContent = new VBox(12);
        scrollContent.setPadding(new Insets(10));
        scrollContent.getChildren().addAll(
            companyLabel, companyNameField, regNumberField, taxIDField,
            new Label("Industry:"), industryCombo,
            new Label("Number of Employees:"), employeeSpinner,
            new Label("Business Type:"), businessTypeCombo,
            contactLabel, contactNameField, contactEmailField, contactPhoneField,
            new Label("Address:"), addressArea,
            credentialsLabel, usernameField, passwordField,
            registerButton
        );
        scrollPane.setContent(scrollContent);
        formBox.getChildren().add(scrollPane);

        tab.setContent(formBox);
        return tab;
    }

    private void handleIndividualRegistration(String firstName, String lastName, String nationalID,
                                             String username, String pin, String password,
                                             String accountType, double initialDeposit,
                                             String employerName, String employerAddress,
                                             String jobTitle, String employmentType) {
        // Validation
        if (firstName.trim().isEmpty() || lastName.trim().isEmpty() || nationalID.trim().isEmpty()) {
            messageLabel.setText("Please fill in all personal information fields");
            messageLabel.setStyle("-fx-text-fill: #cc0000;");
            return;
        }

        if (username.trim().isEmpty()) {
            messageLabel.setText("Please enter a username");
            messageLabel.setStyle("-fx-text-fill: #cc0000;");
            return;
        }

        if (AccountRegistrationService.isUsernameTaken(bank, username)) {
            messageLabel.setText("Username already taken. Please choose another.");
            messageLabel.setStyle("-fx-text-fill: #cc0000;");
            return;
        }

        if (pin.trim().isEmpty() && password.trim().isEmpty()) {
            messageLabel.setText("Please enter either a PIN or password");
            messageLabel.setStyle("-fx-text-fill: #cc0000;");
            return;
        }

        if (!pin.trim().isEmpty() && !AccountRegistrationService.isValidPin(pin)) {
            messageLabel.setText("PIN must be 4-6 digits");
            messageLabel.setStyle("-fx-text-fill: #cc0000;");
            return;
        }

        if (!password.trim().isEmpty() && !AccountRegistrationService.isValidPassword(password)) {
            messageLabel.setText("Password must be at least 6 characters");
            messageLabel.setStyle("-fx-text-fill: #cc0000;");
            return;
        }

        if (!AccountRegistrationService.isValidNationalID(nationalID)) {
            messageLabel.setText("National ID invalid (must be 8-20 characters)");
            messageLabel.setStyle("-fx-text-fill: #cc0000;");
            return;
        }

        if (initialDeposit < 0) {
            messageLabel.setText("Initial deposit must be non-negative");
            messageLabel.setStyle("-fx-text-fill: #cc0000;");
            return;
        }

        // Check employment info for Cheque accounts
        if ("Cheque".equals(accountType)) {
            if (employerName.trim().isEmpty() || jobTitle.trim().isEmpty()) {
                messageLabel.setText("Employment information required for Cheque accounts");
                messageLabel.setStyle("-fx-text-fill: #cc0000;");
                return;
            }
        }

        // Create registration data
        AccountRegistrationData data = new AccountRegistrationData();
        data.setFirstName(firstName);
        data.setLastName(lastName);
        data.setNationalID(nationalID);
        data.setUsername(username);
        data.setPin(pin.isEmpty() ? null : pin);
        data.setPassword(password.isEmpty() ? null : password);
        data.setAccountType(accountType);
        data.setInitialDeposit(initialDeposit);
        data.setEmployerName(employerName);
        data.setEmployerAddress(employerAddress);
        data.setJobTitle(jobTitle);
        data.setEmploymentType(employmentType);

        // Register customer
        IndividualCustomer customer = AccountRegistrationService.registerIndividualCustomer(bank, data);

        if (customer != null) {
            registrationSuccessful = true;
            messageLabel.setText("Registration successful! Your account is pending approval.");
            messageLabel.setStyle("-fx-text-fill: #00aa00;");

            // Show pending approval popup
            showPendingApprovalPopup(customer);

            // Close after brief delay
            new Thread(() -> {
                try {
                    Thread.sleep(3000);
                    javafx.application.Platform.runLater(() -> handleBackToLogin());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        } else {
            messageLabel.setText("Registration failed. Please try again.");
            messageLabel.setStyle("-fx-text-fill: #cc0000;");
        }
    }

    private void handleCompanyRegistration(String companyName, String regNumber, String taxID,
                                          String industry, int employeeCount,
                                          String contactName, String contactEmail, String contactPhone,
                                          String address, String businessType,
                                          String username, String password) {
        // Validation
        if (companyName.trim().isEmpty() || regNumber.trim().isEmpty()) {
            messageLabel.setText("Please fill in company name and registration number");
            messageLabel.setStyle("-fx-text-fill: #cc0000;");
            return;
        }

        if (username.trim().isEmpty()) {
            messageLabel.setText("Please enter a username");
            messageLabel.setStyle("-fx-text-fill: #cc0000;");
            return;
        }

        if (AccountRegistrationService.isUsernameTaken(bank, username)) {
            messageLabel.setText("Username already taken. Please choose another.");
            messageLabel.setStyle("-fx-text-fill: #cc0000;");
            return;
        }

        if (!AccountRegistrationService.isValidPassword(password)) {
            messageLabel.setText("Password must be at least 6 characters");
            messageLabel.setStyle("-fx-text-fill: #cc0000;");
            return;
        }

        // Create company registration
        CompanyRegistration companyReg = new CompanyRegistration(
            companyName, regNumber, taxID, industry, employeeCount,
            contactName, contactEmail, contactPhone, address, businessType
        );

        // Register company
        CompanyCustomer customer = AccountRegistrationService.registerCompanyCustomer(
            bank, companyName, regNumber, username, password, companyReg
        );

        if (customer != null) {
            registrationSuccessful = true;
            messageLabel.setText("Company registration successful! Your account is pending approval.");
            messageLabel.setStyle("-fx-text-fill: #00aa00;");

            // Show pending approval popup
            showCompanyPendingApprovalPopup(customer);

            // Close after brief delay
            new Thread(() -> {
                try {
                    Thread.sleep(3000);
                    javafx.application.Platform.runLater(() -> handleBackToLogin());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        } else {
            messageLabel.setText("Company registration failed. Please try again.");
            messageLabel.setStyle("-fx-text-fill: #cc0000;");
        }
    }

    private void showPendingApprovalPopup(IndividualCustomer customer) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Account Pending Approval");
        alert.setHeaderText("Registration Complete!");
        alert.setContentText(
            "Dear " + customer.getFirstName() + " " + customer.getLastName() + ",\n\n" +
            "Your account has been successfully created and is now pending approval.\n\n" +
            "Approval Timeline: Typically within 3 working days\n\n" +
            "Once approved, you will receive a notification with your account number.\n" +
            "You can then use that account number to manage your funds.\n\n" +
            "Thank you for registering with our banking system!"
        );
        alert.showAndWait();
    }

    private void showCompanyPendingApprovalPopup(CompanyCustomer customer) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Company Account Pending Approval");
        alert.setHeaderText("Company Registration Complete!");
        alert.setContentText(
            "Dear " + customer.getCompanyName() + ",\n\n" +
            "Your company account has been successfully created and is now pending approval.\n\n" +
            "Our staff will review your company information within 3 working days.\n\n" +
            "What happens next:\n" +
            "1. Our staff reviews your company details\n" +
            "2. We verify your registration and tax information\n" +
            "3. Upon approval, you'll receive notification with your account number\n\n" +
            "Thank you for choosing us for your banking needs!"
        );
        alert.showAndWait();
    }

    private void handleBackToLogin() {
        if (stage != null) {
            stage.close();
        }
    }

    public boolean isRegistrationSuccessful() {
        return registrationSuccessful;
    }
}
