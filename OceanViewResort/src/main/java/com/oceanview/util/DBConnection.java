package com.oceanview.util;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static DBConnection instance;
    private Connection connection;
    private String url;

    private DBConnection() {
        try {
            // Register JDBC driver
            Class.forName("org.postgresql.Driver");

            // Load dotenv
            Dotenv dotenv = null;
            try {
                // Ignore missing .env here and fall back to System variables if needed
                dotenv = Dotenv.configure().ignoreIfMissing().load();
            } catch (DotenvException e) {
                System.out.println("Could not load .env file, falling back to System environment variables.");
            }

            url = (dotenv != null && dotenv.get("DB_URL") != null) ? dotenv.get("DB_URL") : System.getenv("DB_URL");
            
            if (url == null) {
                // If not via standard means, provide default (useful for local Tomcat running where working dir is bin/)
                System.out.println("WARNING: DB variables not found in environment, falling back to localhost defaults.");
                url = "jdbc:postgresql://localhost:5432/oceanview?user=postgres&password=root";
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("PostgreSQL JDBC Driver not found.", e);
        }
    }

    public static synchronized DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(url);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get database connection. URL=" + url, e);
        }
        return connection;
    }
}
