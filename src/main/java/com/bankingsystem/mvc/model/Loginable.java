package com.bankingsystem.mvc.model;

public interface Loginable {
    boolean authenticate(String username, String password);
}
