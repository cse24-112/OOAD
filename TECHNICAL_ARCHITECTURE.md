# Banking System - Technical Architecture

## System Overview

```
┌─────────────────────────────────────────────────────────────┐
│                    JavaFX GUI Layer                          │
├──────────────────────────┬──────────────────────────────────┤
│  LoginView               │  StaffDashboardView              │
│  (Authentication)        │  (Approval Workflow)             │
├──────────────────────────┼──────────────────────────────────┤
│  CustomerDashboardView   │  Other Views                     │
│  (Account Display)       │  (Deposit, Transfer, etc)       │
└──────────────────────────┴──────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                 MVC Controller Layer                         │
├──────────────────────────┬──────────────────────────────────┤
│  LoginController         │  StaffController                │
│  (Authentication Logic)  │  (Staff Operations)             │
├──────────────────────────┼──────────────────────────────────┤
│  AccountController       │  Other Controllers              │
│  (Account Operations)    │  (Data Access)                  │
└──────────────────────────┴──────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│              Business Logic & Domain Layer                   │
├──────────────────────────┬──────────────────────────────────┤
│  Bank                    │  AccountApprovalService         │
│  (Core Banking Engine)   │  (Approval Workflow)            │
├──────────────────────────┼──────────────────────────────────┤
│  Customer                │  Account                        │
│  (Customer Management)   │  (Account Base Class)           │
├──────────────────────────┼──────────────────────────────────┤
│  SavingsAccount          │  InvestmentAccount              │
│  ChequeAccount           │  Transaction                    │
└──────────────────────────┴──────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│              Persistence Layer                               │
├──────────────────────────┬──────────────────────────────────┤
│  BankStorage             │  GsonFactory                    │
│  (JSON Serialization)    │  (GSON Configuration)           │
└──────────────────────────┴──────────────────────────────────┘
```

---

## Class Hierarchy

### Account Management Hierarchy
```
Account (abstract)
├── SavingsAccount (extends Account, implements PayInterest)
├── InvestmentAccount (extends Account, implements PayInterest, Withdrawable)
└── ChequeAccount (extends Account, implements Withdrawable)

Transaction (concrete)
│
AccountRequest (concrete)
│
AccountStatus (enum: PENDING, APPROVED, REJECTED)
```

### Customer Hierarchy
```
Customer (abstract)
├── IndividualCustomer (extends Customer)
└── CompanyCustomer (extends Customer)

Staff (concrete)
│
Bank (core engine - singleton pattern)
```

---

## Data Model

### Account State Machine
```
┌────────────┐
│  PENDING   │ ← Initial state when account created
└────┬───────┘
     │ approve()
     ↓
┌────────────┐
│ APPROVED   │ ← Account number assigned, interest accrues
└────────────┘

    OR

┌────────────┐
│  PENDING   │
└────┬───────┘
     │ reject()
     ↓
┌────────────┐
│ REJECTED   │ ← Account cannot be used
└────────────┘
```

### Account Creation Flow
```
1. Customer requests account creation
   ↓
2. Account created with status = PENDING, accountNumber = null
   ↓
3. Account added to customer's account list
   ↓
4. Staff reviews account in Staff Dashboard
   ↓
5a. Approve: Account number generated, status = APPROVED
    Interest calculations enabled
   ↓
5b. Reject: Status = REJECTED, account inactive
```

### Customer Login Flow
```
1. User enters username and credential (PIN/password)
   ↓
2. LoginController.login() called
   ↓
3. Find customer in Bank.customers by username
   ↓
4. Verify credential (PIN or password)
   ↓
5. Return LoginResult with role and customer details
   ↓
6. MainApp routes to appropriate dashboard
```

---

## Key Classes & Responsibilities

### Core Domain Classes

#### Account (Abstract)
```java
- Responsibilities:
  ✓ Store account data (balance, owner, branch, etc)
  ✓ Record transactions
  ✓ Manage account status (PENDING/APPROVED/REJECTED)
  ✓ Generate account number on approval
  ✓ Calculate interest (delegated to subclasses)
  ✓ Handle deposits/withdrawals

- Key Methods:
  • approve(staffUsername) - Assign account number
  • reject(staffUsername) - Mark as rejected
  • deposit(amount) - Add funds
  • withdraw(amount) - Remove funds (subclass implementation)
  • calculateInterest() - Return interest amount
  • applyMonthlyInterest() - Apply interest to balance
```

#### SavingsAccount
```java
- Responsibilities:
  ✓ Charge P50 creation fee
  ✓ Calculate 4% annual interest (0.3333% monthly)
  ✓ Disallow withdrawals
  ✓ Only pay interest when APPROVED

- Key Features:
  • balance = -50 if no deposit
  • Interest only accrues when status == APPROVED
  • Account number format: SAV-XXX
```

#### InvestmentAccount
```java
- Responsibilities:
  ✓ Enforce P1000 minimum for approval
  ✓ Calculate 8% annual interest (0.6667% monthly)
  ✓ Allow withdrawals when approved
  ✓ Only pay interest when APPROVED

- Key Features:
  • Must have balance >= 1000 to be approved
  • Interest only accrues when status == APPROVED
  • Account number format: INV-XXX
```

#### Bank (Singleton Pattern)
```java
- Responsibilities:
  ✓ Store all customers and accounts
  ✓ Authenticate staff
  ✓ Create and manage accounts
  ✓ Process account requests
  ✓ Apply interest to accounts
  ✓ Persist data (save/load)

- Key Collections:
  • List<Customer> customers
  • List<Account> accounts
  • List<AccountRequest> requests
  • List<Staff> staff

- Key Methods:
  • registerCustomer(Customer)
  • openAccount(Customer, type, deposit)
  • authenticateStaff(username, password)
  • approveRequest(requestId, staffUser)
  • applyInterest() - Apply interest to all accounts
```

#### Customer (Abstract)
```java
- Responsibilities:
  ✓ Store customer data
  ✓ Manage customer accounts
  ✓ Authenticate customer
  ✓ Verify identity

- Key Attributes:
  • customerID (CUST-XXX)
  • firstName, lastName
  • nationalID (Omang)
  • username, password/pin
  • accounts: List<Account>

- Subclasses:
  • IndividualCustomer - Person with employment info
  • CompanyCustomer - Business with registration number
```

#### IndividualCustomer
```java
- Additional Attributes:
  • nationalId (Botswana Omang)
  • dateOfBirth
  • gender
  • employerName
  • jobTitle
  • nextOfKinName, nextOfKinContact
  • pin (numeric PIN for login)
  • password (alternative login)

- Key Methods:
  • authenticate(username, password)
  • authenticateWithPin(username, pin)
  • openAccount(type, deposit) - Create account
  • verifyIdentity() - Check national ID exists
```

#### CompanyCustomer
```java
- Additional Attributes:
  • registrationNumber (company reg)
  • companyName
  • taxID
  • incorporationDate
  • annualTurnover

- Key Methods:
  • verifyIdentity() - Check registration number exists
```

### Service Classes

#### AccountApprovalService
```java
- Static Methods:
  ✓ getPendingAccounts(Bank) - Get all pending
  ✓ getPendingAccountsByCustomer(Customer) - Get customer's pending
  ✓ approveAccount(Account, staffUsername) - Approve with validation
  ✓ rejectAccount(Account, staffUsername) - Reject
  ✓ isAccountEligibleForApproval(Account) - Check eligibility
  ✓ getAccountNumber(Account) - Get approved account number
  ✓ hasPendingApprovalNotification(Account) - Check notification flag
  ✓ clearApprovalNotification(Account) - Clear flag

- Validation Rules:
  • Investment: balance >= 1000 to approve
  • Savings: always eligible
  • Cheque: always eligible
```

### Controller Classes

#### LoginController
```java
- Responsibilities:
  ✓ Authenticate user (customer or staff)
  ✓ Support PIN and password credentials
  ✓ Return LoginResult with user role and details

- Key Methods:
  • login(username, credential, userType) -> LoginResult
  • findCustomerByCredentials(bank, username, credential) -> Customer
```

### View Classes

#### LoginView (JavaFX)
```java
- Components:
  ✓ User type selector (Customer/Staff)
  ✓ Username field
  ✓ PIN/Password field (masked)
  ✓ Login button
  ✓ Clear button
  ✓ Demo credentials display

- Features:
  ✓ Input validation
  ✓ Error message display
  ✓ Callback on successful login
  ✓ Professional styling
```

#### StaffDashboardView (JavaFX)
```java
- Components:
  ✓ Title and description
  ✓ TableView of pending accounts
  ✓ Columns: Customer, Type, Balance, Status, DateCreated
  ✓ Approve button (with eligibility check)
  ✓ Reject button (with confirmation)
  ✓ Refresh button
  ✓ Logout button
  ✓ Status label

- Features:
  ✓ Real-time table updates
  ✓ Eligibility validation
  ✓ Helpful error messages
  ✓ Professional styling
```

#### CustomerDashboardView (JavaFX)
```java
- Components:
  ✓ Title and customer info
  ✓ TableView of all accounts
  ✓ Columns: AccountNumber, Type, Status, Balance, DateOpened
  ✓ Deposit button
  ✓ Refresh button
  ✓ Logout button
  ✓ Message label

- Features:
  ✓ Approval notification popup
  ✓ Deposit dialog
  ✓ Real-time account updates
  ✓ Professional styling
```

---

## Data Flow Examples

### Example 1: Account Creation
```
User (Alice)
    ↓
GUI: OpenAccountView (not used in current demo)
    ↓
IndividualCustomer.openAccount("savings", 500.0)
    ↓
Creates new SavingsAccount(alice, "MainBranch", 0.003333, 0.0)
    ↓ Constructor sets:
    • balance = -50 (P50 fee charged)
    • accountNumber = null (not assigned yet)
    • status = AccountStatus.PENDING
    ↓
alice.addAccount(account)
    ↓
bank.openAccount(alice, "savings", 500.0)
    ↓
bank.accounts.add(account)
    ↓
account.deposit(500.0)
    ↓
balance becomes: -50 + 500 = 450
```

### Example 2: Account Approval
```
Staff (staff1)
    ↓
StaffDashboardView.handleApprove()
    ↓
Get selected AccountRow
    ↓
AccountApprovalService.isAccountEligibleForApproval(account)
    ↓ Check if Investment account has balance >= 1000
    ↓
✓ Eligible: Continue
✗ Not eligible: Show error dialog
    ↓
AccountApprovalService.approveAccount(account, "staff1")
    ↓
account.approve("staff1")
    ↓ Sets:
    • status = AccountStatus.APPROVED
    • accountNumber = IDGenerator.generateAccountNumber("SAV")
    • approvalDateTime = LocalDateTime.now()
    • approvalStaffUsername = "staff1"
    • showApprovalNotification = true
    ↓
StaffDashboardView.loadPendingAccounts() (refreshes table)
```

### Example 3: Customer Sees Approval Notification
```
Customer (alice)
    ↓
Login: LoginController.login("alice", "1234", "Customer")
    ↓
Find IndividualCustomer
    ↓
LoginResult created with coreBankCustomer = alice
    ↓
MainApp routes to CustomerDashboardView(alice)
    ↓
CustomerDashboardView.loadAccountsAndCheckApproval()
    ↓ Loop through alice.getAccounts()
    ↓
For each account, check:
    AccountApprovalService.hasPendingApprovalNotification(account)
    ↓
✓ Has notification:
    • Show alert: "Account Approved!"
    • Display account number and balance
    • AccountApprovalService.clearApprovalNotification(account)
    ↓
Display table with account details
    • Approved accounts show actual account number
    • Pending accounts show "(Pending)"
```

---

## Design Patterns Used

### 1. **Singleton Pattern**
- Bank: Only one bank instance in application
- Created and managed by MainApp
- Shared across all controllers

### 2. **MVC (Model-View-Controller)**
- Model: Account, Customer, Bank, Transaction
- View: JavaFX VBox/BorderPane/TableView classes
- Controller: LoginController, StaffController, AccountController

### 3. **Service Layer Pattern**
- AccountApprovalService: Encapsulates business logic
- Static methods for common operations
- Clear separation from controllers

### 4. **Strategy Pattern**
- Account subclasses (Savings, Investment, Cheque)
- Different withdrawal/interest strategies per type

### 5. **State Pattern**
- AccountStatus enum (PENDING, APPROVED, REJECTED)
- Different behaviors based on status

### 6. **DAO (Data Access Object)**
- BankStorage: Handles persistence
- GsonFactory: JSON serialization

---

## Approval Workflow Sequence Diagram

```
Customer          MainApp           Staff          Bank
    │                 │              │              │
    │─── Login ─────>│              │              │
    │                 │          │ auth
    │<─────────────────┼──────────┤──│──────────────│
    │                 │              │              │
    │   View Accounts │              │              │
    │<────────────────│              │              │
    │  (All Pending)  │              │              │
    │                 │              │              │
    │                 │              │  Login       │
    │                 │<─────────────│              │
    │                 │   Staff Dashboard Open    │
    │                 │              │              │
    │                 │<─ Get Pending Accounts ───│
    │                 │              │              │
    │   (Staff approves account)     │              │
    │                 │   Approve───>│              │
    │                 │              │   Approve ──>
    │                 │              │   Account #  │
    │                 │              │   assigned  │
    │                 │<──────────────────────────│
    │                 │              │              │
    │   Login Again   │              │              │
    │─── Login ─────>│              │              │
    │                 │  Check Notifications     │
    │                 │  Found: Approval Popup  │
    │                 │<─────────────────────────│
    │  ┌──────────────────────────────┐            │
    │  │ Account Approved!             │            │
    │  │ Account #: SAV-001            │            │
    │  │ Balance: P450.00              │            │
    │  └──────────────────────────────┘            │
    │                 │              │              │
    │                 │   Deposit ──>│              │
    │<─ Deposit Form ─│              │              │
    │                 │              │  Update ──> │
    │                 │<──────────────────────────│
```

---

## Interest Application Flow

```
Bank.applyInterest() [Called daily/monthly]
    ↓
For each Account in bank.accounts
    ↓
If account instanceof PayInterest
    ↓
If account.status == APPROVED
    ↓
Calculate interest:
    • Savings: balance * 0.003333 (4% annual)
    • Investment: balance * 0.006667 (8% annual)
    ↓
account.recordInterest(interestAmount)
    ↓
balance += interestAmount
    ↓
transaction.add(INTEREST transaction)
```

---

## Error Handling

### Investment Account Approval Validation
```
try {
    AccountApprovalService.approveAccount(investment, "staff1")
} catch {
    → Check balance >= 1000
    → If not: Show warning dialog
       "Investment account requires minimum P1000"
       "Current balance: P" + balance
    → Account remains PENDING
    → No account number assigned
}
```

### Login Validation
```
try {
    LoginController.login(username, credential, userType)
    → Find customer by username
    → Verify credential (PIN or password)
    → If success: Return LoginResult
    → If fail: Return null
    → Display "Invalid username or credential"
}
```

### Deposit Validation
```
try {
    CustomerDashboardView.handleDeposit()
    → Get amount from dialog
    → Validate amount > 0
    → account.deposit(amount)
    → Update balance display
    → Save to persistent storage
}
```

---

## Performance Considerations

1. **Table Rendering**
   - Staff Dashboard: ~10 pending accounts typically
   - Customer Dashboard: 3-5 accounts typical
   - No performance issues expected

2. **Query Operations**
   - getPendingAccounts(): O(n) linear search
   - findAccountByNumber(): O(n) linear search
   - Acceptable for small data set

3. **Persistence**
   - Saves entire Bank object to JSON
   - Automatic on application exit
   - Automatic load on startup
   - < 100ms typically

4. **Interest Calculation**
   - Not yet implemented in demo
   - Would run via Bank.applyInterest()
   - O(n) where n = total accounts
   - Could be optimized with scheduled task

---

## Future Extensibility

```
Account (abstract)
├── SavingsAccount ✓
├── InvestmentAccount ✓
├── ChequeAccount ✓
├── FixedDepositAccount [Future]
└── LoanAccount [Future]

Customer (abstract)
├── IndividualCustomer ✓
├── CompanyCustomer ✓
├── GovernmentAgency [Future]
└── NGO [Future]

AccountApprovalService
├── approveAccount() ✓
├── rejectAccount() ✓
├── scheduleApprovalEmail() [Future]
├── sendApprovalNotification() [Future]
└── generateApprovalReport() [Future]
```

---

## Code Quality Metrics

- ✓ All core classes have JavaDoc comments
- ✓ Methods have clear single responsibility
- ✓ Proper error handling and validation
- ✓ Clean separation of concerns
- ✓ No code duplication
- ✓ Consistent naming conventions
- ✓ Proper use of access modifiers (private/protected/public)

---

This architecture provides a solid foundation for a real banking system with room for extension and improvement.
