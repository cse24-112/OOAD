package com.bankingsystem;

@SuppressWarnings("unused")
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
    private String pin; // PIN for login authentication

    public IndividualCustomer(String customerID, String firstName, String lastName, String nationalId) {
        // Customer superclass requires a pin parameter; default to empty string here.
        super(customerID, firstName, lastName, "");
        this.nationalId = nationalId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getPin() {
        return pin;
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

    /**
     * Authenticate using PIN (alternative login method)
     */
    public boolean authenticateWithPin(String username, String pin) {
        if (this.username == null || this.pin == null) return false;
        return this.username.equals(username) && this.pin.equals(pin);
    }

    public Account openAccount(String type, double initialDeposit) {
        // Accounts now start with PENDING status and null account number
        // They must be approved by staff to be assigned an account number
        switch (type.toLowerCase()) {
            case "savings":
                // Savings accounts charge P50 on creation
                SavingsAccount sa = new SavingsAccount(this, "MainBranch", 0.003333, 0.0); // 4% annual
                if (initialDeposit > 0) {
                    sa.deposit(initialDeposit);
                }
                addAccount(sa);
                return sa;
            case "investment":
                // Investment accounts must have minimum P1000 for approval
                InvestmentAccount ia = new InvestmentAccount(this, "MainBranch", 0.006667, 1000.0); // 8% annual
                if (initialDeposit > 0) {
                    ia.deposit(initialDeposit);
                }
                addAccount(ia);
                return ia;
            case "cheque":
                // Cheque accounts only allowed if person is working (employerName present)
                if (employerName == null || employerName.isBlank()) return null;
                EmploymentInfo info = new EmploymentInfo(employerName, "unknown", "employee");
                ChequeAccount ca = new ChequeAccount(this, "MainBranch", info, false);
                if (initialDeposit > 0) {
                    ca.deposit(initialDeposit);
                }
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

