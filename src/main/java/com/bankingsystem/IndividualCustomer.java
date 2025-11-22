package com.bankingsystem;

public class IndividualCustomer extends Customer {
    private String nationalId;
    private java.time.LocalDate dateOfBirth;
    private String gender;
    private String employerName;
    private String jobTitle;
    private String nextOfKinName;
    private String nextOfKinContact;
    private String username;
    private String password;

    public IndividualCustomer(String customerID, String firstName, String lastName, String nationalId) {
        super(customerID, firstName, lastName);
        this.nationalId = nationalId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public boolean verifyIdentity() {
        // simplistic verification for demo: must have national id
        return nationalId != null && !nationalId.isBlank();
    }

    @Override
    public boolean authenticate(String username, String password) {
        if (this.username == null || this.password == null) return false;
        return this.username.equals(username) && this.password.equals(password);
    }

    public Account openAccount(String type, double initialDeposit) {
        // Overloaded openAccount behavior depends on type
        switch (type.toLowerCase()) {
            case "savings":
                double fee = 50.0;
                if (initialDeposit < fee) return null; // require fee
                SavingsAccount sa = new SavingsAccount(this, "MainBranch", 0.0005, 0.0);
                sa.deposit(initialDeposit - fee);
                addAccount(sa);
                return sa;
            case "investment":
                double required = 500.0;
                if (!InvestmentAccount.meetsOpeningDeposit(initialDeposit, required)) return null;
                InvestmentAccount ia = new InvestmentAccount(this, "MainBranch", 0.05, required);
                ia.deposit(initialDeposit);
                addAccount(ia);
                return ia;
            case "cheque":
                // Only allowed if person is working (employerName present)
                if (employerName == null || employerName.isBlank()) return null;
                EmploymentInfo info = new EmploymentInfo(employerName, "unknown", "employee");
                // do not create directly here: callers should request approval; but allow direct creation if called by bank.staff
                ChequeAccount ca = new ChequeAccount(this, "MainBranch", info, false);
                ca.deposit(initialDeposit);
                addAccount(ca);
                return ca;
            default:
                return null;
        }
    }

    public void setEmployer(String employerName, String employerAddress, String employmentType) {
        this.employerName = employerName;
    }

    public String getEmployerName() {
        return employerName;
    }

    public String getNationalId() {
        return nationalId;
    }
}
