package com.gloriane.dao;

import com.gloriane.db.DatabaseConnection;
import com.gloriane.model.Classroom;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class ClassroomDaoImpl implements ClassroomDao {

    final DatabaseConnection dbConnection;

    public ClassroomDaoImpl(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public Classroom save(Classroom classroom) throws SQLException {
        Connection conn = null;

        try {
            conn = dbConnection.getConnection();
            conn.setAutoCommit(true);

            String classroomSql = "INSERT INTO classrooms (name, capacity, is_accessible) VALUES (?, ?, ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(classroomSql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, classroom.getName());
                pstmt.setInt(2, classroom.getCapacity());
                pstmt.setBoolean(3, classroom.isAccessible());

                pstmt.executeUpdate();

                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        classroom.setId(generatedKeys.getInt(1));
                    }
                }
            }

            if (!classroom.getEquipment().isEmpty()) {
                saveEquipment(conn, classroom.getId(), classroom.getEquipment());
            }

            conn.commit();
            System.out.println("✅ Classroom saved successfully with ID: " + classroom.getId());

        } catch (SQLException e) {
            if (conn == null) {
                try {
                    conn.rollback();
                    System.err.println("❌ Transaction rolled back due to error");
                } catch (SQLException ex) {
                    System.err.println("Error during rollback: " + ex.getMessage());
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
        }

        return classroom;
    }

    public Classroom update(Classroom classroom) throws SQLException {
        Connection conn = null;

        try {
            conn = dbConnection.getConnection();
            conn.setAutoCommit(false);

            String sql = "UPDATE classrooms SET name = ?, capacity = ?, is_accessible = ? WHERE id = ?";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, classroom.getName());
                pstmt.setInt(2, classroom.getCapacity());
                pstmt.setBoolean(3, classroom.isAccessible());
                pstmt.setInt(4, classroom.getId());

                pstmt.executeUpdate();
            }

            deleteEquipment(conn, classroom.getId());

            if (!classroom.getEquipment().isEmpty()) {
                saveEquipment(conn, classroom.getId(), classroom.getEquipment());
            }

            conn.commit();
            System.out.println("✅ Classroom updated successfully");

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Error during rollback: " + ex.getMessage());
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
        }
        return classroom;
    }

    public boolean delete(int classroomId) throws SQLException {
        String sql = "DELETE FROM classrooms WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, classroomId);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Classroom deleted successfully");
            } else {
                System.out.println("⚠️ No classroom found with ID: " + classroomId);
            }
        }
        return false;
    }

    public Optional<Classroom> findById(int id) throws SQLException {
        String sql = "SELECT * FROM classrooms WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Classroom classroom = mapResultSetToClassroom(rs);
                    Set<String> equipment = loadEquipment(conn, classroom.getId());
                    classroom.setEquipment(equipment);
                    return Optional.of(classroom);
                }
            }
        }

        return Optional.empty();
    }

    public List<Classroom> findAll() throws SQLException {
        String sql = "SELECT * FROM classrooms ORDER BY name";
        List<Classroom> classrooms = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Classroom classroom = mapResultSetToClassroom(rs);
                Set<String> equipment = loadEquipment(conn, classroom.getId());
                classroom.setEquipment(equipment);
                classrooms.add(classroom);
            }
        }

        return classrooms;
    }

    public List<Classroom> searchAvailable(int minCapacity, Set<String> requiredEquipment,
                                           boolean needsAccessibility) throws SQLException {
        List<Classroom> allClassrooms = findAll();

        return allClassrooms.stream()
                .filter(classroom -> classroom.meetsRequirements(minCapacity, requiredEquipment, needsAccessibility))
                .sorted(Comparator.comparingInt(Classroom::getCapacity))
                .collect(Collectors.toList());
    }

    public List<Classroom> findAccessible() throws SQLException {
        return findAll().stream()
                .filter(Classroom::isAccessible)
                .collect(Collectors.toList());
    }

    private void saveEquipment(Connection conn, int classroomId, Set<String> equipment) throws SQLException {
        String sql = "INSERT INTO classroom_equipment (classroom_id, equipment_name) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (String item : equipment) {
                pstmt.setInt(1, classroomId);
                pstmt.setString(2, item);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

    private void deleteEquipment(Connection conn, int classroomId) throws SQLException {
        String sql = "DELETE FROM classroom_equipment WHERE classroom_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, classroomId);
            pstmt.executeUpdate();
        }
    }

    private Set<String> loadEquipment(Connection conn, int classroomId) throws SQLException {
        String sql = "SELECT equipment_name FROM classroom_equipment WHERE classroom_id = ?";
        Set<String> equipment = new HashSet<>();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, classroomId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    equipment.add(rs.getString("equipment_name"));
                }
            }
        }
        return equipment;
    }

    private Classroom mapResultSetToClassroom(ResultSet rs) throws SQLException {
        Classroom classroom = new Classroom();
        classroom.setId(rs.getInt("id"));
        classroom.setName(rs.getString("name"));
        classroom.setCapacity(rs.getInt("capacity"));
        classroom.setAccessible(rs.getBoolean("is_accessible"));
        return classroom;
    }
}