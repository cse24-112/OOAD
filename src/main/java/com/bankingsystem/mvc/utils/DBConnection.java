package com.bankingsystem.mvc.utils;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.h2.tools.RunScript;

public class DBConnection {
    private static final String URL = "jdbc:h2:./bankdb;AUTO_SERVER=TRUE";
    private static final String USER = "sa";
    private static final String PASS = "";

    static {
        initDatabase();
        // seed default staff user if none exists
        try {
            com.bankingsystem.mvc.dao.StaffDAO sdao = new com.bankingsystem.mvc.dao.StaffDAO();
            if (sdao.listAll().isEmpty()) {
                com.bankingsystem.mvc.model.Staff s = new com.bankingsystem.mvc.model.Staff("STAFF1", "staff1", "adminpass");
                sdao.createStaff(s);
            }
        } catch (Exception ex) {
            System.err.println("Failed to seed staff: " + ex.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    public static void initDatabase() {
        try (Connection conn = getConnection()) {
            // execute schema.sql from resources/mvc/schema.sql
            try (InputStreamReader r = new InputStreamReader(DBConnection.class.getResourceAsStream("/mvc/schema.sql"), StandardCharsets.UTF_8)) {
                RunScript.execute(conn, r);
            }
        } catch (Exception ex) {
            System.err.println("Failed to initialize database schema: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
