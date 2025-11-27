package com.bankingsystem.persistence;

import com.bankingsystem.*;
import java.time.LocalDateTime;

/**
 * DatabaseInitializer - Populates database with test data for development and testing
 * Creates approved and pending accounts for staff dashboard demonstration
 */
public class DatabaseInitializer {

    private static final CustomerDAOImpl customerDAO = new CustomerDAOImpl();
    private static final AccountDAOImpl accountDAO = new AccountDAOImpl();
    private static final TransactionDAOImpl transactionDAO = new TransactionDAOImpl();

    /**
     * Initialize database with sample data
     * Creates test customers and accounts with different statuses
     */
    public static void initializeWithTestData() {
        try {
            // Create test individual customers
            createTestIndividualCustomer("IND001", "John", "Doe", "123456789", "john.doe", "password123");
            createTestIndividualCustomer("IND002", "Jane", "Smith", "987654321", "jane.smith", "password456");
            createTestIndividualCustomer("IND003", "Robert", "Johnson", "456789123", "robert.j", "password789");
            createTestIndividualCustomer("IND004", "Sarah", "Williams", "789123456", "sarah.w", "password000");

            // Create test company customers
            createTestCompanyCustomer("COMP001", "Tech Solutions Ltd", "TCH-2023-001", "tech.solutions", "company123");
            createTestCompanyCustomer("COMP002", "Global Trading Corp", "GTC-2023-002", "global.trading", "company456");

            // Create approved accounts for individual customers
            createApprovedAccount("ACC-2023-IND001-SAV", "IND001", "SavingsAccount", 5000.0);
            createApprovedAccount("ACC-2023-IND002-SAV", "IND002", "SavingsAccount", 3500.0);
            createApprovedAccount("ACC-2023-IND003-CHQ", "IND003", "ChequeAccount", 2000.0);
            createApprovedAccount("ACC-2023-IND004-INV", "IND004", "InvestmentAccount", 10000.0);

            // Create pending accounts for individual customers
            createPendingAccount("ACC-PENDING-IND001", "IND001", "ChequeAccount", 1500.0);
            createPendingAccount("ACC-PENDING-IND002", "IND002", "InvestmentAccount", 5000.0);

            // Create approved company accounts
            createApprovedAccount("ACC-2023-COMP001-SAV", "COMP001", "SavingsAccount", 50000.0);
            createApprovedAccount("ACC-2023-COMP002-SAV", "COMP002", "SavingsAccount", 75000.0);

            // Create pending company accounts
            createPendingAccount("ACC-PENDING-COMP001", "COMP001", "SavingsAccount", 25000.0);

            // Add some transactions to approved accounts
            createTestTransaction("ACC-2023-IND001-SAV", "DEPOSIT", 500.0);
            createTestTransaction("ACC-2023-IND002-SAV", "WITHDRAWAL", 200.0);
            createTestTransaction("ACC-2023-IND003-CHQ", "TRANSFER", 300.0);

            System.out.println("Database initialized with test data successfully");
        } catch (Exception e) {
            System.err.println("Error initializing test data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Create test individual customer using DAO
     */
    private static void createTestIndividualCustomer(String customerId, String firstName, String lastName, 
                                                     String nationalId, String username, String password) {
        try {
            IndividualCustomer customer = new IndividualCustomer(customerId, firstName, lastName, nationalId);
            customer.setUsername(username);
            customer.setPassword(password);
            customer.setEmail(firstName.toLowerCase() + "." + lastName.toLowerCase() + "@example.com");
            customer.setPhone("+267555" + String.format("%04d", (int)(Math.random() * 10000)));

            if (customerDAO.saveCustomer(customer)) {
                System.out.println("Created customer: " + customerId + " (" + firstName + " " + lastName + ")");
            }
        } catch (Exception e) {
            if (!e.getMessage().contains("UNIQUE")) {
                System.err.println("Error creating customer: " + e.getMessage());
            }
        }
    }

    /**
     * Create test company customer using DAO
     */
    private static void createTestCompanyCustomer(String customerId, String companyName, String registrationNumber, 
                                                  String username, String password) {
        try {
            CompanyCustomer customer = new CompanyCustomer(customerId, companyName, registrationNumber);
            // Use reflection to set username and pin since CompanyCustomer extends Customer
            java.lang.reflect.Field usernameField = Customer.class.getDeclaredField("username");
            usernameField.setAccessible(true);
            usernameField.set(customer, username);
            
            java.lang.reflect.Field pinField = Customer.class.getDeclaredField("pin");
            pinField.setAccessible(true);
            pinField.set(customer, password);
            
            customer.setEmail("info@" + companyName.toLowerCase().replace(" ", "") + ".com");
            customer.setPhone("+267555" + String.format("%04d", (int)(Math.random() * 10000)));

            if (customerDAO.saveCustomer(customer)) {
                System.out.println("Created company customer: " + customerId + " (" + companyName + ")");
            }
        } catch (Exception e) {
            if (!e.getMessage().contains("UNIQUE")) {
                System.err.println("Error creating company: " + e.getMessage());
            }
        }
    }

    /**
     * Create approved account using DAO
     */
    private static void createApprovedAccount(String accountNumber, String customerId, String accountType, double balance) {
        try {
            // Retrieve customer
            var customerOpt = customerDAO.findCustomerById(customerId);
            if (!customerOpt.isPresent()) {
                System.err.println("Customer not found: " + customerId);
                return;
            }

            Customer customer = customerOpt.get();
            
            // Create appropriate account type
            Account account = null;
            if ("SavingsAccount".equals(accountType)) {
                account = new SavingsAccount(customer, "001", 0.005, 100.0);
            } else if ("ChequeAccount".equals(accountType)) {
                account = new ChequeAccount(customer, "001", null, false);
            } else if ("InvestmentAccount".equals(accountType)) {
                account = new InvestmentAccount(customer, "001", 0.05, 500.0);
            }

            if (account != null) {
                // Set account number using reflection for testing
                try {
                    java.lang.reflect.Field numberField = Account.class.getDeclaredField("accountNumber");
                    numberField.setAccessible(true);
                    numberField.set(account, accountNumber);

                    java.lang.reflect.Field balanceField = Account.class.getDeclaredField("balance");
                    balanceField.setAccessible(true);
                    balanceField.set(account, balance);

                    java.lang.reflect.Field statusField = Account.class.getDeclaredField("status");
                    statusField.setAccessible(true);
                    statusField.set(account, AccountStatus.APPROVED);

                    java.lang.reflect.Field approvalTimeField = Account.class.getDeclaredField("approvalDateTime");
                    approvalTimeField.setAccessible(true);
                    approvalTimeField.set(account, LocalDateTime.now().minusDays(30));

                    java.lang.reflect.Field approvalStaffField = Account.class.getDeclaredField("approvalStaffUsername");
                    approvalStaffField.setAccessible(true);
                    approvalStaffField.set(account, "staff1");
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    System.err.println("Error setting account fields: " + e.getMessage());
                    return;
                }

                if (accountDAO.saveAccount(account)) {
                    System.out.println("Created APPROVED account: " + accountNumber + " for customer: " + customerId);
                }
            }
        } catch (Exception e) {
            System.err.println("Error creating approved account: " + e.getMessage());
        }
    }

    /**
     * Create pending account using DAO
     */
    private static void createPendingAccount(String accountNumber, String customerId, String accountType, double balance) {
        try {
            // Retrieve customer
            var customerOpt = customerDAO.findCustomerById(customerId);
            if (!customerOpt.isPresent()) {
                System.err.println("Customer not found: " + customerId);
                return;
            }

            Customer customer = customerOpt.get();
            
            // Create appropriate account type
            Account account = null;
            if ("SavingsAccount".equals(accountType)) {
                account = new SavingsAccount(customer, "001", 0.005, 100.0);
            } else if ("ChequeAccount".equals(accountType)) {
                account = new ChequeAccount(customer, "001", null, false);
            } else if ("InvestmentAccount".equals(accountType)) {
                account = new InvestmentAccount(customer, "001", 0.05, 500.0);
            }

            if (account != null) {
                // Set account number and balance using reflection
                try {
                    java.lang.reflect.Field numberField = Account.class.getDeclaredField("accountNumber");
                    numberField.setAccessible(true);
                    numberField.set(account, accountNumber);

                    java.lang.reflect.Field balanceField = Account.class.getDeclaredField("balance");
                    balanceField.setAccessible(true);
                    balanceField.set(account, balance);

                    java.lang.reflect.Field statusField = Account.class.getDeclaredField("status");
                    statusField.setAccessible(true);
                    statusField.set(account, AccountStatus.PENDING);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    System.err.println("Error setting account fields: " + e.getMessage());
                    return;
                }

                if (accountDAO.saveAccount(account)) {
                    System.out.println("Created PENDING account: " + accountNumber + " for customer: " + customerId);
                }
            }
        } catch (Exception e) {
            System.err.println("Error creating pending account: " + e.getMessage());
        }
    }

    /**
     * Create test transaction using DAO
     */
    private static void createTestTransaction(String accountNumber, String transactionType, double amount) {
        try {
            Transaction transaction = new Transaction(Transaction.Type.valueOf(transactionType), amount, amount, "Test transaction");
            
            if (transactionDAO.saveTransaction(accountNumber, transaction)) {
                System.out.println("Created transaction: " + transactionType + " for account: " + accountNumber);
            }
        } catch (Exception e) {
            System.err.println("Error creating transaction: " + e.getMessage());
        }
    }
}
