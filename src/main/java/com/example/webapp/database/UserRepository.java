package com.example.webapp.database;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserRepository {
    private final DatabaseConnection dbConnection;
    public UserRepository(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }
    public boolean saveUser(String name,String phone,String email,String password) {
        if (name == null || name.trim().isEmpty() ||
            phone == null || phone.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
                System.err.println("Validation failed: All fields are required.");
            return false;
        }
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        String sql = "INSERT INTO users (name, phone, email, password) VALUES (?, ?, ?, ?)";
        try (Connection conn = dbConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, name);
                    pstmt.setString(2, phone);
                    pstmt.setString(3, email);
                    pstmt.setString(4, hashedPassword);
                    int rowsAffected = pstmt.executeUpdate();
                    return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error occurred while saving user: " + e.getMessage());
            return false;
        }
    }

}