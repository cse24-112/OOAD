package com.bankingsystem.view;

import com.bankingsystem.controller.RegistrationController;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class RegistrationView extends VBox {
    private final RegistrationController controller;
    private final ChoiceBox<String> kind = new ChoiceBox<>();
    private final TextField first = new TextField();
    private final TextField last = new TextField();
    private final TextField national = new TextField();
    private final TextField username = new TextField();
    private final PasswordField password = new PasswordField();
    // company fields
    private final TextField companyName = new TextField();
    private final TextField regNumber = new TextField();
    private final TextArea businessDetails = new TextArea();
    private final TextArea directors = new TextArea();
    private final TextArea signatories = new TextArea();
    private final TextArea documents = new TextArea();
    private final Label info = new Label();

    public RegistrationView(RegistrationController controller) {
        this.controller = controller;
        setPadding(new Insets(12));
        setSpacing(10);

        Label title = new Label("Register New Customer");
        title.getStyleClass().add("title");

        kind.getItems().addAll("Individual", "Company");
        kind.setValue("Individual");
        kind.setOnAction(e -> updateForm());

        first.setPromptText("First name");
        last.setPromptText("Last name");
        national.setPromptText("National ID");
        username.setPromptText("Username");
        password.setPromptText("Password");

        companyName.setPromptText("Company name");
        regNumber.setPromptText("Registration number");
        businessDetails.setPromptText("Full company description / trading activities");
        directors.setPromptText("Directors (one per line: Name - ID)");
        signatories.setPromptText("Signatories (one per line)");

        // structured company fields
        ChoiceBox<String> industryBox = new ChoiceBox<>();
        industryBox.getItems().addAll("Agriculture","Manufacturing","Retail","Services","Finance","Technology","Healthcare","Education","Other");
        industryBox.setValue("Services");

        ChoiceBox<String> employeesBox = new ChoiceBox<>();
        employeesBox.getItems().addAll("1-10","11-50","51-200","201-500","501-1000","1001+");
        employeesBox.setValue("1-10");

        ChoiceBox<String> revenueBox = new ChoiceBox<>();
        revenueBox.getItems().addAll("<50k","50k-250k","250k-1M","1M-5M","5M+");
        revenueBox.setValue("<50k");

        ChoiceBox<String> yearsBox = new ChoiceBox<>();
        yearsBox.getItems().addAll("<1","1-3","4-10","10+");
        yearsBox.setValue("1-3");

        Button register = new Button("Submit Registration");
        register.getStyleClass().add("primary-button");
        register.setOnAction(e -> onRegister());

        getChildren().addAll(title, new Label("Type:"), kind, first, last, national, username, password,
            new Separator(), new Label("Company details (choose Company above)"), companyName, regNumber,
            new Label("Industry"), industryBox, new Label("Employees"), employeesBox, new Label("Annual Revenue"), revenueBox, new Label("Years in Operation"), yearsBox,
            new Label("Full Description"), businessDetails,
            new Label("Directors (semicolon separated)"), directors, new Label("Signatories (semicolon separated)"), signatories,
            register, info);
        // store choiceboxes references on the scene graph for later retrieval in onRegister
        industryBox.setId("industryBox");
        employeesBox.setId("employeesBox");
        revenueBox.setId("revenueBox");
        yearsBox.setId("yearsBox");
        updateForm();
    }

    private void onRegister() {
        String k = kind.getValue();
        if ("Individual".equalsIgnoreCase(k)) {
            String fn = first.getText();
            String ln = last.getText();
            String nid = national.getText();
            String user = username.getText();
            String pass = password.getText();
            String res = controller.registerIndividual(fn, ln, nid, user, pass);
            // show confirmation dialog
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle("Registration Submitted");
            alert.setHeaderText("Registration Pending Approval");
            alert.setContentText(res + "\n\nChoose an action:");
            javafx.scene.control.ButtonType goBack = new javafx.scene.control.ButtonType("Back to Login");
            javafx.scene.control.ButtonType stay = new javafx.scene.control.ButtonType("Stay", javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(goBack, stay);
            java.util.Optional<javafx.scene.control.ButtonType> choice = alert.showAndWait();
            if (choice.isPresent() && choice.get() == goBack) {
                LoginView lv = new LoginView(new com.bankingsystem.controller.LoginController(controller.getBank(), controller.getStage()));
                controller.getStage().getScene().setRoot(lv);
            } else {
                info.setText(res);
            }
        } else {
            String cname = companyName.getText();
            String rnum = regNumber.getText();
            String bdet = businessDetails.getText();
            String dirs = directors.getText();
            String signs = signatories.getText();
            @SuppressWarnings("unchecked")
            javafx.scene.control.ChoiceBox<String> industryBox = (javafx.scene.control.ChoiceBox<String>) this.lookup("#industryBox");
            @SuppressWarnings("unchecked")
            javafx.scene.control.ChoiceBox<String> employeesBox = (javafx.scene.control.ChoiceBox<String>) this.lookup("#employeesBox");
            @SuppressWarnings("unchecked")
            javafx.scene.control.ChoiceBox<String> revenueBox = (javafx.scene.control.ChoiceBox<String>) this.lookup("#revenueBox");
            @SuppressWarnings("unchecked")
            javafx.scene.control.ChoiceBox<String> yearsBox = (javafx.scene.control.ChoiceBox<String>) this.lookup("#yearsBox");
            String industry = industryBox == null ? "Other" : String.valueOf(industryBox.getValue());
            String employeesRange = employeesBox == null ? "1-10" : String.valueOf(employeesBox.getValue());
            String revenueRange = revenueBox == null ? "<50k" : String.valueOf(revenueBox.getValue());
            String yearsInOperation = yearsBox == null ? "1-3" : String.valueOf(yearsBox.getValue());
            String res = controller.registerCompany(cname, rnum, bdet, dirs.replaceAll("\\n", ";"), signs.replaceAll("\\n", ";"), industry, employeesRange, revenueRange, yearsInOperation);
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle("Company Registration Submitted");
            alert.setHeaderText("Company Registration Pending Approval");
            alert.setContentText(res + "\n\nA staff member will review the documents. Choose an action:");
            javafx.scene.control.ButtonType goBack = new javafx.scene.control.ButtonType("Back to Login");
            javafx.scene.control.ButtonType stay = new javafx.scene.control.ButtonType("Stay", javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(goBack, stay);
            java.util.Optional<javafx.scene.control.ButtonType> choice = alert.showAndWait();
            if (choice.isPresent() && choice.get() == goBack) {
                LoginView lv = new LoginView(new com.bankingsystem.controller.LoginController(controller.getBank(), controller.getStage()));
                controller.getStage().getScene().setRoot(lv);
            } else {
                info.setText(res);
            }
        }
    }

    private void updateForm() {
        boolean individual = "Individual".equalsIgnoreCase(kind.getValue());
        first.setDisable(!individual);
        last.setDisable(!individual);
        national.setDisable(!individual);
        username.setDisable(!individual);
        password.setDisable(!individual);
        companyName.setDisable(individual);
        regNumber.setDisable(individual);
        businessDetails.setDisable(individual);
        directors.setDisable(individual);
        signatories.setDisable(individual);
        documents.setDisable(individual);
    }
}
