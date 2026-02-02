-- ===================================================================
-- CLASSROOM RENTAL APPLICATION - DATABASE SCHEMA
-- ===================================================================
-- This file creates all the tables needed for our application
-- Think of tables like spreadsheets - they have columns and rows
-- ===================================================================

-- First, we drop (delete) existing tables if they exist
-- This is helpful when we want to start fresh
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS classroom_equipment;
DROP TABLE IF EXISTS classrooms;
DROP TABLE IF EXISTS customers;

-- ===================================================================
-- TABLE 1: CUSTOMERS
-- ===================================================================
-- This table stores information about all our customers
-- (both companies and individual people)
-- ===================================================================

CREATE TABLE customers (
    -- id: A unique number for each customer (like a student ID number)
    -- PRIMARY KEY means this is the main identifier
    -- AUTO_INCREMENT means the database automatically gives the next number
    id INT PRIMARY KEY AUTO_INCREMENT,
    
    -- name: The customer's name (can't be empty - NOT NULL)
    name VARCHAR(100) NOT NULL,
    
    -- email: Customer's email address
    email VARCHAR(100) NOT NULL,
    
    -- phone: Customer's phone number
    phone VARCHAR(20),
    
    -- type: Either 'COMPANY' or 'INDIVIDUAL'
    -- We use ENUM to restrict to only these two values
    type ENUM('COMPANY', 'INDIVIDUAL') NOT NULL
);

-- ===================================================================
-- TABLE 2: CLASSROOMS
-- ===================================================================
-- This table stores information about our 20 training rooms
-- ===================================================================

CREATE TABLE classrooms (
    id INT PRIMARY KEY AUTO_INCREMENT,
    
    -- name: The room name (must be unique - no two rooms with same name)
    name VARCHAR(50) NOT NULL UNIQUE,
    
    -- capacity: How many people can sit (must be positive number)
    capacity INT NOT NULL CHECK (capacity > 0),
    
    -- is_accessible: 1 = yes (accessible), 0 = no (not accessible)
    -- BOOLEAN is stored as TINYINT in MySQL (0 or 1)
    is_accessible BOOLEAN NOT NULL DEFAULT FALSE
);

-- ===================================================================
-- TABLE 3: CLASSROOM_EQUIPMENT
-- ===================================================================
-- This table stores what equipment each classroom has
-- 
-- Why a separate table?
-- Because one classroom can have MULTIPLE pieces of equipment!
-- Example: Room A has: Projector, Whiteboard, Computer
-- 
-- This is called a "one-to-many" relationship
-- ===================================================================

CREATE TABLE classroom_equipment (
    id INT PRIMARY KEY AUTO_INCREMENT,
    
    -- classroom_id: Which classroom does this equipment belong to?
    -- FOREIGN KEY means it must match an id in the classrooms table
    classroom_id INT NOT NULL,
    
    -- equipment_name: The name of the equipment (e.g., "Projector")
    equipment_name VARCHAR(50) NOT NULL,
    
    -- This creates a link to the classrooms table
    -- If a classroom is deleted, all its equipment entries are also deleted (CASCADE)
    FOREIGN KEY (classroom_id) REFERENCES classrooms(id) ON DELETE CASCADE,
    
    -- This ensures we don't add the same equipment twice to a room
    UNIQUE KEY unique_classroom_equipment (classroom_id, equipment_name)
);

-- ===================================================================
-- TABLE 4: BOOKINGS
-- ===================================================================
-- This table stores all reservations of classrooms by customers
-- ===================================================================

CREATE TABLE bookings (
    id INT PRIMARY KEY AUTO_INCREMENT,
    
    -- customer_id: Which customer made this booking?
    customer_id INT NOT NULL,
    
    -- classroom_id: Which room is being booked?
    classroom_id INT NOT NULL,
    
    -- booking_date: What day is the booking for?
    booking_date DATE NOT NULL,
    
    -- start_time: What time does it start?
    start_time TIME NOT NULL,
    
    -- end_time: What time does it end?
    end_time TIME NOT NULL,
    
    -- booking_user_name: Name of the person who created this booking
    booking_user_name VARCHAR(100) NOT NULL,
    
    -- comments: Optional notes (allergies, special requirements, etc.)
    -- TEXT allows for long comments
    comments TEXT,
    
    -- Foreign keys linking to customers and classrooms tables
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE,
    FOREIGN KEY (classroom_id) REFERENCES classrooms(id) ON DELETE CASCADE,
    
    -- Constraint to ensure start_time is before end_time
    CHECK (start_time < end_time)
);

-- ===================================================================
-- INDEXES FOR BETTER PERFORMANCE
-- ===================================================================
-- Indexes make searching faster (like an index in a book)
-- We create indexes on columns we'll search often
-- ===================================================================

-- We often search bookings by date
CREATE INDEX idx_bookings_date ON bookings(booking_date);

-- We often search bookings by classroom
CREATE INDEX idx_bookings_classroom ON bookings(classroom_id);

-- We often search bookings by customer
CREATE INDEX idx_bookings_customer ON bookings(customer_id);

-- We often search equipment by classroom
CREATE INDEX idx_equipment_classroom ON classroom_equipment(classroom_id);

-- ===================================================================
-- SAMPLE DATA FOR TESTING
-- ===================================================================
-- Let's add some example data so we can test our application
-- ===================================================================

-- Add some customers
INSERT INTO customers (name, email, phone, type) VALUES
('ABC Corporation', 'contact@abc.com', '555-0101', 'COMPANY'),
('John Smith', 'john.smith@email.com', '555-0102', 'INDIVIDUAL'),
('Tech Solutions Inc', 'info@techsolutions.com', '555-0103', 'COMPANY'),
('Jane Doe', 'jane.doe@email.com', '555-0104', 'INDIVIDUAL'),
('Global Training Ltd', 'hello@globaltraining.com', '555-0105', 'COMPANY');

-- Add 20 classrooms with different capacities and accessibility
INSERT INTO classrooms (name, capacity, is_accessible) VALUES
('Innovation Lab', 30, TRUE),
('Conference Room A', 50, TRUE),
('Conference Room B', 50, TRUE),
('Training Room 1', 25, TRUE),
('Training Room 2', 25, FALSE),
('Training Room 3', 20, FALSE),
('Executive Boardroom', 15, TRUE),
('Small Meeting Room 1', 10, TRUE),
('Small Meeting Room 2', 10, FALSE),
('Workshop Space', 40, TRUE),
('Seminar Hall', 100, TRUE),
('Computer Lab', 30, TRUE),
('Creative Studio', 20, FALSE),
('Strategy Room', 12, TRUE),
('Breakout Room 1', 8, TRUE),
('Breakout Room 2', 8, FALSE),
('Auditorium', 200, TRUE),
('Focus Room', 6, TRUE),
('Collaboration Space', 35, TRUE),
('Presentation Theater', 80, TRUE);

-- Add equipment to classrooms
INSERT INTO classroom_equipment (classroom_id, equipment_name) VALUES
-- Innovation Lab (id: 1)
(1, 'Projector'),
(1, 'Whiteboard'),
(1, 'Sound System'),
(1, 'Video Conferencing'),
-- Conference Room A (id: 2)
(2, 'Projector'),
(2, 'Whiteboard'),
(2, 'Video Conferencing'),
-- Conference Room B (id: 3)
(3, 'Projector'),
(3, 'Whiteboard'),
-- Training Room 1 (id: 4)
(4, 'Projector'),
(4, 'Whiteboard'),
(4, 'Flip Chart'),
-- Training Room 2 (id: 5)
(5, 'Projector'),
(5, 'Whiteboard'),
-- Seminar Hall (id: 11)
(11, 'Projector'),
(11, 'Sound System'),
(11, 'Stage'),
(11, 'Microphone'),
-- Computer Lab (id: 12)
(12, 'Computers'),
(12, 'Projector'),
(12, 'Whiteboard'),
-- Auditorium (id: 17)
(17, 'Projector'),
(17, 'Sound System'),
(17, 'Stage'),
(17, 'Lighting'),
(17, 'Microphone'),
-- Presentation Theater (id: 20)
(20, 'Projector'),
(20, 'Sound System'),
(20, 'Video Conferencing');

-- Add some sample bookings (using tomorrow's date to keep them "upcoming")
INSERT INTO bookings (customer_id, classroom_id, booking_date, start_time, end_time, booking_user_name, comments) VALUES
-- Tomorrow's bookings
(1, 1, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '09:00:00', '12:00:00', 'Sarah Johnson', 'Team training session - need extra chairs'),
(2, 4, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '14:00:00', '16:00:00', 'John Smith', 'Personal coaching session'),
-- Day after tomorrow
(3, 2, DATE_ADD(CURDATE(), INTERVAL 2 DAY), '10:00:00', '15:00:00', 'Mike Chen', 'Client presentation - VIP guests'),
(4, 7, DATE_ADD(CURDATE(), INTERVAL 2 DAY), '13:00:00', '14:30:00', 'Jane Doe', NULL),
-- 3 days from now
(5, 11, DATE_ADD(CURDATE(), INTERVAL 3 DAY), '09:00:00', '17:00:00', 'David Wilson', 'Annual conference - catering required. Attendee has nut allergy.');

-- ===================================================================
-- VERIFICATION QUERIES
-- ===================================================================
-- These are example queries you can run to check if everything worked
-- ===================================================================

-- Show all customers
-- SELECT * FROM customers;

-- Show all classrooms with their capacity
-- SELECT name, capacity, is_accessible FROM classrooms ORDER BY capacity DESC;

-- Show each classroom with its equipment
-- SELECT c.name, e.equipment_name 
-- FROM classrooms c 
-- LEFT JOIN classroom_equipment e ON c.id = e.classroom_id
-- ORDER BY c.name;

-- Show all bookings with customer and classroom names
-- SELECT 
--     b.id,
--     c.name AS customer_name,
--     cl.name AS classroom_name,
--     b.booking_date,
--     b.start_time,
--     b.end_time,
--     b.booking_user_name
-- FROM bookings b
-- JOIN customers c ON b.customer_id = c.id
-- JOIN classrooms cl ON b.classroom_id = cl.id
-- ORDER BY b.booking_date, b.start_time;
