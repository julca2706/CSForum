package org.example.DataAccessObjects;

import org.example.ConfigurationFiles.DatabaseManager;
import org.example.JavaModels.User;

import java.sql.*;

public class UserDAO {

    /**
     * Adds a user to the database (❌ Vulnerable to SQL Injection).
     * @param user User object.
     * @return true if registration is successful, false otherwise.
     */
    public boolean addUser(User user) {
        String query = "INSERT INTO users (username, password) VALUES (?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, user.username());
            stmt.setString(2, user.password()); // Now stores hashed password
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error adding user: " + e.getMessage());
            return false;
        }
    }


    /**
     * Retrieves a user by username (❌ Vulnerable to SQL Injection).
     * @param username Username.
     * @return User object or null if not found.
     */
    public User getUserByUsername(String username) {
        String query = "SELECT user_id, username, password FROM users WHERE username = ?"; // ✅ Safe query

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(rs.getString("username"), rs.getString("password"), rs.getInt("user_id"));
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving user: " + e.getMessage());
        }
        return null;
    }

}
