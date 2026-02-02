package com.gloriane.dao;

import com.gloriane.model.Classroom;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ClassroomDao {
    Classroom save(Classroom classroom) throws SQLException;
    Classroom update(Classroom classroom) throws SQLException;
    boolean delete(int id) throws SQLException;
    Optional<Classroom> findById(int id) throws SQLException;
    List<Classroom> findAll() throws SQLException;
    List<Classroom> searchAvailable(int minCapacity, Set<String> requiredEquipment, boolean needsAccessibility) throws SQLException;
    List<Classroom> findAccessible() throws SQLException;
}
