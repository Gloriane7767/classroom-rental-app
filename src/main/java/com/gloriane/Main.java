package com.gloriane;

import com.gloriane.db.Databaseinitializer;

public class Main {
    public static void main(String[] args) {
        Databaseinitializer initializer = new Databaseinitializer();
        Databaseinitializer.initialize();
    }
}