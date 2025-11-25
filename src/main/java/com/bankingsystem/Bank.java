package com.bankingsystem;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Bank {
    private String branchCode;
    private List<Customer> customers = new ArrayList<>();
    private List<Account> accounts = new ArrayList<>();
    private List<AccountRequest> requests = new ArrayList<>();
    private List<CustomerRegistrationRequest> customerRequests = new ArrayList<>();
    private List<Staff> staff = new ArrayList<>();

    public Bank(String bankName, String branchCode) {
        this.branchCode = branchCode;
        // seed a default staff user for demo
        staff.add(new Staff("staff1", "adminpass"));
    }

    public void registerCustomer(Customer c) {
        if (c != null) customers.add(c);
    }

    public CustomerRegistrationRequest submitCustomerRegistration(CustomerRegistrationRequest req) {
        String id = "CREQ" + (1000 + customerRequests.size());
        CustomerRegistrationRequest r = new CustomerRegistrationRequest(id, req.getKind(), req.getFirstName(), req.getLastName(), req.getNationalId(), req.getCompanyName(), req.getRegistrationNumber(), req.getBusinessDetails(), req.getIndustry(), req.getEmployeesRange(), req.getRevenueRange(), req.getYearsInOperation(), req.getDirectors(), req.getSignatories());
        customerRequests.add(r);
        return r;
    }

    public java.util.List<CustomerRegistrationRequest> getCustomerRequests() { return java.util.Collections.unmodifiableList(customerRequests); }

    public boolean approveCustomerRegistration(String requestId, String staffUser, String username, String password, String staffNotes) {
        CustomerRegistrationRequest req = customerRequests.stream().filter(r -> r.getId().equals(requestId)).findFirst().orElse(null);
        if (req == null) return false;
        if (req.getKind().equalsIgnoreCase("individual")) {
            // require staff to set credentials
            if (username == null || username.isBlank() || password == null || password.isBlank()) {
                req.setStatus(CustomerRegistrationRequest.Status.REJECTED);
                req.setRejectionReason("Staff must set username and password before approval");
                req.setApprover(staffUser);
                req.setApprovalNotes(staffNotes);
                req.setApprovedAt(java.time.LocalDateTime.now());
                return false;
            }
            // prevent duplicate usernames
            if (usernameExists(username)) {
                req.setStatus(CustomerRegistrationRequest.Status.REJECTED);
                req.setRejectionReason("Username already exists: " + username);
                req.setApprover(staffUser);
                req.setApprovalNotes(staffNotes);
                req.setApprovedAt(java.time.LocalDateTime.now());
                return false;
            }
            String id = "CUST" + (1000 + customers.size());
            IndividualCustomer ic = new IndividualCustomer(id, req.getFirstName(), req.getLastName(), req.getNationalId());
            ic.setUsername(username);
            ic.setPassword(password);
            registerCustomer(ic);
            req.setStatus(CustomerRegistrationRequest.Status.APPROVED);
            req.setApprover(staffUser);
            req.setApprovalNotes(staffNotes);
            req.setApprovedAt(java.time.LocalDateTime.now());
            return true;
        } else if (req.getKind().equalsIgnoreCase("company")) {
            String id = "CUST" + (1000 + customers.size());
            CompanyCustomer cc = new CompanyCustomer(id, req.getCompanyName(), req.getRegistrationNumber());
            registerCustomer(cc);
            req.setStatus(CustomerRegistrationRequest.Status.APPROVED);
            req.setApprover(staffUser);
            req.setApprovalNotes(staffNotes);
            req.setApprovedAt(java.time.LocalDateTime.now());
            return true;
        }
        req.setStatus(CustomerRegistrationRequest.Status.REJECTED);
        req.setRejectionReason("Invalid registration kind");
        return false;
    }

    public boolean usernameExists(String username) {
        if (username == null) return false;
        return customers.stream().filter(c -> c instanceof IndividualCustomer).map(c -> (IndividualCustomer) c).anyMatch(ic -> username.equals(ic.getUsername()));
    }

    public boolean rejectCustomerRegistration(String requestId, String staffUser, String reason) {
        CustomerRegistrationRequest req = customerRequests.stream().filter(r -> r.getId().equals(requestId)).findFirst().orElse(null);
        if (req == null) return false;
        req.setStatus(CustomerRegistrationRequest.Status.REJECTED);
        req.setApprover(staffUser);
        req.setRejectionReason(reason);
        req.setApprovedAt(java.time.LocalDateTime.now());
        return true;
    }

    public Account openAccount(Customer customer, String type, double initialDeposit) {
        if (customer == null) throw new IllegalArgumentException("Customer required");
        Account a = null;
        if (customer instanceof IndividualCustomer) {
            // if called directly by staff approval or internal callers, delegate to customer's openAccount which creates the account immediately
            a = ((IndividualCustomer) customer).openAccount(type, initialDeposit);
        } else if (customer instanceof CompanyCustomer) {
            // company customers can open accounts too; for simplicity allow savings and investment
            switch (type.toLowerCase()) {
                case "savings":
                    double fee = 50.0;
                    if (initialDeposit < fee) return null;
                    SavingsAccount sa = new SavingsAccount(customer, branchCode, 0.0005, 0.0);
                    sa.deposit(initialDeposit - fee);
                    a = sa;
                    break;
                case "investment":
                    if (InvestmentAccount.meetsOpeningDeposit(initialDeposit, 500.0)) {
                        InvestmentAccount ia = new InvestmentAccount(customer, branchCode, 0.05, 500.0);
                        ia.deposit(initialDeposit);
                        a = ia;
                    }
                    break;
                case "cheque":
                    // companies can have cheque accounts with employment info optional
                    ChequeAccount ca = new ChequeAccount(customer, branchCode, null, false);
                    ca.deposit(initialDeposit);
                    a = ca;
                    break;
            }
        }
        if (a != null) {
            accounts.add(a);
            customer.addAccount(a);
        }
        return a;
    }

    public boolean rejectAccountRequest(String requestId, String staffUser, String reason) {
        AccountRequest req = requests.stream().filter(r -> r.getId().equals(requestId)).findFirst().orElse(null);
        if (req == null) return false;
        req.setStatus(AccountRequest.Status.REJECTED);
        // optionally record staffUser and reason somewhere; for now we print
        System.err.println("Account request " + requestId + " rejected by " + staffUser + ": " + reason);
        return true;
    }

    // Request-based opening: customer requests an account which staff approves later.
    public AccountRequest requestAccountOpen(Customer customer, String type, double initialDeposit, EmploymentInfo employmentInfo) {
        String id = "REQ" + (1000 + requests.size());
        AccountRequest req = new AccountRequest(id, customer.getCustomerID(), type, initialDeposit, employmentInfo);
        requests.add(req);
        return req;
    }

    public java.util.List<AccountRequest> getRequests() { return java.util.Collections.unmodifiableList(requests); }

    public boolean approveRequest(String requestId, String staffUser) {
        AccountRequest req = requests.stream().filter(r -> r.getId().equals(requestId)).findFirst().orElse(null);
        if (req == null) return false;
        // find customer
        Customer c = customers.stream().filter(x -> x.getCustomerID().equals(req.getCustomerId())).findFirst().orElse(null);
        if (c == null) return false;
        // perform employment verification if cheque
        if ("cheque".equalsIgnoreCase(req.getType())) {
            if (req.getEmploymentInfo() == null || !EmploymentVerificationService.verify(req.getEmploymentInfo())) {
                req.setStatus(AccountRequest.Status.REJECTED);
                return false;
            }
        }
        Account a = openAccount(c, req.getType(), req.getInitialDeposit());
        if (a != null) {
            req.setStatus(AccountRequest.Status.APPROVED);
            return true;
        }
        req.setStatus(AccountRequest.Status.REJECTED);
        return false;
    }

    public boolean authenticateStaff(String username, String password) {
        return staff.stream().anyMatch(s -> s.authenticate(username, password));
    }

    public void applyInterest() {
        for (Account a : accounts) {
            if (a instanceof PayInterest) {
                PayInterest pi = (PayInterest) a;
                double interest = pi.calculateInterest();
                if (interest > 0) {
                    // record interest properly via Account helper
                    a.recordInterest(interest);
                }
            }
        }
    }

    public Account findAccount(String accountNumber) {
        Optional<Account> o = accounts.stream().filter(ac -> ac.getAccountNumber().equals(accountNumber)).findFirst();
        return o.orElse(null);
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public List<Account> getAccounts() {
        return accounts;
    }
}
