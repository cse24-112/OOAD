package com.bankingsystem.mvc.view;

import com.bankingsystem.mvc.controller.LoginController;
import com.bankingsystem.mvc.model.Customer;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class LoginView extends VBox {
    private final LoginController controller;
    private final TextField emailField = new TextField();
    private final PasswordField passField = new PasswordField();
    private final ComboBox<String> userType = new ComboBox<>();
    private final Label info = new Label();
    private final java.util.function.Consumer<com.bankingsystem.mvc.controller.LoginResult> onSuccess;

    public LoginView(LoginController controller, java.util.function.Consumer<com.bankingsystem.mvc.controller.LoginResult> onSuccess) {
        this.controller = controller;
        this.onSuccess = onSuccess;
        setPadding(new Insets(20));
        setSpacing(10);
        Label title = new Label("Banking System - Login");
        title.getStyleClass().add("title");
        emailField.setPromptText("Email");
        passField.setPromptText("Password");
        userType.getItems().addAll("Individual","Company","Staff");
        userType.setValue("Individual");
        Button login = new Button("Login");
        login.setOnAction(e -> onLogin());
        getChildren().addAll(title, emailField, passField, userType, login, info);
    }

    private void onLogin() {
        String email = emailField.getText();
        String pass = passField.getText();
        if (email == null || email.isBlank()) { info.setText("Email required"); return; }
        if (pass == null || pass.isBlank()) { info.setText("Password required"); return; }
        String ut = userType.getValue();
        com.bankingsystem.mvc.controller.LoginResult res = controller.login(email, pass, ut);
        if (res == null) { info.setText("Invalid credentials"); }
        else {
            info.setText("Welcome");
            if (onSuccess != null) onSuccess.accept(res);
        }
    }
}
