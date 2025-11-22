package com.bankingsystem.mvc.model;

public class Staff implements Loginable {
    private String staffId;
    private String username;
    private String password;

    public Staff() {}

    public Staff(String staffId, String username, String password) {
        this.staffId = staffId;
        this.username = username;
        this.password = password;
    }

    public String getStaffId() { return staffId; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }

    @Override
    public boolean authenticate(String username, String password) {
        if (this.username == null || this.password == null) return false;
        return this.username.equals(username) && this.password.equals(password);
    }
}
