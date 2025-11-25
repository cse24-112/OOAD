# Quick Testing Guide - Final Features

## How to Test the New Features

### 1. Run the Application
```bash
cd "c:\Users\thoma\OneDrive - Botswana Accountancy College\Desktop\OOAD"
mvn clean javafx:run
```

### 2. Test New Customer Registration

**Step 1: Start Registration**
- Click **"Create Account"** button on login screen
- New window opens with registration form

**Step 2: Individual Registration (Tab 1)**
- Fill in personal information:
  - First Name: Test
  - Last Name: User
  - National ID: 123456789012
- Fill in login credentials:
  - Username: testuser1
  - PIN: 1234 (or password)
- Select account type: **Savings**
- Enter initial deposit: 1000
- Click **"Register & Create Account"**
- See popup: "Account Pending Approval"
- Closes automatically, returns to login

**Step 3: Company Registration (Tab 2)**
- Fill in company info:
  - Company Name: Test Corp
  - Registration Number: REG-2025-TEST
  - Tax ID: TAX-12345
- Select Industry: Technology
- Employees: 25
- Enter contact details
- Click **"Register & Create Company Account"**
- See popup: "Company Account Pending Approval"
- Returns to login

### 3. Test Staff Approval Workflow

**Step 1: Login as Staff**
- Username: **staff1**
- Password: **adminpass**
- Select: **Staff**
- Click **Login**

**Step 2: See Pending Accounts**
- Staff Dashboard opens
- Shows table of ALL pending accounts (both demo and new registrations)
- See mix of:
  - Individual accounts (Savings, Investment, Cheque)
  - Company accounts with company names

**Step 3: Approve an Individual Account**
- Click on any individual account row
- Click **"Approve Selected"**
- Dialog: "Enter Account Number"
- Auto-suggested: SAV-123456 (or similar)
- Click OK
- See success message: "Account approved! Account Number: SAV-XXXXX"
- Table refreshes, account disappears (no longer pending)

**Step 4: Approve a Company Account**
- Click on a company account row
- Click **"Approve Selected"**
- First sees popup: Company details (name, reg#, tax ID, industry, employees, contact)
- Click OK to dismiss details
- Dialog: "Enter Account Number"
- Accept or customize account number
- Click OK
- See success message with company account details
- Account approved and removed from pending list

**Step 5: Reject an Account**
- Click on a pending account
- Click **"Reject Selected"**
- Confirmation dialog: "Are you sure?"
- Click OK
- See success message: "Account rejected"
- Account removed from pending list

### 4. Test Customer Login with Approved Account

**Step 1: See Approval Notification**
- After staff approves a pending account
- Logout (click Logout button)
- Login as that customer (use the username/PIN from registration)
- Popup appears: "Account Approved! Account Number: XXX"
- Shows account number and balance

**Step 2: View Account in Dashboard**
- Dashboard shows account with:
  - Account Number: SAV-XXXXX (or assigned number)
  - Type: Savings
  - Status: APPROVED
  - Balance: 950.00 (original amount minus P50 fee)
  - Date Opened: Today

**Step 3: Deposit to Approved Account**
- Select an approved account
- Click **"Deposit"**
- Dialog: Enter amount (e.g., 500)
- Click OK
- Balance updates: 950 + 500 = 1450
- Click **"Refresh"** to verify

### 5. Verify Demo Data Customers

**Test accounts with pre-loaded data:**

| Login | PIN/Password | Status | Accounts |
|-------|---|---|---|
| alice | 1234 | Has approved & pending | 3 accounts (2 approved, 1 pending) |
| bob | 5678 | Has approved & pending | 2 accounts (1 approved, 1 pending) |
| charlie | 9999 | All pending | 3 accounts (all pending) |
| diana | 1111 | All approved | 1 account (approved) |
| techcompany | password | Company approved/pending | 2 accounts (1 approved, 1 pending) |
| miningcorp | password | Company all pending | 2 accounts (all pending) |

**Test flow:**
1. Login as alice
2. See 2 approved accounts with balances + 1 pending
3. Logout, login as staff1
4. See alice's pending Cheque account
5. Approve it, assign number
6. Logout, login as alice
7. See approval popup
8. See account now has number and APPROVED status

### 6. Validate Input Validation

**Try these to see validation errors:**
- Empty fields → "Please fill in..."
- Duplicate username → "Username already taken"
- PIN not numeric → "PIN must be 4-6 digits"
- Password too short → "Password must be 6+ characters"
- National ID too short → "National ID invalid"
- Cheque account without employer → "Employment information required"
- Investment account with <P1000 balance (when approving) → "Investment account requires minimum P1000"

### 7. Testing Checklist

- [ ] Can click "Create Account" from login
- [ ] Individual registration form accepts valid data
- [ ] Company registration form accepts valid data
- [ ] See "Pending Approval" popup after registration
- [ ] Can login as staff1 and see pending accounts
- [ ] Staff can approve individual accounts
- [ ] Staff can approve company accounts (shows company details)
- [ ] Staff can reject accounts
- [ ] Customer sees approval notification on next login
- [ ] Customer sees account number in dashboard
- [ ] Demo customers (alice, bob, charlie, etc.) work with pre-loaded data
- [ ] Deposit functionality works with approved accounts
- [ ] Approved account balances persist after login/logout
- [ ] Investment accounts show eligibility errors if balance < P1000
- [ ] Input validation works (rejects invalid data)
- [ ] All buttons (Logout, Refresh, Back) function correctly

### 8. Key Demo Scenarios

**Scenario A: New Registration → Approval → Notification (5 min)**
1. Click "Create Account"
2. Register new individual customer
3. Logout
4. Login as staff1
5. See new pending account
6. Approve with account number
7. Logout staff
8. Login as new customer
9. See approval popup with account number

**Scenario B: Company Account Registration (3 min)**
1. Click "Create Account"
2. Go to Company Registration tab
3. Fill company details (use realistic company info)
4. Submit
5. Logout
6. Login staff1
7. Approve company account
8. Staff sees company details popup
9. Assign account number
10. Logout, login as company

**Scenario C: Pre-Loaded Demo Data (2 min)**
1. Login as alice
2. See 2 approved accounts with money
3. See 1 pending Cheque account
4. Try to deposit to approved account
5. Logout, login as staff1
6. See alice's pending Cheque account
7. Approve it
8. Logout, login as alice
9. See approval notification
10. See Cheque account now approved with number

---

## Troubleshooting

**Problem:** Can't see "Create Account" button
- **Solution:** Make sure you're on the Login screen (not already logged in)

**Problem:** Registration succeeds but returns to login screen immediately
- **Solution:** This is expected. Click login with your new credentials to verify account was created

**Problem:** Staff doesn't see new pending accounts
- **Solution:** Click "Refresh" button to reload pending accounts list

**Problem:** Account number not showing in customer dashboard
- **Solution:** Click "Refresh" button. Account number is assigned by staff, may take a moment to sync

**Problem:** Deposit amount not updating balance
- **Solution:** Make sure you're depositing to an APPROVED account. Click "Refresh" to update display

**Problem:** Can't login with new credentials
- **Solution:** Make sure you typed username/PIN correctly. Check case sensitivity (usernames are case-sensitive)

---

## Expected Console Output on Startup

```
=== DEMO DATA INITIALIZED ===
Demo Customers Created:
1. Alice Smith (alice / 1234) - 2 approved, 1 pending
2. Bob Johnson (bob / 5678) - 1 approved, 1 pending
3. Charlie Williams (charlie / 9999) - 0 approved, 3 pending
4. Diana Evans (diana / 1111) - 1 approved
5. Botswana Tech Ltd (techcompany/password) - 1 approved, 1 pending
6. Mining Services Corp (miningcorp/password) - 0 approved, 2 pending
Staff: staff1 / adminpass

Total Approved Accounts: 6
Total Pending Accounts: 6
============================
```

---

## Key Endpoints to Test

| Feature | How to Test | Expected Result |
|---------|---|---|
| Create Account | Click "Create Account" | Registration form opens |
| Individual Reg | Fill form, submit | Pending popup, returns to login |
| Company Reg | Fill company form, submit | Company pending popup, returns to login |
| Staff Login | staff1 / adminpass | Staff Dashboard with pending accounts |
| Staff Approve | Select account, click Approve | Dialog for account number, success message |
| Staff Reject | Select account, click Reject | Confirmation dialog, account removed |
| Customer Login | Use registered credentials | See account(s) with status and balance |
| Approval Notification | Staff approves, customer logs in | Popup shows account number |
| Deposit | Select account, click Deposit | Balance updates |
| Logout | Click Logout | Return to login screen |

---

**System is production-ready for testing and demonstration!**
