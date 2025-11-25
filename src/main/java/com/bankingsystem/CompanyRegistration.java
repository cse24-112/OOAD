package com.bankingsystem;

import java.time.LocalDate;

/**
 * Represents company information for company account registration and approval review.
 * Staff reviewers use this to evaluate company accounts before approval.
 */
public class CompanyRegistration {
    private String companyName;
    private String registrationNumber;
    private String taxID;
    private String industry;
    private int employeeCount;
    private String contactPersonName;
    private String contactPersonEmail;
    private String contactPersonPhone;
    private String businessAddress;
    private LocalDate registrationDate;
    private String businessType; // e.g., "Manufacturing", "Services", "Retail", "Technology"
    private boolean verified; // Whether staff has verified the information

    public CompanyRegistration(String companyName, String registrationNumber, String taxID,
                              String industry, int employeeCount, String contactPersonName,
                              String contactPersonEmail, String contactPersonPhone,
                              String businessAddress, String businessType) {
        this.companyName = companyName;
        this.registrationNumber = registrationNumber;
        this.taxID = taxID;
        this.industry = industry;
        this.employeeCount = employeeCount;
        this.contactPersonName = contactPersonName;
        this.contactPersonEmail = contactPersonEmail;
        this.contactPersonPhone = contactPersonPhone;
        this.businessAddress = businessAddress;
        this.businessType = businessType;
        this.registrationDate = LocalDate.now();
        this.verified = false;
    }

    // Getters
    public String getCompanyName() { return companyName; }
    public String getRegistrationNumber() { return registrationNumber; }
    public String getTaxID() { return taxID; }
    public String getIndustry() { return industry; }
    public int getEmployeeCount() { return employeeCount; }
    public String getContactPersonName() { return contactPersonName; }
    public String getContactPersonEmail() { return contactPersonEmail; }
    public String getContactPersonPhone() { return contactPersonPhone; }
    public String getBusinessAddress() { return businessAddress; }
    public LocalDate getRegistrationDate() { return registrationDate; }
    public String getBusinessType() { return businessType; }
    public boolean isVerified() { return verified; }

    // Setters
    public void setVerified(boolean verified) { this.verified = verified; }

    /**
     * Returns formatted company info for display in staff review panel
     */
    public String getFormattedInfo() {
        return String.format(
            "Company: %s\n" +
            "Reg #: %s\n" +
            "Tax ID: %s\n" +
            "Industry: %s\n" +
            "Employees: %d\n" +
            "Type: %s\n" +
            "Contact: %s\n" +
            "Email: %s\n" +
            "Phone: %s\n" +
            "Address: %s",
            companyName, registrationNumber, taxID, industry, employeeCount,
            businessType, contactPersonName, contactPersonEmail, contactPersonPhone, businessAddress
        );
    }

    @Override
    public String toString() {
        return "CompanyRegistration{" +
            "companyName='" + companyName + '\'' +
            ", registrationNumber='" + registrationNumber + '\'' +
            ", taxID='" + taxID + '\'' +
            ", industry='" + industry + '\'' +
            ", employeeCount=" + employeeCount +
            '}';
    }
}
