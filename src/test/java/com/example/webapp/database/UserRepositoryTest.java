package com.example.webapp.database;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class UserRepositoryTest {
    @Test
    void testSaveUser() {
        DatabaseConnection dbConnection = new DatabaseConnection();
        UserRepository repo = new UserRepository(dbConnection);
        String uniqueEmail = "testuser" + System.currentTimeMillis() + "@example.com"; 
        boolean success = repo.saveUser("Test User", "1234567890", uniqueEmail, "password123");
        assertTrue(success, "User should be saved successfully in the database");
    }
}