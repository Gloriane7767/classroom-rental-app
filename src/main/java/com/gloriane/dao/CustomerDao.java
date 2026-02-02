package com.gloriane.dao;

import com.gloriane.model.Customer;
import com.gloriane.model.CustomerType;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface CustomerDao {
    Customer save(Customer customer) throws SQLException;
    Optional<Customer> update(Customer customer) throws SQLException;
    boolean delete(int id) throws SQLException;
    Optional<Customer> findById(int id) throws SQLException;
    List<Customer> findAll() throws SQLException;
    List<Customer> findByType(CustomerType type) throws SQLException;
    List<Customer> searchByName(String pattern) throws SQLException;
    boolean existsByEmail(String email) throws SQLException;
}
