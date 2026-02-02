package com.gloriane.dao;

import com.gloriane.model.Booking;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface BookingDao {
    Booking save(Booking booking);
    List<Booking> findAll();
    Optional<Booking> findById(long id);
    List<Booking> findUpcoming();
    List<Booking> findByClassroom(int classroomId);
    List<Booking> findUpcomingByClassroom(int classroomId);
    List<Booking> findByCustomer(int customerId);
    List<Booking> findByDate(LocalDate date);
    boolean isClassroomAvailable(int classroomId, LocalDate date, LocalTime startTime, LocalTime endTime);
    List<Booking> findConflictingBookings(int classroomId, LocalDate date, LocalTime startTime, LocalTime endTime);
    Booking update(Booking booking);
    boolean delete(Booking booking);
}