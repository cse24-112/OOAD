package com.bankingsystem.mvc.utils;

import javafx.application.Platform;
import java.util.Timer;
import java.util.TimerTask;

/**
 * DashboardRefreshManager - Provides auto-refresh capabilities for staff/customer dashboards
 * Periodically polls the database for new or updated accounts
 */
public class DashboardRefreshManager {
    private Timer refreshTimer;
    private final long REFRESH_INTERVAL_MS = 5000; // Refresh every 5 seconds
    private boolean isRunning = false;
    private final Runnable refreshAction;

    /**
     * Create a refresh manager with a custom refresh action
     * @param refreshAction The action to execute on refresh (e.g., reload table data)
     */
    public DashboardRefreshManager(Runnable refreshAction) {
        this.refreshAction = refreshAction;
    }

    /**
     * Start auto-refresh polling
     */
    public void startAutoRefresh() {
        if (isRunning) {
            return; // Already running
        }

        isRunning = true;
        refreshTimer = new Timer("DashboardRefreshTimer", true);
        
        refreshTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // Execute on JavaFX thread
                Platform.runLater(() -> {
                    try {
                        refreshAction.run();
                    } catch (Exception e) {
                        System.err.println("Error during dashboard refresh: " + e.getMessage());
                    }
                });
            }
        }, REFRESH_INTERVAL_MS, REFRESH_INTERVAL_MS);

        System.out.println("Dashboard auto-refresh started (interval: " + REFRESH_INTERVAL_MS + "ms)");
    }

    /**
     * Stop auto-refresh polling
     */
    public void stopAutoRefresh() {
        if (refreshTimer != null) {
            refreshTimer.cancel();
            isRunning = false;
            System.out.println("Dashboard auto-refresh stopped");
        }
    }

    /**
     * Check if auto-refresh is running
     */
    public boolean isAutoRefreshRunning() {
        return isRunning;
    }

    /**
     * Set refresh interval (in milliseconds)
     */
    public void setRefreshInterval(long intervalMs) {
        if (intervalMs < 1000) {
            System.err.println("Refresh interval too short, minimum is 1000ms");
            return;
        }
        // Would need to restart timer to apply new interval
        System.out.println("Note: Refresh interval change requires restart");
    }
}
