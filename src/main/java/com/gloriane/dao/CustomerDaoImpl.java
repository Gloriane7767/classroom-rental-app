package com.gloriane.dao;

import com.gloriane.db.DatabaseConnection;
import com.gloriane.model.Customer;
import com.gloriane.model.CustomerType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CustomerDaoImpl implements CustomerDao{

    final DatabaseConnection dbConnection;

    public CustomerDaoImpl(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public Customer save(Customer customer) throws SQLException {
        String sql = "INSERT INTO customers (name, email, phone, type) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getMySQLConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, customer.getName());
            pstmt.setString(2, customer.getEmail());
            pstmt.setString(3, customer.getPhone());
            pstmt.setString(4, customer.getType().name());

            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    Customer savedCustomer = new Customer(
                        generatedId,
                        customer.getName(),
                        customer.getEmail(),
                        customer.getPhone(),
                        customer.getType()
                    );
                    System.out.println("✅ Customer saved successfully with ID: " + generatedId);
                    return savedCustomer;
                }
            }
            
            return customer;
        }
    }

    @Override
    public Optional<Customer> update(Customer customer) throws SQLException {
        String sql = "UPDATE customers SET name = ?, email = ?, phone = ?, type = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getMySQLConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, customer.getName());
            pstmt.setString(2, customer.getEmail());
            pstmt.setString(3, customer.getPhone());
            pstmt.setString(4, customer.getType().name());
            pstmt.setInt(5, customer.getId());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Customer updated successfully");
                return Optional.of(customer);
            } else {
                System.out.println("⚠️ No customer found with ID: " + customer.getId());
                return Optional.empty();
            }
        }
    }

    @Override
    public boolean delete(int customerId) throws SQLException {
        String sql = "DELETE FROM customers WHERE id = ?";

        try (Connection conn = DatabaseConnection.getMySQLConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, customerId);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Customer deleted successfully (ID: " + customerId + ")");
                return true;
            } else {
                System.out.println("⚠️ No customer found with ID: " + customerId);
                return false;
            }
        }
    }

    @Override
    public Optional<Customer> findById(int id) throws SQLException {
        String sql = "SELECT * FROM customers WHERE id = ?";

        try (Connection conn = DatabaseConnection.getMySQLConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Customer customer = mapResultSetToCustomer(rs);
                    return Optional.of(customer);
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public List<Customer> findAll() throws SQLException {
        String sql = "SELECT * FROM customers ORDER BY name";
        List<Customer> customers = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getMySQLConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Customer customer = mapResultSetToCustomer(rs);
                customers.add(customer);
            }
        }

        return customers;
    }

    @Override
    public List<Customer> findByType(CustomerType type) throws SQLException {
        String sql = "SELECT * FROM customers WHERE type = ?";
        List<Customer> customers = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getMySQLConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, type.name());

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    customers.add(mapResultSetToCustomer(rs));
                }
            }
        }

        return customers;
    }

    @Override
    public List<Customer> searchByName(String pattern) throws SQLException {
        String sql = "SELECT * FROM customers WHERE name LIKE ?";
        List<Customer> customers = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getMySQLConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + pattern + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    customers.add(mapResultSetToCustomer(rs));
                }
            }
        }

        return customers;
    }

    @Override
    public boolean existsByEmail(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM customers WHERE email = ?";

        try (Connection conn = DatabaseConnection.getMySQLConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }

        return false;
    }

    private Customer mapResultSetToCustomer(ResultSet rs) throws SQLException {
        String typeStr = rs.getString("type");
        CustomerType type = typeStr != null && !typeStr.trim().isEmpty() ? CustomerType.valueOf(typeStr.trim()) : CustomerType.INDIVIDUAL;
        
        return new Customer(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("email"),
            rs.getString("phone"),
            type
        );
    }
}