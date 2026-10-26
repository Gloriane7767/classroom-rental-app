# Classroom Rental App

A Java-based classroom booking and management system designed to help organizations manage room availability, customer bookings, and facility requirements efficiently.

This project demonstrates core Java object-oriented design, database persistence with MySQL, and practical business logic for scheduling and booking rooms.

## Overview

The Classroom Rental App allows users to:
- manage customer records,
- track classroom availability,
- assign equipment to rooms,
- create and validate bookings,
- prevent scheduling conflicts,
- support accessibility and capacity-based filtering.

The application uses a Java command-line style architecture backed by MySQL and SQL schema definitions.

## Features

- Customer management for both individual and company clients
- Classroom catalog with capacity and equipment tracking
- Accessibility-aware classroom filtering
- Booking creation and validation
- Conflict detection for overlapping bookings
- Database-backed persistence using JDBC and SQL
- Data model separation by domain layer and DAO layer
- Easy setup with Maven and MySQL

## Tech Stack

- Java 21
- Maven
- MySQL
- JDBC / SQL
- Java standard libraries

## Project Structure

```text
classroom-rental-app/
├── .gitignore
├── pom.xml
├── README.md
├── src/
│   ├── README.md
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── gloriane/
│       │           ├── Main.java
│       │           ├── dao/
│       │           │   ├── BookingDao.java
│       │           │   ├── BookingDaoImpl.java
│       │           │   ├── ClassroomDao.java
│       │           │   ├── ClassroomDaoImpl.java
│       │           │   ├── CustomerDao.java
│       │           │   └── CustomerDaoImpl.java
│       │           ├── db/
│       │           │   ├── DatabaseConnection.java
│       │           │   └── Databaseinitializer.java
│       │           ├── model/
│       │           │   ├── Booking.java
│       │           │   ├── Classroom.java
│       │           │   ├── Customer.java
│       │           │   └── CustomerType.java
│       │           └── util/
│       │               ├── BookingHelper.java
│       │               └── ClassroomHelper.java
│       └── resources/
│           └── schema.sql
```

## Core Domain Model

The application is organized around these main entities:

- `Customer`: stores customer identity and type
- `Classroom`: stores classroom details, capacity, accessibility, and equipment
- `Booking`: represents a reservation with date, time, customer, and classroom

## Database Design

The database schema includes:

- `customers`
- `classrooms`
- `classroom_equipment`
- `bookings`

The schema is defined in:

- `src/main/resources/schema.sql`

This file creates the tables, inserts sample data, and includes useful example queries for validation and testing.

## Key Business Rules

The app includes logic for:
- preventing bookings with an end time earlier than or equal to the start time,
- preventing bookings in the past,
- checking time overlap between bookings for the same room,
- validating classroom requirements such as minimum capacity, required equipment, and accessibility,
- keeping equipment lists protected and consistent.

## Prerequisites

Before running the project, ensure you have:

- Java 21+
- Maven
- MySQL Server
- MySQL Workbench or another SQL client (optional)

## Setup Instructions

### 1. Clone the repository

```bash
git clone https://github.com/Gloriane7767/classroom-rental-app.git
cd classroom-rental-app
```

### 2. Configure MySQL

Create a MySQL database and update your database connection settings as needed.

### 3. Initialize the schema

Run the SQL script in `src/main/resources/schema.sql` to create the tables and load sample data.

### 4. Build the project

```bash
mvn clean compile
```

### 5. Run the application

Run the `Main` class from your IDE or use a Java launcher that points to:

```text
com.gloriane.Main
```

## Application Flow

The app initializes the database when started via `Main`, which triggers the database setup process.

The underlying design follows a clean separation:
- `model` contains data objects,
- `dao` contains database interaction code,
- `db` handles database configuration and initialization,
- `util` contains helper logic for validation and scheduling support.

## Example Use Cases

- A trainer books a room for a workshop with a projector and whiteboard.
- A manager checks whether a room with 20 seats and accessible access is available.
- A student or staff member reserves a classroom for a seminar and receives validation feedback for conflicts.
- An administrator reviews bookings by date, customer, or room.

## Sample Database Queries

The schema file includes example SQL queries for checking:
- all customers,
- all classrooms and capacity,
- classroom equipment,
- bookings by date, room, or customer.

## Why This Project Matters

This project is a strong example of:
- object-oriented programming in Java,
- database design and SQL integration,
- business rule validation,
- domain-driven modeling,
- practical software architecture for a real application.

## License

This project is open for learning and demonstration purposes. Please review the repository license if added in the future.

## Contributor Notes

This repository is structured for educational and project-based learning. It is well suited for:
- Java coursework,
- database-driven application practice,
- interview or portfolio demonstration,
- OOP and JDBC learning exercises.

## Future Enhancements

Possible enhancements include:
- a graphical user interface,
- user authentication and authorization,
- advanced search and filtering,
- booking history and reporting,
- email or notification reminders,
- REST API integration.

## Conclusion

The Classroom Rental App is a practical Java application showcasing real-world concepts in booking systems, database persistence, and validation logic. It provides a strong foundation for extending into a more complete enterprise application.
