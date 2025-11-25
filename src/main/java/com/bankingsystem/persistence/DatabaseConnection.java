package com.bankingsystem.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.SQLException;

/**
 * DatabaseConnection - Manages JDBC connection to H2 database
 * Provides singleton pattern for database connection management
 */
public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;
    private static final String DB_URL = "jdbc:h2:mem:bankingdb";
    private static final String DB_USERNAME = "sa";
    private static final String DB_PASSWORD = "";

    private DatabaseConnection() {
        try {
            // Load H2 driver
            Class.forName("org.h2.Driver");
            // Create connection to in-memory H2 database
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            System.out.println("Connected to H2 database successfully");
            initializeSchema();
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Get singleton instance of DatabaseConnection
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * Get the database connection
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Initialize database schema
     */
    private void initializeSchema() {
        try (Statement stmt = connection.createStatement()) {
            // Create CUSTOMERS table
            stmt.execute("CREATE TABLE IF NOT EXISTS customers (" +
                    "  customer_id VARCHAR(64) PRIMARY KEY," +
                    "  first_name VARCHAR(100)," +
                    "  last_name VARCHAR(100)," +
                    "  national_id VARCHAR(100)," +
                    "  customer_type VARCHAR(32)," +
                    "  username VARCHAR(100) UNIQUE," +
                    "  password VARCHAR(128)," +
                    "  email VARCHAR(150)," +
                    "  phone VARCHAR(50)," +
                    "  company_name VARCHAR(255)," +
                    "  registration_number VARCHAR(100)," +
                    "  creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")");

            // Create ACCOUNTS table
            stmt.execute("CREATE TABLE IF NOT EXISTS accounts (" +
                    "  account_number VARCHAR(64) PRIMARY KEY," +
                    "  customer_id VARCHAR(64)," +
                    "  account_type VARCHAR(32)," +
                    "  balance DOUBLE," +
                    "  branch VARCHAR(64)," +
                    "  status VARCHAR(32)," +
                    "  date_opened DATE," +
                    "  interest_rate DOUBLE," +
                    "  min_balance DOUBLE," +
                    "  min_opening_deposit DOUBLE," +
                    "  employer_name VARCHAR(255)," +
                    "  employer_address VARCHAR(255)," +
                    "  overdraft_allowed BOOLEAN," +
                    "  approval_timestamp TIMESTAMP," +
                    "  approval_staff_username VARCHAR(100)," +
                    "  FOREIGN KEY (customer_id) REFERENCES customers(customer_id)" +
                    ")");

            // Create TRANSACTIONS table
            stmt.execute("CREATE TABLE IF NOT EXISTS transactions (" +
                    "  transaction_id VARCHAR(64) PRIMARY KEY," +
                    "  account_number VARCHAR(64)," +
                    "  transaction_type VARCHAR(32)," +
                    "  amount DOUBLE," +
                    "  balance_after DOUBLE," +
                    "  description VARCHAR(255)," +
                    "  transaction_timestamp TIMESTAMP," +
                    "  FOREIGN KEY (account_number) REFERENCES accounts(account_number)" +
                    ")");

            // Create STAFF table
            stmt.execute("CREATE TABLE IF NOT EXISTS staff (" +
                    "  staff_id VARCHAR(64) PRIMARY KEY," +
                    "  username VARCHAR(100) UNIQUE," +
                    "  password VARCHAR(128)" +
                    ")");

            System.out.println("Database schema initialized successfully");
        } catch (SQLException e) {
            System.err.println("Failed to initialize schema: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Close database connection
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}
