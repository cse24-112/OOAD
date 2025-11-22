package com.bankingsystem.view;

import com.bankingsystem.controller.LoginController;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;

/**
 * Boundary class: LoginView
 * Responsible only for displaying login controls and collecting user input.
 * No business logic here; delegates to LoginController.
 */
public class LoginView extends VBox {
    private final LoginController controller;
    private final TextField usernameField = new TextField();
    private final PasswordField passwordField = new PasswordField();
    private final Label message = new Label();

    public LoginView(LoginController controller) {
        this.controller = controller;
        setPadding(new Insets(12));
        setSpacing(8);

        Label title = new Label("Banking System - Login");
        title.setStyle("-fx-font-size:16px; -fx-font-weight: bold;");

        usernameField.setPromptText("Username");
        passwordField.setPromptText("Password");
        Button loginBtn = new Button("Login");
        loginBtn.setOnAction(e -> onLogin());
        Button registerBtn = new Button("Register");
        registerBtn.setOnAction(e -> onRegister());

        getChildren().addAll(title, usernameField, passwordField, new HBox(8, loginBtn, registerBtn), message);
    }

    private void onLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        if (username == null || username.isBlank()) {
            message.setText("Enter username");
            return;
        }
        // delegate to controller; controller will handle navigation and authentication
        String result = controller.login(username, password);
        message.setText(result);
    }

    private void onRegister() {
        controller.showRegistration();
    }
}
