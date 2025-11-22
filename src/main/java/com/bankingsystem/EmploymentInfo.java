package com.bankingsystem;

public class EmploymentInfo {
    private String employerName;
    private String employerAddress;
    private String employmentType;

    public EmploymentInfo(String employerName, String employerAddress, String employmentType) {
        this.employerName = employerName;
        this.employerAddress = employerAddress;
        this.employmentType = employmentType;
    }

    public String getEmployerName() {
        return employerName;
    }

    public String getEmployerAddress() {
        return employerAddress;
    }

    public String getEmploymentType() {
        return employmentType;
    }

    @Override
    public String toString() {
        return employerName + " (" + employmentType + ") @ " + employerAddress;
    }
}
