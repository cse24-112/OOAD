package com.bankingsystem.view;

import com.bankingsystem.AccountRequest;
import com.bankingsystem.CustomerRegistrationRequest;
import com.bankingsystem.EmploymentInfo;
import com.bankingsystem.controller.AdminController;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class AdminView extends BorderPane {
    private final AdminController controller;
    private final ListView<String> reqList = new ListView<>();
    private final ListView<String> custReqList = new ListView<>();
    private final Label info = new Label();

    public AdminView(AdminController controller) {
        this.controller = controller;
        setPadding(new Insets(10));
        Label title = new Label("Admin - Pending Requests");
        title.getStyleClass().add("title");

        Button refresh = new Button("Refresh");
        Button approve = new Button("Approve Account");
        Button approveCust = new Button("Approve Customer");
        Button showCust = new Button("Show Customer Requests");
        Button showAcc = new Button("Show Account Requests");
        Button showHistory = new Button("Show Approval History");
        Button logout = new Button("Logout");
        refresh.setOnAction(e -> refresh());
        approve.setOnAction(e -> onApprove());
        approveCust.setOnAction(e -> onApproveCustomer());
        showCust.setOnAction(e -> refreshCustomer());
        showAcc.setOnAction(e -> refresh());
        logout.setOnAction(e -> onLogout());

        HBox top = new HBox(8, refresh, approve, approveCust, showAcc, showCust, showHistory, logout);
        top.setPadding(new Insets(8));
        setTop(new HBox(8, title, top));
        setCenter(reqList);
        setBottom(info);
        refresh();
    }

    private void refresh() {
        reqList.getItems().clear();
        for (AccountRequest r : controller.listRequests()) {
            reqList.getItems().add(r.getId() + " - " + r.getCustomerId() + " - " + r.getType() + " - " + r.getInitialDeposit() + " - " + r.getStatus());
        }
    }

    private void refreshCustomer() {
        custReqList.getItems().clear();
        for (CustomerRegistrationRequest r : controller.listCustomerRequests()) {
            custReqList.getItems().add(r.getId() + " - " + r.getKind() + " - " + (r.getKind().equalsIgnoreCase("company") ? r.getCompanyName() : r.getFirstName() + " " + r.getLastName()) + " - " + r.getStatus());
        }
        setCenter(custReqList);
    }

    private void refreshHistory() {
        ListView<String> history = new ListView<>();
        for (CustomerRegistrationRequest r : controller.listCustomerHistory()) {
            String who = r.getApprover() == null ? "(n/a)" : r.getApprover();
            String when = r.getApprovedAt() == null ? "(n/a)" : r.getApprovedAt().toString();
            String note = r.getApprovalNotes() == null ? "" : r.getApprovalNotes();
            String rej = r.getRejectionReason() == null ? "" : (" Rejection: " + r.getRejectionReason());
            history.getItems().add(r.getId() + " - " + r.getKind() + " - " + r.getStatus() + " - by: " + who + " at: " + when + " " + note + rej);
        }
        setCenter(history);
    }

    private void onApprove() {
        String sel = reqList.getSelectionModel().getSelectedItem();
        if (sel == null) { info.setText("Select request"); return; }
        String id = sel.split(" - ")[0];
        // show account request details and allow approve/reject with notes
        AccountRequest req = controller.listRequests().stream().filter(r -> r.getId().equals(id)).findFirst().orElse(null);
        if (req == null) { info.setText("Request not found"); return; }

        javafx.scene.control.Dialog<java.util.Map<String,String>> dlg = new javafx.scene.control.Dialog<>();
        dlg.setTitle("Approve Account Request");
        dlg.setHeaderText("Review account request " + req.getId());
        javafx.scene.control.ButtonType approveBtn = new javafx.scene.control.ButtonType("Approve", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
        javafx.scene.control.ButtonType rejectBtn = new javafx.scene.control.ButtonType("Reject", javafx.scene.control.ButtonBar.ButtonData.NO);
        javafx.scene.control.ButtonType cancelBtn = new javafx.scene.control.ButtonType("Cancel", javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE);
        dlg.getDialogPane().getButtonTypes().addAll(approveBtn, rejectBtn, cancelBtn);

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(8);
        grid.setVgap(8);
        grid.setPadding(new javafx.geometry.Insets(10));
        int row = 0;
        grid.add(new javafx.scene.control.Label("Request ID:"), 0, row); grid.add(new javafx.scene.control.Label(req.getId()), 1, row++);
        grid.add(new javafx.scene.control.Label("Customer ID:"), 0, row); grid.add(new javafx.scene.control.Label(req.getCustomerId()), 1, row++);
        grid.add(new javafx.scene.control.Label("Type:"), 0, row); grid.add(new javafx.scene.control.Label(req.getType()), 1, row++);
        grid.add(new javafx.scene.control.Label("Initial Deposit:"), 0, row); grid.add(new javafx.scene.control.Label(String.valueOf(req.getInitialDeposit())), 1, row++);
        if ("cheque".equalsIgnoreCase(req.getType())) {
            EmploymentInfo ei = req.getEmploymentInfo();
            String empStr = ei == null ? "(none provided)" : ei.toString();
            grid.add(new javafx.scene.control.Label("Employment Info:"), 0, row); grid.add(new javafx.scene.control.Label(empStr), 1, row++);
        }
        if ("investment".equalsIgnoreCase(req.getType())) {
            double estInterest = req.getInitialDeposit() * 0.05; // using default 5% rate for estimate
            grid.add(new javafx.scene.control.Label("Estimated Interest (yr):"), 0, row); grid.add(new javafx.scene.control.Label(String.format("%.2f", estInterest)), 1, row++);
        }
        if ("savings".equalsIgnoreCase(req.getType())) {
            grid.add(new javafx.scene.control.Label("Savings Setup Fee:"), 0, row); grid.add(new javafx.scene.control.Label("P50.00 will be charged on approval"), 1, row++);
        }

        javafx.scene.control.TextArea notes = new javafx.scene.control.TextArea(); notes.setPromptText("Staff notes / verification comments"); notes.setPrefRowCount(3);
        grid.add(new javafx.scene.control.Label("Staff notes:"), 0, row); grid.add(notes, 1, row++);
        dlg.getDialogPane().setContent(grid);

        dlg.setResultConverter(bt -> {
            if (bt == approveBtn) {
                java.util.Map<String,String> map = new java.util.HashMap<>();
                map.put("action", "approve");
                map.put("notes", notes.getText());
                return map;
            } else if (bt == rejectBtn) {
                java.util.Map<String,String> map = new java.util.HashMap<>();
                map.put("action", "reject");
                map.put("notes", notes.getText());
                return map;
            }
            return null;
        });

        java.util.Optional<java.util.Map<String,String>> result = dlg.showAndWait();
        if (result.isPresent()) {
            java.util.Map<String,String> r = result.get();
            if ("approve".equals(r.get("action"))) {
                String res = controller.approveRequest(id, "staff1");
                info.setText(res + " — " + r.getOrDefault("notes", ""));
            } else if ("reject".equals(r.get("action"))) {
                String reason = r.getOrDefault("notes", "Rejected by staff");
                String res = controller.rejectRequest(id, reason);
                info.setText(res);
            }
        }
        refresh();
    }

    private void onLogout() {
        // navigate back to login screen
        javafx.scene.control.Alert a = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        a.setTitle("Logged out");
        a.setHeaderText(null);
        a.setContentText("You have been logged out.");
        a.showAndWait();
        com.bankingsystem.view.LoginView lv = new com.bankingsystem.view.LoginView(new com.bankingsystem.controller.LoginController(controller.getBank(), controller.getStage()));
        controller.getStage().getScene().setRoot(lv);
    }

    private void onApproveCustomer() {
        String sel = custReqList.getSelectionModel().getSelectedItem();
        if (sel == null) { info.setText("Select customer request"); return; }
        String id = sel.split(" - ")[0];
        // show approval dialog with request details
        CustomerRegistrationRequest req = controller.getBank().getCustomerRequests().stream().filter(r -> r.getId().equals(id)).findFirst().orElse(null);
        if (req == null) { info.setText("Request not found"); return; }
        javafx.scene.control.Dialog<java.util.Map<String,String>> dlg = new javafx.scene.control.Dialog<>();
        dlg.setTitle("Approve Customer Registration");
        dlg.setHeaderText("Review registration request " + req.getId());
        javafx.scene.control.ButtonType approveBtn = new javafx.scene.control.ButtonType("Approve", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
        javafx.scene.control.ButtonType rejectBtn = new javafx.scene.control.ButtonType("Reject", javafx.scene.control.ButtonBar.ButtonData.NO);
        javafx.scene.control.ButtonType cancelBtn = new javafx.scene.control.ButtonType("Cancel", javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE);
        dlg.getDialogPane().getButtonTypes().addAll(approveBtn, rejectBtn, cancelBtn);

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(8);
        grid.setVgap(8);
        grid.setPadding(new javafx.geometry.Insets(10));

        int row = 0;
        grid.add(new javafx.scene.control.Label("Kind:"), 0, row); grid.add(new javafx.scene.control.Label(req.getKind()), 1, row++);
            if (req.getKind().equalsIgnoreCase("individual")) {
            grid.add(new javafx.scene.control.Label("Name:"), 0, row); grid.add(new javafx.scene.control.Label(req.getFirstName() + " " + req.getLastName()), 1, row++);
            grid.add(new javafx.scene.control.Label("National ID:"), 0, row); grid.add(new javafx.scene.control.Label(req.getNationalId()), 1, row++);
            // allow staff to set username/password (required)
            javafx.scene.control.TextField userField = new javafx.scene.control.TextField(); userField.setPromptText("Set username (required)");
            javafx.scene.control.PasswordField passField = new javafx.scene.control.PasswordField(); passField.setPromptText("Set password (required)");
            grid.add(new javafx.scene.control.Label("Username:"), 0, row); grid.add(userField, 1, row++);
            grid.add(new javafx.scene.control.Label("Password:"), 0, row); grid.add(passField, 1, row++);
            // disable approve button until username & password provided
            javafx.scene.Node approveNode = dlg.getDialogPane().lookupButton(approveBtn);
            approveNode.disableProperty().bind(
                    javafx.beans.binding.Bindings.createBooleanBinding(() -> userField.getText() == null || userField.getText().isBlank() || passField.getText() == null || passField.getText().isBlank(),
                            userField.textProperty(), passField.textProperty())
            );
        } else {
            grid.add(new javafx.scene.control.Label("Company:"), 0, row); grid.add(new javafx.scene.control.Label(req.getCompanyName()), 1, row++);
            grid.add(new javafx.scene.control.Label("Reg No:"), 0, row); grid.add(new javafx.scene.control.Label(req.getRegistrationNumber()), 1, row++);
            grid.add(new javafx.scene.control.Label("Industry:"), 0, row); grid.add(new javafx.scene.control.Label(req.getIndustry()), 1, row++);
            grid.add(new javafx.scene.control.Label("Employees:"), 0, row); grid.add(new javafx.scene.control.Label(req.getEmployeesRange()), 1, row++);
            grid.add(new javafx.scene.control.Label("Revenue:"), 0, row); grid.add(new javafx.scene.control.Label(req.getRevenueRange()), 1, row++);
            grid.add(new javafx.scene.control.Label("Years in operation:"), 0, row); grid.add(new javafx.scene.control.Label(req.getYearsInOperation()), 1, row++);
            grid.add(new javafx.scene.control.Label("Business Details:"), 0, row); javafx.scene.control.TextArea bd = new javafx.scene.control.TextArea(req.getBusinessDetails()); bd.setPrefRowCount(3); bd.setWrapText(true); bd.setEditable(false); grid.add(bd, 1, row++);
            grid.add(new javafx.scene.control.Label("Directors:"), 0, row); javafx.scene.control.TextArea dta = new javafx.scene.control.TextArea(String.join("\n", req.getDirectors())); dta.setPrefRowCount(3); dta.setEditable(false); grid.add(dta, 1, row++);
            grid.add(new javafx.scene.control.Label("Signatories:"), 0, row); javafx.scene.control.TextArea sta = new javafx.scene.control.TextArea(String.join("\n", req.getSignatories())); sta.setPrefRowCount(2); sta.setEditable(false); grid.add(sta, 1, row++);
        }

        // staff notes
        javafx.scene.control.TextArea notes = new javafx.scene.control.TextArea(); notes.setPromptText("Staff notes / verification comments"); notes.setPrefRowCount(3);
        grid.add(new javafx.scene.control.Label("Staff notes:"), 0, row); grid.add(notes, 1, row++);

        dlg.getDialogPane().setContent(grid);

        // result converter
        dlg.setResultConverter(bt -> {
            if (bt == approveBtn) {
                java.util.Map<String,String> map = new java.util.HashMap<>();
                // extract optional username/password if present
                javafx.scene.Node n1 = grid.lookup(".text-field");
                // safer: use typed fields defined above
                javafx.scene.control.TextField uf = (javafx.scene.control.TextField) grid.getChildren().stream().filter(n-> n instanceof javafx.scene.control.TextField).findFirst().orElse(null);
                String u = uf == null ? null : uf.getText();
                javafx.scene.control.PasswordField pf = (javafx.scene.control.PasswordField) grid.getChildren().stream().filter(n-> n instanceof javafx.scene.control.PasswordField).findFirst().orElse(null);
                String p = pf == null ? null : pf.getText();
                map.put("action", "approve");
                map.put("username", u == null ? "" : u);
                map.put("password", p == null ? "" : p);
                map.put("notes", notes.getText());
                return map;
            } else if (bt == rejectBtn) {
                java.util.Map<String,String> map = new java.util.HashMap<>();
                map.put("action", "reject");
                map.put("notes", notes.getText());
                return map;
            }
            return null;
        });

        java.util.Optional<java.util.Map<String,String>> result = dlg.showAndWait();
        if (result.isPresent()) {
            java.util.Map<String,String> r = result.get();
            if ("approve".equals(r.get("action"))) {
                String username = r.getOrDefault("username", null);
                String password = r.getOrDefault("password", null);
                String n = r.getOrDefault("notes", null);
                String res = controller.approveCustomer(id, username, password, n);
                info.setText(res);
            } else if ("reject".equals(r.get("action"))) {
                String reason = r.getOrDefault("notes", "Rejected by staff");
                String res = controller.rejectCustomer(id, reason);
                info.setText(res);
            }
        }
        refreshCustomer();
    }
}
