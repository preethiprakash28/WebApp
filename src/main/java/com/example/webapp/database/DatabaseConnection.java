package com.example.webapp.database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class DatabaseConnection {
    private final String host;
    private final String port;
    private final String database;
    private final String user;
    private final String password;
    public DatabaseConnection(String host, String port, String database, String user, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.user = user;
        this.password = password;
    }
    public DatabaseConnection() {
        this(
            System.getenv().getOrDefault("DB_HOST", "localhost"),
            System.getenv().getOrDefault("DB_PORT", "3306"),
            System.getenv().getOrDefault("DB_NAME", "webapp"),
            System.getenv().getOrDefault("DB_USER", "root"),
            System.getenv().getOrDefault("DB_PASSWORD", "")
        );
    }
    public String getJdbcUrl() {
        return "jdbc:mysql://" + host + ":" + port + "/" + database;
    }
    public Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(getJdbcUrl(), user, password);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found in classpath.", e);
        }
    }
}