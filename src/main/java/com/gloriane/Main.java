package com.gloriane;

import com.gloriane.dao.ClassroomDaoImpl;
import com.gloriane.dao.CustomerDaoImpl;
import com.gloriane.db.DatabaseConnection;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello and welcome!");

        DatabaseConnection dbConnection = new DatabaseConnection();
        CustomerDaoImpl customerDao = new CustomerDaoImpl();
        ClassroomDaoImpl classroomDao = new ClassroomDaoImpl();

    }
}
