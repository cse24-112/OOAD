package com.bankingsystem;

import java.time.LocalDateTime;

public class AccountRequest {
    public enum Status { PENDING, APPROVED, REJECTED }

    private final String id;
    private final String customerId;
    private final String type;
    private final double initialDeposit;
    private final EmploymentInfo employmentInfo;
    private final LocalDateTime createdAt;
    private Status status = Status.PENDING;

    public AccountRequest(String id, String customerId, String type, double initialDeposit, EmploymentInfo employmentInfo) {
        this.id = id;
        this.customerId = customerId;
        this.type = type;
        this.initialDeposit = initialDeposit;
        this.employmentInfo = employmentInfo;
        this.createdAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public String getCustomerId() { return customerId; }
    public String getType() { return type; }
    public double getInitialDeposit() { return initialDeposit; }
    public EmploymentInfo getEmploymentInfo() { return employmentInfo; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
}
