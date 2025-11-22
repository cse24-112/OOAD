package com.bankingsystem;

import java.time.LocalDate;

public class CompanyCustomer extends Customer {
    private String registrationNumber;
    private String taxID;
    private LocalDate incorporationDate;
    private String contactPersonPhone;
    private double annualTurnover;
    private String certificateOfIncorporationPath;
    private String taxClearanceDocPath;

    public CompanyCustomer(String customerID, String name, String registrationNumber) {
        super(customerID, name, "");
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

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public String getTaxID() {
        return taxID;
    }

    public void setTaxID(String taxID) {
        this.taxID = taxID;
    }
}
