package com.bankingsystem.persistence;

import com.bankingsystem.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * AccountDAOImpl - JDBC implementation for Account persistence
 * Handles CRUD operations for accounts in H2 database
 */
public class AccountDAOImpl {
    private CustomerDAOImpl customerDAO;

    public AccountDAOImpl() {
        this.customerDAO = new CustomerDAOImpl();
    }

    /**
     * Save a new account to database
     */
    public boolean saveAccount(Account account) {
        if (account == null) return false;

        String sql = "INSERT INTO accounts (account_number, customer_id, account_type, balance, branch, status, " +
                "date_opened, interest_rate, min_balance, min_opening_deposit, employer_name, employer_address, " +
                "overdraft_allowed, approval_timestamp, approval_staff_username) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, account.getAccountNumber());
            pstmt.setString(2, account.getOwner().getCustomerID());
            pstmt.setString(3, account.getClass().getSimpleName());
            pstmt.setDouble(4, account.getBalance());
            pstmt.setString(5, account.getBranch());
            pstmt.setString(6, account.getStatus().toString());
            pstmt.setDate(7, java.sql.Date.valueOf(account.getDateOpened()));

            if (account instanceof SavingsAccount) {
                SavingsAccount sa = (SavingsAccount) account;
                pstmt.setDouble(8, sa.getInterestRate());
                pstmt.setDouble(9, sa.getMinBalance());
                pstmt.setDouble(10, 0);
            } else if (account instanceof InvestmentAccount) {
                InvestmentAccount ia = (InvestmentAccount) account;
                pstmt.setDouble(8, ia.getInterestRate());
                pstmt.setDouble(9, ia.getMinOpeningDeposit());
                pstmt.setDouble(10, InvestmentAccount.getMinimumDepositRequired());
            } else if (account instanceof ChequeAccount) {
                ChequeAccount ca = (ChequeAccount) account;
                EmploymentInfo ei = ca.getEmploymentInfo();
                pstmt.setDouble(8, 0);
                pstmt.setDouble(9, 0);
                pstmt.setDouble(10, 0);
                pstmt.setString(11, ei != null ? ei.getEmployerName() : null);
                pstmt.setString(12, ei != null ? ei.getEmployerAddress() : null);
                pstmt.setBoolean(13, ca.isOverdraftAllowed());
            } else {
                pstmt.setDouble(8, 0);
                pstmt.setDouble(9, 0);
                pstmt.setDouble(10, 0);
            }

            pstmt.setTimestamp(14, account.getApprovalDateTime() != null ?
                    Timestamp.valueOf(account.getApprovalDateTime()) : null);
            pstmt.setString(15, account.getApprovalStaffUsername());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error saving account: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Update existing account
     */
    public boolean updateAccount(Account account) {
        if (account == null) return false;

        String sql = "UPDATE accounts SET balance=?, status=?, approval_timestamp=?, " +
                "approval_staff_username=? WHERE account_number=?";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, account.getBalance());
            pstmt.setString(2, account.getStatus().toString());
            pstmt.setTimestamp(3, account.getApprovalDateTime() != null ?
                    Timestamp.valueOf(account.getApprovalDateTime()) : null);
            pstmt.setString(4, account.getApprovalStaffUsername());
            pstmt.setString(5, account.getAccountNumber());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating account: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Assign or change the account number for an existing pending account row.
     * This finds the pending account row for the given customer and updates its primary key to the provided new number.
     */
    public boolean assignAccountNumber(Account account, String newAccountNumber) {
        if (account == null || newAccountNumber == null || newAccountNumber.trim().isEmpty()) return false;

        String customerId = account.getOwner() != null ? account.getOwner().getCustomerID() : null;
        if (customerId == null) return false;

        String findSql = "SELECT account_number FROM accounts WHERE customer_id = ? AND status = ? ORDER BY date_opened DESC LIMIT 1";
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement findStmt = connection.prepareStatement(findSql)) {
            findStmt.setString(1, customerId);
            findStmt.setString(2, AccountStatus.PENDING.toString());
            ResultSet rs = findStmt.executeQuery();
            if (rs.next()) {
                String oldAccountNumber = rs.getString("account_number");
                // Update the account_number value in the DB
                String updateSql = "UPDATE accounts SET account_number = ? WHERE account_number = ?";
                try (PreparedStatement upd = connection.prepareStatement(updateSql)) {
                    upd.setString(1, newAccountNumber);
                    upd.setString(2, oldAccountNumber);
                    int rows = upd.executeUpdate();
                    if (rows > 0) {
                        // reflectively update the in-memory object
                        try {
                            java.lang.reflect.Field f = Account.class.getDeclaredField("accountNumber");
                            f.setAccessible(true);
                            f.set(account, newAccountNumber);
                        } catch (Exception ex) {
                            // ignore reflective failures
                        }
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error assigning account number: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Update account balance only
     */
    public boolean updateAccountBalance(String accountNumber, double newBalance) {
        if (accountNumber == null) return false;

        String sql = "UPDATE accounts SET balance=? WHERE account_number=?";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, newBalance);
            pstmt.setString(2, accountNumber);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating account balance: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Find account by account number
     */
    public Optional<Account> findAccountByNumber(String accountNumber) {
        if (accountNumber == null) return Optional.empty();

        String sql = "SELECT * FROM accounts WHERE account_number = ?";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRowToAccount(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding account: " + e.getMessage());
            e.printStackTrace();
        }

        return Optional.empty();
    }

    /**
     * Find all accounts for a customer
     */
    public List<Account> findAccountsByCustomerId(String customerId) {
        if (customerId == null) return new ArrayList<>();

        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts WHERE customer_id = ? ORDER BY date_opened DESC";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, customerId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Account account = mapRowToAccount(rs);
                if (account != null) {
                    accounts.add(account);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding customer accounts: " + e.getMessage());
            e.printStackTrace();
        }

        return accounts;
    }

    /**
     * Get all accounts
     */
    public List<Account> getAllAccounts() {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts ORDER BY date_opened DESC";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Account account = mapRowToAccount(rs);
                if (account != null) {
                    accounts.add(account);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting all accounts: " + e.getMessage());
            e.printStackTrace();
        }

        return accounts;
    }

    /**
     * Get accounts by status (helper)
     */
    public List<Account> getAccountsByStatus(String status) {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts WHERE status = ? ORDER BY date_opened DESC";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Account account = mapRowToAccount(rs);
                if (account != null) {
                    accounts.add(account);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting accounts by status: " + e.getMessage());
            e.printStackTrace();
        }

        return accounts;
    }

    /**
     * Get pending accounts
     */
    public List<Account> getPendingAccounts() {
        return getAccountsByStatus(AccountStatus.PENDING.toString());
    }

    /**
     * Get approved accounts
     */
    public List<Account> getApprovedAccounts() {
        return getAccountsByStatus(AccountStatus.APPROVED.toString());
    }

    /**
     * Get accounts for a customer with a specific status
     */
    public List<Account> getAccountsByCustomerIdAndStatus(String customerId, AccountStatus status) {
        if (customerId == null || status == null) return new ArrayList<>();

        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts WHERE customer_id = ? AND status = ? ORDER BY date_opened DESC";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, customerId);
            pstmt.setString(2, status.toString());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Account account = mapRowToAccount(rs);
                if (account != null) {
                    accounts.add(account);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting customer accounts by status: " + e.getMessage());
            e.printStackTrace();
        }

        return accounts;
    }

    /**
     * Delete account
     */
    public boolean deleteAccount(String accountNumber) {
        if (accountNumber == null) return false;

        String sql = "DELETE FROM accounts WHERE account_number = ?";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, accountNumber);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting account: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Map database row to Account object
     */
    private Account mapRowToAccount(ResultSet rs) throws SQLException {
        String accountType = rs.getString("account_type");
        String customerId = rs.getString("customer_id");
        Optional<Customer> customerOpt = customerDAO.findCustomerById(customerId);

        if (!customerOpt.isPresent()) {
            System.err.println("Customer not found for account: " + customerId);
            return null;
        }

        Customer customer = customerOpt.get();
        String branch = rs.getString("branch");
        Account account = null;

        if ("SavingsAccount".equals(accountType)) {
            double interestRate = rs.getDouble("interest_rate");
            double minBalance = rs.getDouble("min_balance");
            account = new SavingsAccount(customer, branch, interestRate, minBalance);
        } else if ("InvestmentAccount".equals(accountType)) {
            double interestRate = rs.getDouble("interest_rate");
            double minOpeningDeposit = rs.getDouble("min_opening_deposit");
            account = new InvestmentAccount(customer, branch, interestRate, minOpeningDeposit);
        } else if ("ChequeAccount".equals(accountType)) {
            String employerName = rs.getString("employer_name");
            String employerAddress = rs.getString("employer_address");
            boolean overdraftAllowed = rs.getBoolean("overdraft_allowed");
            EmploymentInfo employmentInfo = null;
            if (employerName != null) {
                employmentInfo = new EmploymentInfo(employerName, employerAddress, "");
            }
            account = new ChequeAccount(customer, branch, employmentInfo, overdraftAllowed);
        }

        if (account != null) {
            // Set account-level properties
            try {
                java.lang.reflect.Field accountNumberField = Account.class.getDeclaredField("accountNumber");
                accountNumberField.setAccessible(true);
                accountNumberField.set(account, rs.getString("account_number"));

                java.lang.reflect.Field balanceField = Account.class.getDeclaredField("balance");
                balanceField.setAccessible(true);
                balanceField.set(account, rs.getDouble("balance"));

                java.lang.reflect.Field statusField = Account.class.getDeclaredField("status");
                statusField.setAccessible(true);
                String statusStr = rs.getString("status");
                statusField.set(account, AccountStatus.valueOf(statusStr));
            } catch (NoSuchFieldException | IllegalAccessException e) {
                System.err.println("Error setting account fields: " + e.getMessage());
            }
        }

        return account;
    }
}

