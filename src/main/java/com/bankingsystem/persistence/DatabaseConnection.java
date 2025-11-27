package com.bankingsystem.persistence;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;
import java.io.InputStream;
import java.util.Properties;

/**
 * DatabaseConnection - Manages connection pool to MySQL or H2 database using HikariCP
 * Supports both MySQL (production) and H2 (development) databases
 * Provides singleton pattern for database connection pool management
 */
public class DatabaseConnection {
    private static DatabaseConnection instance;
    private HikariDataSource dataSource;
    private String databaseType;
    private static final int MAX_POOL_SIZE = 10;
    private static final int MIN_IDLE = 2;
    private static final long IDLE_TIMEOUT = 600000; // 10 minutes
    private static final long MAX_LIFETIME = 1800000; // 30 minutes

    private DatabaseConnection() {
        loadConfiguration();
    }

    /**
     * Load database configuration from database.properties file
     */
    private void loadConfiguration() {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("database.properties")) {
            if (input == null) {
                System.out.println("database.properties not found, using H2 as default");
                initializeH2();
                return;
            }
            props.load(input);
            String dbType = props.getProperty("db.type", "h2").toLowerCase();
            
            if ("mysql".equalsIgnoreCase(dbType)) {
                initializeMySQL(props);
            } else {
                initializeH2();
            }
        } catch (Exception e) {
            System.err.println("Error loading database configuration: " + e.getMessage());
            System.out.println("Falling back to H2 database");
            initializeH2();
        }
    }

    /**
     * Initialize H2 embedded database
     */
    private void initializeH2() {
        try {
            Class.forName("org.h2.Driver");
            
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:h2:mem:bankingdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=MySQL");
            config.setUsername("sa");
            config.setPassword("");
            config.setMaximumPoolSize(MAX_POOL_SIZE);
            config.setMinimumIdle(MIN_IDLE);
            config.setIdleTimeout(IDLE_TIMEOUT);
            config.setMaxLifetime(MAX_LIFETIME);
            config.setConnectionTimeout(30000);
            config.setValidationTimeout(10000);
            config.setLeakDetectionThreshold(60000);
            config.setAutoCommit(true);
            config.setPoolName("BankingSystem-H2-Pool");
            
            dataSource = new HikariDataSource(config);
            databaseType = "H2";
            System.out.println("Connected to H2 Embedded Database (Development Mode)");
            System.out.println("Pool configuration - Max: " + MAX_POOL_SIZE + ", Min Idle: " + MIN_IDLE);
            
            initializeSchema();
        } catch (ClassNotFoundException e) {
            System.err.println("Failed to load H2 driver: " + e.getMessage());
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * Initialize MySQL database connection
     */
    private void initializeMySQL(Properties props) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            String host = props.getProperty("mysql.host", "localhost");
            String port = props.getProperty("mysql.port", "3306");
            String database = props.getProperty("mysql.database", "banking_system");
            String user = props.getProperty("mysql.user", "root");
            String password = props.getProperty("mysql.password", "");
            
            String url = String.format("jdbc:mysql://%s:%s/%s?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
                    host, port, database);
            
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(url);
            config.setUsername(user);
            config.setPassword(password);
            config.setMaximumPoolSize(MAX_POOL_SIZE);
            config.setMinimumIdle(MIN_IDLE);
            config.setIdleTimeout(IDLE_TIMEOUT);
            config.setMaxLifetime(MAX_LIFETIME);
            config.setConnectionTimeout(30000);
            config.setValidationTimeout(10000);
            config.setLeakDetectionThreshold(60000);
            config.setAutoCommit(true);
            config.setPoolName("BankingSystem-MySQL-Pool");
            
            dataSource = new HikariDataSource(config);
            databaseType = "MySQL";
            System.out.println("Connected to MySQL Database at " + host + ":" + port + "/" + database);
            System.out.println("Pool configuration - Max: " + MAX_POOL_SIZE + ", Min Idle: " + MIN_IDLE);
            
            initializeSchema();
        } catch (ClassNotFoundException e) {
            System.err.println("Failed to load MySQL driver: " + e.getMessage());
            System.err.println("Make sure MySQL is running and accessible at the configured location");
            throw new ExceptionInInitializerError(e);
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
     * Get a database connection from the pool
     */
    public Connection getConnection() throws SQLException {
        if (dataSource == null || dataSource.isClosed()) {
            throw new SQLException("Connection pool is not initialized or has been closed");
        }
        return dataSource.getConnection();
    }

    /**
     * Initialize database schema with proper constraints and indexes
     */
    private void initializeSchema() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Create CUSTOMERS table
            stmt.execute("CREATE TABLE IF NOT EXISTS customers (" +
                    "  customer_id VARCHAR(64) PRIMARY KEY," +
                    "  first_name VARCHAR(100)," +
                    "  last_name VARCHAR(100)," +
                    "  national_id VARCHAR(100) UNIQUE," +
                    "  customer_type VARCHAR(32) NOT NULL," +
                    "  username VARCHAR(100) UNIQUE," +
                    "  password VARCHAR(128)," +
                    "  email VARCHAR(150)," +
                    "  phone VARCHAR(50)," +
                    "  company_name VARCHAR(255)," +
                    "  registration_number VARCHAR(100) UNIQUE," +
                    "  creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "  last_modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")");

            // Create ACCOUNTS table
            stmt.execute("CREATE TABLE IF NOT EXISTS accounts (" +
                    "  account_number VARCHAR(64) PRIMARY KEY," +
                    "  customer_id VARCHAR(64) NOT NULL," +
                    "  account_type VARCHAR(32) NOT NULL," +
                    "  balance DOUBLE NOT NULL DEFAULT 0," +
                    "  branch VARCHAR(64)," +
                    "  status VARCHAR(32) NOT NULL DEFAULT 'PENDING'," +
                    "  date_opened DATE NOT NULL," +
                    "  interest_rate DOUBLE," +
                    "  min_balance DOUBLE," +
                    "  min_opening_deposit DOUBLE," +
                    "  employer_name VARCHAR(255)," +
                    "  employer_address VARCHAR(255)," +
                    "  overdraft_allowed BOOLEAN DEFAULT FALSE," +
                    "  approval_timestamp TIMESTAMP," +
                    "  approval_staff_username VARCHAR(100)," +
                    "  creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "  last_modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "  FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE" +
                    ")");

            // Create TRANSACTIONS table
            stmt.execute("CREATE TABLE IF NOT EXISTS transactions (" +
                    "  transaction_id VARCHAR(64) PRIMARY KEY," +
                    "  account_number VARCHAR(64) NOT NULL," +
                    "  transaction_type VARCHAR(32) NOT NULL," +
                    "  amount DOUBLE NOT NULL," +
                    "  balance_after DOUBLE NOT NULL," +
                    "  description VARCHAR(255)," +
                    "  transaction_timestamp TIMESTAMP NOT NULL," +
                    "  creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "  FOREIGN KEY (account_number) REFERENCES accounts(account_number) ON DELETE CASCADE" +
                    ")");

            // Create STAFF table
            stmt.execute("CREATE TABLE IF NOT EXISTS staff (" +
                    "  staff_id VARCHAR(64) PRIMARY KEY," +
                    "  username VARCHAR(100) NOT NULL UNIQUE," +
                    "  password VARCHAR(128) NOT NULL," +
                    "  role VARCHAR(50)," +
                    "  creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "  last_modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")");

            // Create indexes for better query performance
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_customers_username ON customers(username)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_customers_email ON customers(email)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_customers_customer_type ON customers(customer_type)");
            
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_accounts_customer_id ON accounts(customer_id)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_accounts_status ON accounts(status)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_accounts_date_opened ON accounts(date_opened)");
            
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_transactions_account_number ON transactions(account_number)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_transactions_timestamp ON transactions(transaction_timestamp)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_transactions_type ON transactions(transaction_type)");
            
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_staff_username ON staff(username)");

            System.out.println("Database schema and indexes initialized successfully");
        } catch (SQLException e) {
            System.err.println("Failed to initialize schema: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Close database connection pool
     */
    public void closeConnection() {
        try {
            if (dataSource != null && !dataSource.isClosed()) {
                dataSource.close();
                System.out.println("Connection pool closed successfully");
            }
        } catch (Exception e) {
            System.err.println("Error closing connection pool: " + e.getMessage());
        }
    }

    /**
     * Get the HikariDataSource for advanced operations
     */
    public HikariDataSource getDataSource() {
        return dataSource;
    }

    /**
     * Get database type (H2 or MySQL)
     */
    public String getDatabaseType() {
        return databaseType;
    }

    /**
     * Check if connection pool is healthy
     */
    public boolean isPoolHealthy() {
        if (dataSource == null || dataSource.isClosed()) {
            return false;
        }
        try (Connection conn = dataSource.getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Pool health check failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get connection pool statistics
     */
    public String getPoolStats() {
        if (dataSource == null) {
            return "Connection pool not initialized";
        }
        return String.format("DB: %s | Total Connections: %d, Idle: %d, Active: %d", 
                databaseType,
                dataSource.getHikariPoolMXBean().getTotalConnections(),
                dataSource.getHikariPoolMXBean().getIdleConnections(),
                dataSource.getHikariPoolMXBean().getActiveConnections());
    }
}
