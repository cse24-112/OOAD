package com.bankingsystem;

import java.time.LocalDate;

@SuppressWarnings("unused")
public class CompanyCustomer extends Customer {
    private String registrationNumber;
    private String companyName;
    private String taxID;
    private LocalDate incorporationDate;
    private String contactPersonPhone;
    private double annualTurnover;
    private String certificateOfIncorporationPath;
    private String taxClearanceDocPath;
    private CompanyRegistration companyRegistration; // Detailed company registration info for staff review

    private String password;
    public CompanyCustomer(String customerID, String companyName, String registrationNumber) {
        super(customerID, companyName, companyName, "");
        this.companyName = companyName;
        this.registrationNumber = registrationNumber;
    }

    @Override
    public boolean verifyIdentity() {
        // simplistic check: must have registration number
        return registrationNumber != null && !registrationNumber.isBlank();
    }

    public void authorizesSignatory(String name) {
        // placeholder behaviour
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public boolean authenticate(String username, String password) {
        if (this.username == null || this.password == null) return false;
        return this.username.equals(username) && this.password.equals(password);
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getTaxID() {
        return taxID;
    }

    public void setTaxID(String taxID) {
        this.taxID = taxID;
    }

    public void setCompanyRegistration(CompanyRegistration companyRegistration) {
        this.companyRegistration = companyRegistration;
    }

    public CompanyRegistration getCompanyRegistration() {
        return companyRegistration;
    }
}

