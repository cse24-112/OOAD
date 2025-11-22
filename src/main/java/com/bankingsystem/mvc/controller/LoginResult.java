package com.bankingsystem.mvc.controller;

import com.bankingsystem.mvc.model.Customer;
import com.bankingsystem.mvc.model.Staff;

public class LoginResult {
    public enum Role { CUSTOMER, COMPANY, STAFF }
    private Role role;
    private Customer customer;
    private Staff staff;

    public LoginResult(Role role) { this.role = role; }
    public Role getRole() { return role; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer c) { this.customer = c; }
    public Staff getStaff() { return staff; }
    public void setStaff(Staff s) { this.staff = s; }
}
