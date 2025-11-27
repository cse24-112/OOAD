package com.bankingsystem.persistence;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * ConnectionRetryManager - Handles connection resilience with automatic retry logic
 * Provides retry policies and connection validation for robust database operations
 */
public class ConnectionRetryManager {
    private static final int DEFAULT_MAX_RETRIES = 3;
    private static final long DEFAULT_RETRY_DELAY_MS = 1000;
    private static final long MAX_RETRY_DELAY_MS = 10000;

    /**
     * Execute operation with automatic retry on connection failure
     * @param operation The operation to execute
     * @param maxRetries Maximum number of retry attempts
     * @return true if operation succeeded, false otherwise
     */
    public static boolean executeWithRetry(ConnectionOperation operation, int maxRetries) {
        int attempt = 0;
        long delayMs = DEFAULT_RETRY_DELAY_MS;

        while (attempt < maxRetries) {
            try {
                // Validate connection pool health
                if (!DatabaseConnection.getInstance().isPoolHealthy()) {
                    System.err.println("Connection pool is unhealthy");
                    throw new SQLException("Connection pool health check failed");
                }

                operation.execute();
                return true;
            } catch (SQLException e) {
                attempt++;
                System.err.println("Attempt " + attempt + " failed: " + e.getMessage());

                if (attempt < maxRetries) {
                    System.out.println("Retrying in " + delayMs + "ms...");
                    try {
                        Thread.sleep(delayMs);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return false;
                    }
                    // Exponential backoff with cap
                    delayMs = Math.min(delayMs * 2, MAX_RETRY_DELAY_MS);
                }
            }
        }

        System.err.println("Operation failed after " + maxRetries + " attempts");
        return false;
    }

    /**
     * Execute operation with default retry count
     */
    public static boolean executeWithRetry(ConnectionOperation operation) {
        return executeWithRetry(operation, DEFAULT_MAX_RETRIES);
    }

    /**
     * Test connection availability
     * @return true if connection is available and working
     */
    public static boolean testConnection() {
        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            // Execute a simple query to validate connection
            return conn != null && !conn.isClosed() && conn.isValid(5);
        } catch (SQLException e) {
            System.err.println("Connection test failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get connection with validation
     * @return A validated database connection
     * @throws SQLException if unable to obtain valid connection
     */
    public static Connection getValidatedConnection() throws SQLException {
        int maxAttempts = 3;
        for (int i = 0; i < maxAttempts; i++) {
            try {
                Connection conn = DatabaseConnection.getInstance().getConnection();
                if (conn != null && !conn.isClosed() && conn.isValid(5)) {
                    return conn;
                }
            } catch (SQLException e) {
                if (i < maxAttempts - 1) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                } else {
                    throw e;
                }
            }
        }
        throw new SQLException("Unable to obtain a valid connection after " + maxAttempts + " attempts");
    }

    /**
     * Get pool statistics for monitoring
     */
    public static String getPoolStatistics() {
        return DatabaseConnection.getInstance().getPoolStats();
    }

    /**
     * Functional interface for connection operations
     */
    @FunctionalInterface
    public interface ConnectionOperation {
        void execute() throws SQLException;
    }
}
