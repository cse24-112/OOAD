package com.bankingsystem.mvc.view;

import com.bankingsystem.mvc.controller.LoginController;
import com.bankingsystem.mvc.controller.LoginResult;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Login view supporting both customer and staff authentication
 * Customers: username + PIN (or password)
 * Staff: username + password
 */
public class LoginView extends VBox {
    private final LoginController controller;
    private final TextField usernameField = new TextField();
    private final PasswordField credentialField = new PasswordField();
    private final ComboBox<String> userTypeCombo = new ComboBox<>();
    private final Label messageLabel = new Label();
    private final java.util.function.Consumer<LoginResult> onSuccess;
    private final Runnable onRegisterClick;

    public LoginView(LoginController controller, java.util.function.Consumer<LoginResult> onSuccess, 
                    Runnable onRegisterClick) {
        this.controller = controller;
        this.onSuccess = onSuccess;
        this.onRegisterClick = onRegisterClick;
        initializeUI();
    }

    private void initializeUI() {
        setPadding(new Insets(30));
        setSpacing(15);
        setStyle("-fx-font-size: 12;");
        
        // Title
        Label titleLabel = new Label("Banking System - Login");
        titleLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: #1a5490;");
        
        // User type selection
        Label userTypeLabel = new Label("Login As:");
        userTypeCombo.getItems().addAll("Customer", "Staff");
        userTypeCombo.setValue("Customer");
        userTypeCombo.setPrefWidth(200);
        
        // Username/email field
        usernameField.setPromptText("Enter username");
        usernameField.setPrefWidth(250);
        Label usernameLabel = new Label("Username:");
        
        // Credential field (PIN or password)
        credentialField.setPromptText("Enter PIN or password");
        credentialField.setPrefWidth(250);
        Label credentialLabel = new Label("PIN/Password:");
        
        // Login button
        Button loginButton = new Button("Login");
        loginButton.setPrefWidth(100);
        loginButton.setStyle("-fx-font-size: 14; -fx-padding: 10;");
        loginButton.setOnAction(e -> handleLogin());
        
        // Clear button
        Button clearButton = new Button("Clear");
        clearButton.setPrefWidth(100);
        clearButton.setStyle("-fx-font-size: 14; -fx-padding: 10;");
        clearButton.setOnAction(e -> clearFields());
        
        // Create Account button
        Button createAccountButton = new Button("Create Account");
        createAccountButton.setPrefWidth(120);
        createAccountButton.setStyle("-fx-font-size: 14; -fx-padding: 10; -fx-background-color: #2196F3; -fx-text-fill: white;");
        createAccountButton.setOnAction(e -> handleCreateAccount());
        
        // Message label
        messageLabel.setStyle("-fx-text-fill: #cc0000; -fx-font-size: 12;");
        messageLabel.setWrapText(true);
        
        // Layout
        VBox userTypeBox = new VBox(5, userTypeLabel, userTypeCombo);
        VBox usernameBox = new VBox(5, usernameLabel, usernameField);
        VBox credentialBox = new VBox(5, credentialLabel, credentialField);
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(loginButton, clearButton, createAccountButton);
        
        getChildren().addAll(
            titleLabel,
            new Separator(),
            userTypeBox,
            usernameBox,
            credentialBox,
            buttonBox,
            messageLabel
        );
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String credential = credentialField.getText().trim();
        String userType = userTypeCombo.getValue();
        
        if (username.isEmpty()) {
            messageLabel.setText("Please enter username");
            return;
        }
        
        if (credential.isEmpty()) {
            messageLabel.setText("Please enter PIN or password");
            return;
        }
        
        LoginResult result = controller.login(username, credential, userType);
        
        if (result != null) {
            messageLabel.setText("Login successful!");
            messageLabel.setStyle("-fx-text-fill: #00aa00;");
            if (onSuccess != null) {
                onSuccess.accept(result);
            }
        } else {
            messageLabel.setText("Invalid username or credential");
            messageLabel.setStyle("-fx-text-fill: #cc0000;");
        }
    }

    private void clearFields() {
        usernameField.clear();
        credentialField.clear();
        messageLabel.setText("");
    }

    private void handleCreateAccount() {
        if (onRegisterClick != null) {
            onRegisterClick.run();
        }
    }
}
