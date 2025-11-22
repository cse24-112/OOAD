package com.bankingsystem.mvc.model;

public class Customer implements Loginable {
    private String customerId;
    private String firstname;
    private String surname;
    private String address;
    private String email;
    private String phone;
    private String customerType; // INDIVIDUAL or COMPANY
    private String password;

    public Customer() {}

    public Customer(String customerId, String firstname, String surname, String address, String email, String phone, String customerType, String password) {
        this.customerId = customerId;
        this.firstname = firstname;
        this.surname = surname;
        this.address = address;
        this.email = email;
        this.phone = phone;
        this.customerType = customerType;
        this.password = password;
    }

    public String getCustomerId() { return customerId; }
    public String getFirstname() { return firstname; }
    public String getSurname() { return surname; }
    public String getAddress() { return address; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getCustomerType() { return customerType; }
    public String getPassword() { return password; }

    @Override
    public boolean authenticate(String username, String password) {
        if (email == null || this.password == null) return false;
        return email.equals(username) && this.password.equals(password);
    }
}
