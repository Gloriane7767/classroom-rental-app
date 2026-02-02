package com.gloriane.db;

import com.mysql.cj.jdbc.MysqlDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/rental_app_db";
    private static final String USER = "root";
    private static final String PASSWORD = "@Gloriane--001";

    private static Connection connection;
    private static DataSource dataSource;

    // Singleton instance
    private static DatabaseConnection instance;

    // private constructor for Singleton
    public DatabaseConnection() {

    }

    // Return a (cached) JDBC Connection to the database.
    public static Connection getMySQLConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = getMySQLDataSource().getConnection();
        }
        return connection;
    }

    public static DataSource getMySQLDataSource() {
        if (dataSource == null) {
            MysqlDataSource mysqlDataSource = new MysqlDataSource();
            mysqlDataSource.setUrl(URL);
            mysqlDataSource.setUser(USER);
            mysqlDataSource.setPassword(PASSWORD);
            dataSource = mysqlDataSource;
        }
        return dataSource;
    }

    // Singleton accessor
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    // Tests if we can connect to the database.
    public boolean testConnection() {
        // Use a fresh connection from the DataSource for the test
        try (Connection conn = getMySQLDataSource().getConnection()) {
            System.out.println("✅ Database connection successful!");
            System.out.println("Connected to: " + conn.getMetaData().getDatabaseProductName());
            return true;
        } catch (SQLException e) {
            System.err.println("❌ Database connection failed!");
            System.err.println("Error: " + e.getMessage());
            return false;
        }
    }

    // Closes a connection safely
    public void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        DatabaseConnection db = DatabaseConnection.getInstance();
        boolean ok = db.testConnection();
        System.out.println("Connection test result: " + ok);
    }

    public Connection getConnection() {
    }
}
