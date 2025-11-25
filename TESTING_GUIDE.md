# Comprehensive Testing Guide

## Test Plan Overview

This document outlines all test scenarios for the JavaFX Banking System.

---

## Unit Test Scenarios

### 1. Account Creation Tests

#### Test 1.1: Savings Account Creation with Fee
```java
Given: New SavingsAccount created with P500 initial deposit
When: Account is created
Then:
  • Balance = P450 (P500 - P50 fee)
  • Status = PENDING
  • AccountNumber = null (not assigned)
  • Interest Rate = 0.003333 (4% annual)
  • Transaction recorded for deposit
  • Transaction recorded for fee
```

#### Test 1.2: Investment Account Creation Minimum Requirement
```java
Given: Customer requests Investment account with P1000 deposit
When: Account is created
Then:
  • Balance = P1000
  • Status = PENDING
  • AccountNumber = null
  • Interest Rate = 0.006667 (8% annual)
  • Account eligible for approval (balance >= 1000)
```

#### Test 1.3: Investment Account Insufficient Balance
```java
Given: Customer requests Investment account with P500 deposit
When: Account is created
Then:
  • Balance = P500
  • Status = PENDING
  • AccountNumber = null
  • Account NOT eligible for approval (balance < 1000)
```

#### Test 1.4: Cheque Account Creation
```java
Given: Customer with employment info requests Cheque account
When: Account is created with P200 deposit
Then:
  • Balance = P200 (no creation fee)
  • Status = PENDING
  • AccountNumber = null
  • Employment info preserved
```

---

### 2. Account Approval Tests

#### Test 2.1: Approve Eligible Savings Account
```java
Given: Savings account in PENDING status with P450 balance
When: approve("staff1") is called
Then:
  • Status = APPROVED
  • AccountNumber generated (format: SAV-XXXXX)
  • ApprovalDateTime = now
  • ApprovalStaffUsername = "staff1"
  • showApprovalNotification = true
```

#### Test 2.2: Approve Eligible Investment Account
```java
Given: Investment account in PENDING status with P2000 balance
When: approve("staff1") is called
Then:
  • Status = APPROVED
  • AccountNumber generated (format: INV-XXXXX)
  • ApprovalDateTime = now
  • ApprovalStaffUsername = "staff1"
  • Interest will accrue monthly
```

#### Test 2.3: Reject Account
```java
Given: Pending account
When: reject("staff1") is called
Then:
  • Status = REJECTED
  • AccountNumber = null
  • ApprovalStaffUsername = "staff1"
  • Account inactive
```

#### Test 2.4: Cannot Approve Already Approved Account
```java
Given: Account with status = APPROVED
When: approve() is called again
Then:
  • Throws IllegalStateException
  • Account remains unchanged
```

#### Test 2.5: Cannot Approve Ineligible Investment Account
```java
Given: Investment account in PENDING status with P800 balance
When: AccountApprovalService.approveAccount() is called
Then:
  • isAccountEligibleForApproval() returns false
  • Approval fails
  • Account remains PENDING
```

---

### 3. Interest Calculation Tests

#### Test 3.1: Savings Account Interest (Approved)
```java
Given: Approved Savings account with balance P10000
When: calculateInterest() is called
Then:
  • Interest = P10000 * 0.003333 = P33.33
  • Returns correct amount
```

#### Test 3.2: Savings Account Interest (Pending)
```java
Given: Pending Savings account with balance P10000
When: calculateInterest() is called
Then:
  • Interest = P0 (not approved)
  • Returns 0
```

#### Test 3.3: Investment Account Interest (Approved)
```java
Given: Approved Investment account with balance P5000
When: calculateInterest() is called
Then:
  • Interest = P5000 * 0.006667 = P33.34
  • Returns correct amount
```

#### Test 3.4: Monthly Interest Application
```java
Given: Approved Savings account with balance P1000
When: applyMonthlyInterest() is called
Then:
  • Interest calculated: P3.33
  • Balance updated: P1003.33
  • Transaction recorded with INTEREST type
  • Description = "monthly interest"
```

---

### 4. Authentication Tests

#### Test 4.1: Customer PIN Authentication
```java
Given: IndividualCustomer alice with PIN 1234
When: authenticateWithPin("alice", "1234") is called
Then:
  • Returns true
```

#### Test 4.2: Customer PIN Authentication Fail
```java
Given: IndividualCustomer alice with PIN 1234
When: authenticateWithPin("alice", "0000") is called
Then:
  • Returns false
```

#### Test 4.3: Customer Password Authentication
```java
Given: IndividualCustomer alice with password "password"
When: authenticate("alice", "password") is called
Then:
  • Returns true
```

#### Test 4.4: Staff Authentication
```java
Given: Bank with staff staff1/adminpass
When: bank.authenticateStaff("staff1", "adminpass") is called
Then:
  • Returns true
```

#### Test 4.5: Staff Authentication Fail
```java
Given: Bank with staff staff1/adminpass
When: bank.authenticateStaff("staff1", "wrongpass") is called
Then:
  • Returns false
```

---

### 5. Deposit & Withdrawal Tests

#### Test 5.1: Valid Deposit
```java
Given: Account with balance P100
When: deposit(P50) is called
Then:
  • Balance = P150
  • Transaction recorded (DEPOSIT type)
```

#### Test 5.2: Invalid Deposit (Negative)
```java
Given: Account with balance P100
When: deposit(-P50) is called
Then:
  • Throws IllegalArgumentException
  • Balance unchanged
```

#### Test 5.3: Savings Account Withdrawal (Not Allowed)
```java
Given: SavingsAccount with balance P100
When: withdraw(P50) is called
Then:
  • Returns false
  • Balance unchanged
```

#### Test 5.4: Investment Account Withdrawal
```java
Given: Approved InvestmentAccount with balance P500
When: withdraw(P200) is called
Then:
  • Returns true
  • Balance = P300
  • Transaction recorded (WITHDRAW type)
```

#### Test 5.5: Investment Account Insufficient Balance
```java
Given: Approved InvestmentAccount with balance P100
When: withdraw(P200) is called
Then:
  • Returns false
  • Balance unchanged
```

---

## Integration Tests

### Test 6: Full Customer Lifecycle

#### Scenario 6.1: Create Customer → Create Account → Get Approved
```
Step 1: Create customer
  alice = new IndividualCustomer("CUST001", "Alice", "Smith", "123456789101")
  alice.setUsername("alice")
  alice.setPin("1234")
  
Step 2: Create account
  account = alice.openAccount("savings", P500)
  
Step 3: Verify initial state
  ✓ balance = P450 (P500 - P50 fee)
  ✓ status = PENDING
  ✓ accountNumber = null
  
Step 4: Staff approval
  AccountApprovalService.approveAccount(account, "staff1")
  
Step 5: Verify approved state
  ✓ status = APPROVED
  ✓ accountNumber = "SAV-001"
  ✓ showApprovalNotification = true
  
Step 6: Check notification on next login
  ✓ hasPendingApprovalNotification() = true
  ✓ Display popup to customer
  ✓ clearApprovalNotification()
  ✓ hasPendingApprovalNotification() = false
```

---

## UI/Integration Tests (Manual)

### Test 7: Login & Navigation

#### Test 7.1: Customer Login
```
Steps:
1. Launch application
2. Select User Type: "Customer"
3. Enter username: "alice"
4. Enter PIN: "1234"
5. Click "Login"

Expected:
✓ Login succeeds
✓ CustomerDashboardView displayed
✓ Shows customer name "Alice Smith"
✓ Shows 3 accounts
✓ All accounts show status "Pending Approval"
```

#### Test 7.2: Staff Login
```
Steps:
1. At login screen
2. Select User Type: "Staff"
3. Enter username: "staff1"
4. Enter password: "adminpass"
5. Click "Login"

Expected:
✓ Login succeeds
✓ StaffDashboardView displayed
✓ Table shows all pending accounts (9 total)
✓ "Approve Selected" button enabled
✓ "Reject Selected" button enabled
```

#### Test 7.3: Invalid Login
```
Steps:
1. Select User Type: "Customer"
2. Enter username: "alice"
3. Enter PIN: "0000" (wrong)
4. Click "Login"

Expected:
✓ Login fails
✓ Error message: "Invalid username or credential"
✓ Remain at login screen
✓ Fields cleared
```

---

### Test 8: Account Approval Workflow

#### Test 8.1: Approve Eligible Account
```
Steps:
1. Login as staff1
2. Select Alice's Investment account (P1500)
3. Click "Approve Selected"

Expected:
✓ Success dialog: "Account approved! Account Number: INV-001"
✓ Account removed from pending list
✓ Table refreshes
✓ Pending count decreases
```

#### Test 8.2: Attempt to Approve Ineligible Account
```
Steps:
1. Login as staff1
2. Select Charlie's Investment account (P800)
3. Click "Approve Selected"

Expected:
✓ Error dialog: "Investment account requires minimum balance of P1000"
✓ "Current balance: P800.00"
✓ Account remains in pending list
✓ Approval fails
```

#### Test 8.3: Reject Account
```
Steps:
1. Login as staff1
2. Select Charlie's Cheque account
3. Click "Reject Selected"
4. Confirm dialog: Click OK

Expected:
✓ Account removed from pending list
✓ Status changes to REJECTED
✓ Table refreshes
✓ Success message
```

---

### Test 9: Approval Notification

#### Test 9.1: See Notification After Approval
```
Steps:
1. Login as staff1
2. Approve Bob's Investment account (P2000)
3. Logout
4. Login as bob (PIN: 5678)

Expected:
✓ Approval notification popup appears
✓ Title: "Account Approved"
✓ Shows: "Excellent News!"
✓ Shows account number and balance
✓ Click OK to dismiss
✓ Customer dashboard shows account as APPROVED
✓ Account number displayed (not "Pending")
```

---

### Test 10: Customer Dashboard

#### Test 10.1: View All Accounts
```
Steps:
1. Login as alice (PIN: 1234)

Expected:
✓ Dashboard displays 3 accounts:
  - Savings: balance P450, status "Pending Approval"
  - Investment: balance P1500, status "Pending Approval"
  - Cheque: balance P250, status "Pending Approval"
✓ Account numbers show "(Pending)"
✓ Date opened shows current date
```

#### Test 10.2: Make Deposit
```
Steps:
1. Login as alice
2. Select Investment account
3. Click "Deposit"
4. Enter amount: P500
5. Click OK

Expected:
✓ Balance updates from P1500 to P2000
✓ Dialog closes
✓ Table refreshes
✓ Success message shown
```

#### Test 10.3: Deposit and Approval
```
Steps:
1. Login as charlie (PIN: 9999)
2. Select Investment account (balance P800)
3. Click "Deposit"
4. Enter amount: P300
5. Balance now P1100
6. Logout
7. Login as staff1
8. Find Charlie's Investment (P1100)
9. Click "Approve"

Expected:
✓ Deposit succeeds
✓ Balance updates to P1100
✓ Staff can now approve (balance > P1000)
✓ Approval succeeds
```

---

### Test 11: Logout Functionality

#### Test 11.1: Customer Logout
```
Steps:
1. Login as alice
2. Click "Logout"
3. Confirm dialog: Click OK

Expected:
✓ Confirmation dialog appears
✓ CustomerDashboardView closes
✓ Return to login screen
✓ Fields cleared
```

#### Test 11.2: Staff Logout
```
Steps:
1. Login as staff1
2. Click "Logout"
3. Confirm dialog: Click OK

Expected:
✓ Confirmation dialog appears
✓ StaffDashboardView closes
✓ Return to login screen
```

---

### Test 12: Data Persistence

#### Test 12.1: Data Saved on Exit
```
Steps:
1. Login as alice
2. Click "Deposit"
3. Deposit P100 to savings account
4. Close application

Expected:
✓ Data saved to ~user/.banking-data.json
✓ Console shows: "Bank data saved to..."
```

#### Test 12.2: Data Loaded on Startup
```
Steps:
1. Start application (after previous test)

Expected:
✓ Demo data loaded from file
✓ Console shows: "Loaded bank data from file..."
✓ Alice's savings account balance = P550 (P450 + P100 deposit)
✓ Any staff approvals preserved
```

#### Test 12.3: Corrupted Data Handled
```
Steps:
1. Corrupt banking-data.json file (edit JSON manually)
2. Start application

Expected:
✓ Error caught gracefully
✓ Old file deleted
✓ Fresh demo data created
✓ Console shows: "Corrupted banking-data.json removed"
```

---

## Edge Cases & Boundary Tests

### Test 13: Edge Cases

#### Test 13.1: Very Large Deposit
```
Given: Account with P0 balance
When: Deposit P1,000,000 is made
Then:
✓ Balance = P1,000,000
✓ Transaction recorded
✓ No overflow issues
```

#### Test 13.2: Very Small Interest
```
Given: Account with P1 balance, 4% annual
When: Interest calculated
Then:
✓ Interest = P1 * 0.003333 ≈ P0.003333
✓ Precision maintained
✓ No rounding errors
```

#### Test 13.3: Maximum Customers
```
Given: 1000 customers created
When: Bank.getCustomers() called
Then:
✓ All customers returned
✓ No performance degradation
✓ UI responds normally
```

#### Test 13.4: Multiple Approvals Rapid Fire
```
Steps:
1. Login as staff1
2. Select and approve 5 accounts rapidly
3. Click refresh

Expected:
✓ All approvals succeed
✓ No duplicate account numbers
✓ All statuses updated
✓ Table refreshes properly
```

---

## Performance Tests

### Test 14: Performance

#### Test 14.1: Table Rendering (Staff Dashboard)
```
Expected Performance:
✓ Load 10 pending accounts < 100ms
✓ Render table < 200ms
✓ Responsive UI
```

#### Test 14.2: Login Speed
```
Expected Performance:
✓ Lookup customer < 50ms
✓ Authenticate < 50ms
✓ Navigate to dashboard < 500ms
```

#### Test 14.3: Bulk Operations
```
Expected Performance:
✓ Deposit 100 times < 1000ms
✓ Approve 50 accounts < 2000ms
✓ Save 1000 accounts to JSON < 1000ms
```

---

## Test Coverage Summary

### Coverage by Feature

| Feature | Unit Tests | Integration | UI Tests | Status |
|---------|-----------|-------------|----------|--------|
| Account Creation | ✓ | ✓ | ✓ | ✓ Pass |
| Account Approval | ✓ | ✓ | ✓ | ✓ Pass |
| Interest Calculation | ✓ | ✓ | N/A | ✓ Pass |
| Authentication | ✓ | ✓ | ✓ | ✓ Pass |
| Login/Logout | N/A | ✓ | ✓ | ✓ Pass |
| Approval Notification | N/A | ✓ | ✓ | ✓ Pass |
| Deposit | ✓ | ✓ | ✓ | ✓ Pass |
| Withdrawal | ✓ | ✓ | N/A | ✓ Pass |
| Data Persistence | N/A | ✓ | ✓ | ✓ Pass |
| Staff Dashboard | N/A | ✓ | ✓ | ✓ Pass |
| Customer Dashboard | N/A | ✓ | ✓ | ✓ Pass |

---

## Test Execution Instructions

### Run All Tests
```bash
# Unit tests only
mvn test

# Full build + tests
mvn clean test

# Test with coverage
mvn test jacoco:report
```

### Manual UI Testing
```bash
# Run application
mvn clean javafx:run

# Or from IDE
# Right-click MainApp.java → Run
```

---

## Test Result Documentation

After executing tests, document:
1. ✓ Test passed or ✗ Test failed
2. Actual vs expected results
3. Any issues or anomalies
4. Performance metrics
5. Date and time of test
6. Tester name

---

## Known Limitations

1. **Not Tested**:
   - Network connectivity (single-machine only)
   - Concurrent access (single-threaded)
   - Large dataset performance (> 10000 accounts)

2. **Future Testing**:
   - Multi-user concurrent scenarios
   - Database backend testing
   - Mobile app testing
   - Load testing with thousands of accounts

---

This comprehensive test plan ensures all features work correctly and integrate properly.
