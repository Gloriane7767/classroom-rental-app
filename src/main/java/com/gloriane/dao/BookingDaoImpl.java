package com.gloriane.dao;

import com.gloriane.db.DatabaseConnection;
import com.gloriane.model.Booking;
import com.gloriane.model.Customer;
import com.gloriane.model.Classroom;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;

public class BookingDaoImpl implements BookingDao {
    
    private static final Logger logger = Logger.getLogger(BookingDaoImpl.class.getName());
    final DatabaseConnection dbConnection;
    private final CustomerDao customerDao;
    private final ClassroomDao classroomDao;

    public BookingDaoImpl(DatabaseConnection dbConnection, CustomerDao customerDao, ClassroomDao classroomDao) {
        this.dbConnection = dbConnection;
        this.customerDao = customerDao;
        this.classroomDao = classroomDao;
    }

    @Override
    public Booking save(Booking booking) {
        String sql = "INSERT INTO bookings (customer_id, classroom_id, booking_date, start_time, end_time, booking_user_name, comments) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, booking.getCustomerId());
            pstmt.setInt(2, booking.getClassroomId());
            pstmt.setDate(3, Date.valueOf(booking.getBookingDate()));
            pstmt.setTime(4, Time.valueOf(booking.getStartTime()));
            pstmt.setTime(5, Time.valueOf(booking.getEndTime()));
            pstmt.setString(6, booking.getBookingUserName());
            pstmt.setString(7, booking.getComments());
            
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    booking.setId((int) generatedKeys.getLong(1));
                }
            }
            
            logger.info("Booking saved successfully with ID: " + booking.getId());
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error saving booking", e);
            throw new RuntimeException(e);
        }
        
        return booking;
    }

    @Override
    public List<Booking> findAll() {
        String sql = "SELECT * FROM bookings ORDER BY booking_date, start_time";
        List<Booking> bookings = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Booking booking = mapResultSetToBooking(rs);
                loadRelatedData(booking);
                bookings.add(booking);
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding all bookings", e);
            throw new RuntimeException(e);
        }
        
        return bookings;
    }

    @Override
    public Optional<Booking> findById(long id) {
        String sql = "SELECT * FROM bookings WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Booking booking = mapResultSetToBooking(rs);
                    loadRelatedData(booking);
                    return Optional.of(booking);
                }
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding booking by ID", e);
            throw new RuntimeException(e);
        }
        
        return Optional.empty();
    }

    @Override
    public List<Booking> findUpcoming() {
        String sql = "SELECT * FROM bookings WHERE booking_date >= CURDATE() ORDER BY booking_date, start_time";
        List<Booking> bookings = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Booking booking = mapResultSetToBooking(rs);
                loadRelatedData(booking);
                bookings.add(booking);
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding upcoming bookings", e);
            throw new RuntimeException(e);
        }
        
        return bookings;
    }

    @Override
    public List<Booking> findByClassroom(int classroomId) {
        String sql = "SELECT * FROM bookings WHERE classroom_id = ? ORDER BY booking_date, start_time";
        List<Booking> bookings = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, classroomId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Booking booking = mapResultSetToBooking(rs);
                    loadRelatedData(booking);
                    bookings.add(booking);
                }
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding bookings by classroom", e);
            throw new RuntimeException(e);
        }
        
        return bookings;
    }

    @Override
    public List<Booking> findUpcomingByClassroom(int classroomId) {
        String sql = "SELECT * FROM bookings WHERE classroom_id = ? AND booking_date >= CURDATE() ORDER BY booking_date, start_time";
        List<Booking> bookings = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, classroomId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Booking booking = mapResultSetToBooking(rs);
                    loadRelatedData(booking);
                    bookings.add(booking);
                }
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding upcoming bookings by classroom", e);
            throw new RuntimeException(e);
        }
        
        return bookings;
    }

    @Override
    public List<Booking> findByCustomer(int customerId) {
        String sql = "SELECT * FROM bookings WHERE customer_id = ? ORDER BY booking_date, start_time";
        List<Booking> bookings = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, customerId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Booking booking = mapResultSetToBooking(rs);
                    loadRelatedData(booking);
                    bookings.add(booking);
                }
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding bookings by customer", e);
            throw new RuntimeException(e);
        }
        
        return bookings;
    }

    @Override
    public List<Booking> findByDate(LocalDate date) {
        String sql = "SELECT * FROM bookings WHERE booking_date = ? ORDER BY start_time";
        List<Booking> bookings = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, Date.valueOf(date));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Booking booking = mapResultSetToBooking(rs);
                    loadRelatedData(booking);
                    bookings.add(booking);
                }
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding bookings by date", e);
            throw new RuntimeException(e);
        }
        
        return bookings;
    }

    @Override
    public boolean isClassroomAvailable(int classroomId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        String sql = "SELECT COUNT(*) FROM bookings WHERE classroom_id = ? AND booking_date = ? AND ((start_time < ? AND end_time > ?) OR (start_time < ? AND end_time > ?) OR (start_time >= ? AND end_time <= ?))";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, classroomId);
            pstmt.setDate(2, Date.valueOf(date));
            pstmt.setTime(3, Time.valueOf(endTime));
            pstmt.setTime(4, Time.valueOf(startTime));
            pstmt.setTime(5, Time.valueOf(startTime));
            pstmt.setTime(6, Time.valueOf(startTime));
            pstmt.setTime(7, Time.valueOf(startTime));
            pstmt.setTime(8, Time.valueOf(endTime));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == 0;
                }
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error checking classroom availability", e);
            throw new RuntimeException(e);
        }
        
        return false;
    }

    @Override
    public List<Booking> findConflictingBookings(int classroomId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        String sql = "SELECT * FROM bookings WHERE classroom_id = ? AND booking_date = ? AND ((start_time < ? AND end_time > ?) OR (start_time < ? AND end_time > ?) OR (start_time >= ? AND end_time <= ?))";
        List<Booking> bookings = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, classroomId);
            pstmt.setDate(2, Date.valueOf(date));
            pstmt.setTime(3, Time.valueOf(endTime));
            pstmt.setTime(4, Time.valueOf(startTime));
            pstmt.setTime(5, Time.valueOf(startTime));
            pstmt.setTime(6, Time.valueOf(startTime));
            pstmt.setTime(7, Time.valueOf(startTime));
            pstmt.setTime(8, Time.valueOf(endTime));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Booking booking = mapResultSetToBooking(rs);
                    loadRelatedData(booking);
                    bookings.add(booking);
                }
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding conflicting bookings", e);
            throw new RuntimeException(e);
        }
        
        return bookings;
    }

    @Override
    public Booking update(Booking booking) {
        String sql = "UPDATE bookings SET customer_id = ?, classroom_id = ?, booking_date = ?, start_time = ?, end_time = ?, booking_user_name = ?, comments = ? WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, booking.getCustomerId());
            pstmt.setInt(2, booking.getClassroomId());
            pstmt.setDate(3, Date.valueOf(booking.getBookingDate()));
            pstmt.setTime(4, Time.valueOf(booking.getStartTime()));
            pstmt.setTime(5, Time.valueOf(booking.getEndTime()));
            pstmt.setString(6, booking.getBookingUserName());
            pstmt.setString(7, booking.getComments());
            pstmt.setInt(8, booking.getId());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                logger.info("Booking updated successfully");
            } else {
                logger.warning("No booking found with ID: " + booking.getId());
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating booking", e);
            throw new RuntimeException(e);
        }
        
        return booking;
    }

    @Override
    public boolean delete(Booking booking) {
        String sql = "DELETE FROM bookings WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, booking.getId());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                logger.info("Booking deleted successfully");
                return true;
            } else {
                logger.warning("No booking found with ID: " + booking.getId());
                return false;
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error deleting booking", e);
            throw new RuntimeException(e);
        }
    }
    
    private Booking mapResultSetToBooking(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        int customerId = rs.getInt("customer_id");
        int classroomId = rs.getInt("classroom_id");
        
        Date sqlDate = rs.getDate("booking_date");
        LocalDate bookingDate = sqlDate != null ? sqlDate.toLocalDate() : null;
        
        Time sqlStartTime = rs.getTime("start_time");
        LocalTime startTime = sqlStartTime != null ? sqlStartTime.toLocalTime() : null;
        
        Time sqlEndTime = rs.getTime("end_time");
        LocalTime endTime = sqlEndTime != null ? sqlEndTime.toLocalTime() : null;
        
        String bookingUserName = rs.getString("booking_user_name");
        String comments = rs.getString("comments");
        
        return new Booking((int) id, customerId, classroomId, bookingDate,
                startTime, endTime, bookingUserName, comments);
    }
    
    private void loadRelatedData(Booking booking) {
        try {
            Optional<Customer> customer = customerDao.findById(booking.getCustomerId());
            customer.ifPresent(booking::setCustomer);
            
            Optional<Classroom> classroom = classroomDao.findById(booking.getClassroomId());
            classroom.ifPresent(booking::setClassroom);
            
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Could not load related data for booking " + booking.getId(), e);
        }
    }
}
