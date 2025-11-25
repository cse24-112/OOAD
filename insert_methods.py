#!/usr/bin/env python3
import os

file_path = r"c:\Users\thoma\OneDrive - Botswana Accountancy College\Desktop\OOAD\src\main\java\com\bankingsystem\persistence\AccountDAOImpl.java"

# Read the file
with open(file_path, 'r', encoding='utf-8') as f:
    lines = f.readlines()

# Find the line that contains "Delete account"
insert_idx = -1
for i, line in enumerate(lines):
    if '* Delete account' in line:
        insert_idx = i - 1  # Insert before the comment block
        break

if insert_idx == -1:
    print("ERROR: Could not find insertion point")
    exit(1)

# New methods to insert
new_methods = '''
    /**
     * Get all pending accounts ordered by date
     */
    public List<Account> getPendingAccounts() {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts WHERE status = 'PENDING' ORDER BY date_opened DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Account account = mapRowToAccount(rs);
                if (account != null) {
                    accounts.add(account);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting pending accounts: " + e.getMessage());
            e.printStackTrace();
        }

        return accounts;
    }

    /**
     * Get all approved accounts ordered by date
     */
    public List<Account> getApprovedAccounts() {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts WHERE status = 'APPROVED' ORDER BY date_opened DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Account account = mapRowToAccount(rs);
                if (account != null) {
                    accounts.add(account);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting approved accounts: " + e.getMessage());
            e.printStackTrace();
        }

        return accounts;
    }

    /**
     * Get accounts by customer ID and status
     */
    public List<Account> getAccountsByCustomerIdAndStatus(String customerId, String status) {
        if (customerId == null) return new ArrayList<>();

        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts WHERE customer_id = ? AND status = ? ORDER BY date_opened DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, customerId);
            pstmt.setString(2, status);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Account account = mapRowToAccount(rs);
                if (account != null) {
                    accounts.add(account);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting accounts by customer and status: " + e.getMessage());
            e.printStackTrace();
        }

        return accounts;
    }
'''

# Insert the methods
lines.insert(insert_idx, new_methods + '\n')

# Write back
with open(file_path, 'w', encoding='utf-8') as f:
    f.writelines(lines)

print(f"Successfully inserted methods at line {insert_idx}")
