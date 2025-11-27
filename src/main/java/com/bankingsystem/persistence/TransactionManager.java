package com.bankingsystem.persistence;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.function.Consumer;

/**
 * TransactionManager - Manages ACID transactions with automatic commit/rollback
 * Provides utility methods for executing operations within database transactions
 * Ensures data consistency and proper error handling
 */
public class TransactionManager {

    /**
     * Execute a transaction with automatic rollback on error
     * @param operation The database operation to execute
     * @return true if transaction succeeded, false otherwise
     */
    public static boolean executeTransaction(Consumer<Connection> operation) {
        Connection connection = null;
        try {
            connection = DatabaseConnection.getInstance().getConnection();
            connection.setAutoCommit(false);
            
            operation.accept(connection);
            
            connection.commit();
            return true;
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                    System.err.println("Transaction rolled back due to error: " + e.getMessage());
                } catch (SQLException rollbackEx) {
                    System.err.println("Error during rollback: " + rollbackEx.getMessage());
                }
            }
            System.err.println("Transaction failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Execute a transaction with a return value
     * @param operation The database operation to execute that returns a value
     * @param <T> The return type
     * @return The result from the operation, or null if transaction failed
     */
    public static <T> T executeTransactionWithResult(SQLFunction<Connection, T> operation) {
        Connection connection = null;
        try {
            connection = DatabaseConnection.getInstance().getConnection();
            connection.setAutoCommit(false);
            
            T result = operation.apply(connection);
            
            connection.commit();
            return result;
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                    System.err.println("Transaction rolled back due to error: " + e.getMessage());
                } catch (SQLException rollbackEx) {
                    System.err.println("Error during rollback: " + rollbackEx.getMessage());
                }
            }
            System.err.println("Transaction failed: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Functional interface for operations that throw SQLException
     */
    @FunctionalInterface
    public interface SQLFunction<T, R> {
        R apply(T t) throws SQLException;
    }

    /**
     * Get the current savepoint identifier within a transaction
     * @param connection The database connection
     * @return Savepoint identifier
     */
    public static String createSavepoint(Connection connection) throws SQLException {
        String savepointName = "SP_" + System.nanoTime();
        connection.setSavepoint(savepointName);
        return savepointName;
    }

    /**
     * Rollback to a specific savepoint
     * @param connection The database connection
     * @param savepointName The savepoint identifier
     */
    public static void rollbackToSavepoint(Connection connection, String savepointName) throws SQLException {
        try {
            Savepoint savepoint = connection.setSavepoint(savepointName);
            connection.rollback(savepoint);
        } catch (SQLException e) {
            // If savepoint operation fails, just roll back the entire transaction
            connection.rollback();
        }
    }
}
