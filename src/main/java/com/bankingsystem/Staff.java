package com.bankingsystem;

public class Staff {
    private String username;
    private String password;

    public Staff(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() { return username; }
    public boolean authenticate(String u, String p) { return username.equals(u) && password.equals(p); }
}
