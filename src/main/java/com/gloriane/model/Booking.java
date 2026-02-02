package com.gloriane.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Booking {
    private int id;
    private int customerId;
    private int classroomId;
    private LocalDate bookingDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String bookingUserName;
    private String comments;

    // This keeps references to the actual Customer and Classroom objects
    // This makes it easier to get full information without always querying the database
    private Customer customer;
    private Classroom classroom;

    //Constructor for creating a NEW booking (without ID). Use this when someone is making a new reservation
    public Booking(int customerId, int classroomId, LocalDate bookingDate,
                   LocalTime startTime, LocalTime endTime, String bookingUserName, String comments) {
        this.customerId = customerId;
        this.classroomId = classroomId;
        this.bookingDate = bookingDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.bookingUserName = bookingUserName;
        this.comments = comments;
    }

    // Constructor for loading an EXISTING booking from database
    public Booking(int id, int customerId, int classroomId, LocalDate bookingDate,
                   LocalTime startTime, LocalTime endTime, String bookingUserName, String comments) {
        this.id = id;
        this.customerId = customerId;
        this.classroomId = classroomId;
        this.bookingDate = bookingDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.bookingUserName = bookingUserName;
        this.comments = comments;
    }

    // ===== GETTERS AND SETTERS =====

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getClassroomId() {
        return classroomId;
    }

    public void setClassroomId(int classroomId) {
        this.classroomId = classroomId;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getBookingUserName() {
        return bookingUserName;
    }

    public void setBookingUserName(String bookingUserName) {
        this.bookingUserName = bookingUserName;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Classroom getClassroom() {
        return classroom;
    }

    public void setClassroom(Classroom classroom) {
        this.classroom = classroom;
    }


    // ===== HELPER METHODS =====
    // These methods provide business logic functionality for booking operations
    public boolean overlapsWith(LocalDate date, LocalTime start, LocalTime end) {
        // First check: Must be the same date
        if (!this.bookingDate.equals(date)) {
            return false; // Different days = no conflict
        }

        // Check if times overlap.
        return this.startTime.isBefore(end) && this.endTime.isAfter(start);
    }

         // Helper method: Checks if this booking is for a future date/time. This is useful for showing "upcoming bookings"
         // true if booking is in the future
    public boolean isUpcoming() {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        // Future if: date is after today, OR (date is today AND time hasn't passed yet)
        return this.bookingDate.isAfter(today)
                || (this.bookingDate.equals(today) && this.startTime.isAfter(now));
    }

    // Helper method: Validates that this booking makes sense.Returns null if valid, or an error message if something is wrong null
    // if valid, error message if invalid
    public String validate() {
        // Check 1: Start time must be before end time
        if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
            return "Start time must be before end time";
        }

        // Check 2: Can't book in the past
        LocalDate today = LocalDate.now();
        if (bookingDate.isBefore(today)) {
            return "Cannot book a date in the past";
        }

        // Check 3: If booking is today, start time can't be in the past
        if (bookingDate.equals(today) && startTime.isBefore(LocalTime.now())) {
            return "Cannot book a time that has already passed";
        }

        // All checks passed!
        return null;
    }

    @Override
    public String toString() {
        return String.format("Booking[id=%d, customer=%s, classroom=%s, date=%s, time=%s-%s, user=%s]",
                id,
                customer != null ? customer.getName() : "ID:" + customerId,
                classroom != null ? classroom.getName() : "ID:" + classroomId,
                bookingDate, startTime, endTime, bookingUserName);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Booking booking = (Booking) obj;
        return id == booking.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
