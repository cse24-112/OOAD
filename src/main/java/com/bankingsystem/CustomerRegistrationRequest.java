package com.bankingsystem;

import java.time.LocalDateTime;
import java.util.List;

public class CustomerRegistrationRequest {
    public enum Status { PENDING, APPROVED, REJECTED }

    private final String id;
    private final String kind; // "individual" or "company"
    private final String firstName;
    private final String lastName;
    private final String nationalId;
    // company fields
    private final String companyName;
    private final String registrationNumber;
    private final String businessDetails;
    private final String industry;
    private final String employeesRange;
    private final String revenueRange;
    private final String yearsInOperation;
    private final List<String> directors; // names/ids
    private final List<String> signatories;

    private final LocalDateTime createdAt;
    private Status status = Status.PENDING;
    // staff review fields
    private String approver;
    private String approvalNotes;
    private LocalDateTime approvedAt;
    private String rejectionReason;

    public CustomerRegistrationRequest(String id, String kind,
                                       String firstName, String lastName, String nationalId,
                                       String companyName, String registrationNumber, String businessDetails,
                                       String industry, String employeesRange, String revenueRange, String yearsInOperation,
                                       List<String> directors, List<String> signatories) {
        this.id = id;
        this.kind = kind;
        this.firstName = firstName;
        this.lastName = lastName;
        this.nationalId = nationalId;
        this.companyName = companyName;
        this.registrationNumber = registrationNumber;
        this.businessDetails = businessDetails;
        this.industry = industry;
        this.employeesRange = employeesRange;
        this.revenueRange = revenueRange;
        this.yearsInOperation = yearsInOperation;
        this.directors = directors;
        this.signatories = signatories;
        this.createdAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public String getKind() { return kind; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getNationalId() { return nationalId; }
    public String getCompanyName() { return companyName; }
    public String getRegistrationNumber() { return registrationNumber; }
    public String getBusinessDetails() { return businessDetails; }
    public List<String> getDirectors() { return directors; }
    public List<String> getSignatories() { return signatories; }
    public String getIndustry() { return industry; }
    public String getEmployeesRange() { return employeesRange; }
    public String getRevenueRange() { return revenueRange; }
    public String getYearsInOperation() { return yearsInOperation; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public String getApprover() { return approver; }
    public void setApprover(String approver) { this.approver = approver; }
    public String getApprovalNotes() { return approvalNotes; }
    public void setApprovalNotes(String approvalNotes) { this.approvalNotes = approvalNotes; }
    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }
    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
}
