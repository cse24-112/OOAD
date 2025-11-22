package com.bankingsystem;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public abstract class Customer {
    protected String customerID;
    protected String firstName;
    protected String lastName;
    protected String address;
    protected String email;
    protected String nationalID;
    protected String phone;
    protected List<Account> accounts = new ArrayList<>();
    protected LocalDate createdDate = LocalDate.now();

    public Customer(String customerID, String firstName, String lastName) {
        this.customerID = customerID;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getCustomerID() {
        return customerID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNationalID() {
        return nationalID;
    }

    public void setNationalID(String nationalID) {
        this.nationalID = nationalID;
    }

    /**
     * Authenticate a customer using username/password. Default: not supported.
     */
    public boolean authenticate(String username, String password) {
        return false;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void addAccount(Account a) {
        if (a != null) accounts.add(a);
    }

    public abstract boolean verifyIdentity();

    @Override
    public String toString() {
        return firstName + " " + lastName + " (" + customerID + ")";
    }
}
