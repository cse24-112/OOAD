package com.bankingsystem;

/**
 * Data transfer object capturing new account registration form input.
 * Used by registration views to pass data to controllers/services.
 */
public class AccountRegistrationData {
    // Individual customer fields
    private String firstName;
    private String lastName;
    private String nationalID;
    private String username;
    private String pin;
    private String password;

    // Account fields
    private String accountType; // "Savings", "Investment", "Cheque"
    private double initialDeposit;

    // Employment info (for Cheque accounts)
    private String employerName;
    private String employerAddress;
    private String jobTitle;
    private String employmentType; // "Employee", "Self-employed", etc.

    // Company registration info
    private CompanyRegistration companyRegistration;

    public AccountRegistrationData() {}

    // Individual customer setters
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setNationalID(String nationalID) { this.nationalID = nationalID; }
    public void setUsername(String username) { this.username = username; }
    public void setPin(String pin) { this.pin = pin; }
    public void setPassword(String password) { this.password = password; }

    // Account setters
    public void setAccountType(String accountType) { this.accountType = accountType; }
    public void setInitialDeposit(double initialDeposit) { this.initialDeposit = initialDeposit; }

    // Employment info setters
    public void setEmployerName(String employerName) { this.employerName = employerName; }
    public void setEmployerAddress(String employerAddress) { this.employerAddress = employerAddress; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    public void setEmploymentType(String employmentType) { this.employmentType = employmentType; }

    // Company registration setter
    public void setCompanyRegistration(CompanyRegistration companyRegistration) {
        this.companyRegistration = companyRegistration;
    }

    // Getters
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getNationalID() { return nationalID; }
    public String getUsername() { return username; }
    public String getPin() { return pin; }
    public String getPassword() { return password; }
    public String getAccountType() { return accountType; }
    public double getInitialDeposit() { return initialDeposit; }
    public String getEmployerName() { return employerName; }
    public String getEmployerAddress() { return employerAddress; }
    public String getJobTitle() { return jobTitle; }
    public String getEmploymentType() { return employmentType; }
    public CompanyRegistration getCompanyRegistration() { return companyRegistration; }

    /**
     * Validate individual customer registration data
     */
    public boolean isValidIndividualRegistration() {
        return firstName != null && !firstName.trim().isEmpty()
            && lastName != null && !lastName.trim().isEmpty()
            && nationalID != null && !nationalID.trim().isEmpty()
            && username != null && !username.trim().isEmpty()
            && (pin != null && !pin.trim().isEmpty() || password != null && !password.trim().isEmpty())
            && accountType != null && !accountType.trim().isEmpty()
            && initialDeposit >= 0;
    }

    /**
     * Validate Cheque account employment info
     */
    public boolean isValidChequeEmploymentInfo() {
        return employerName != null && !employerName.trim().isEmpty()
            && jobTitle != null && !jobTitle.trim().isEmpty();
    }

    /**
     * Validate company registration data
     */
    public boolean isValidCompanyRegistration() {
        return companyRegistration != null
            && companyRegistration.getCompanyName() != null && !companyRegistration.getCompanyName().isEmpty()
            && companyRegistration.getRegistrationNumber() != null && !companyRegistration.getRegistrationNumber().isEmpty();
    }

    @Override
    public String toString() {
        return "AccountRegistrationData{" +
            "firstName='" + firstName + '\'' +
            ", lastName='" + lastName + '\'' +
            ", accountType='" + accountType + '\'' +
            ", initialDeposit=" + initialDeposit +
            '}';
    }
}
