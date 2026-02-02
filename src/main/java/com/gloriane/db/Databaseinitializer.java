package com.gloriane.db;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Databaseinitializer {
    private static final String SERVER_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "classroom_rental_db";
    private static final String USER = "root";
    private static final String PASSWORD = "@Gloriane--001";


    public static void initialize() {
        System.out.println("🚀 Starting database initialization...");

        // Step 1: Create the database
        createDatabaseIfNotExists();

        // Step 2: Run the schema.sql file (commented out - file doesn't exist)
        runSQLScript("src/main/resources/schema.sql");

        System.out.println("✅ Database initialization complete!");
    }

    // Creates the database if it doesn't already exist
    private static void createDatabaseIfNotExists() {
        // Connect to MySQL server (not to a specific database yet)
        try (Connection conn = DriverManager.getConnection(SERVER_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {

            // Create database if it doesn't exist
            String sql = "CREATE DATABASE IF NOT EXISTS " + DB_NAME;
            stmt.executeUpdate(sql);
            System.out.println("✅ Database '" + DB_NAME + "' is ready");

        } catch (SQLException e) {
            System.err.println("❌ Error creating database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void runSQLScript(String scriptPath) {
        System.out.println("📄 Running SQL script: " + scriptPath);

        try (Connection conn = DriverManager.getConnection(SERVER_URL + DB_NAME, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             BufferedReader reader = new BufferedReader(new FileReader(scriptPath))) {

            // Read the entire file
            StringBuilder sqlBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                // Skip comments and empty lines
                line = line.trim();
                if (line.isEmpty() || line.startsWith("--")) {
                    continue;
                }
                sqlBuilder.append(line).append(" ");
            }

            // Split into individual SQL statements (separated by semicolons)
            String[] sqlStatements = sqlBuilder.toString().split(";");

            // Execute each statement
            for (String sql : sqlStatements) {
                sql = sql.trim();
                if (!sql.isEmpty()) {
                    try {
                        stmt.execute(sql);
                    } catch (SQLException e) {
                        // Some statements might fail (like DROP TABLE if table doesn't exist)
                        // We can ignore these errors
                        if (!e.getMessage().contains("doesn't exist")) {
                            System.err.println("Warning: " + e.getMessage());
                        }
                    }
                }
            }

            System.out.println("✅ SQL script executed successfully");

        } catch (SQLException | IOException e) {
            System.err.println("❌ Error running SQL script: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Main method for testing database initialization
     */
    public static void main(String[] args) {
        initialize();
    }
}

