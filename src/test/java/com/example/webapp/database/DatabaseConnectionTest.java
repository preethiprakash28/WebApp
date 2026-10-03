package com.example.webapp.database;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
class DatabaseConnectionTest {
    
    @Test
    void testJdbcUrlConstruction() {
        DatabaseConnection db = new DatabaseConnection("localhost", "3306", "testdb", "user", "pass");
        
        assertEquals("jdbc:mysql://localhost:3306/testdb", db.getJdbcUrl());
    }
}