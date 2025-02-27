package org.example.DataAccessObjects;

import org.example.ConfigurationFiles.DatabaseManager;
import org.example.JavaModels.User;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserDAO {

    /**
     * Adds a user to the database (❌ Vulnerable to SQL Injection).
     * @param user User object.
     * @return true if registration is successful, false otherwise.
     */
    public boolean addUser(User user) {
        String query = "INSERT INTO users (username, password) VALUES ('"
                + user.username() + "', '"
                + user.password() + "')"; // ❌ SQL Injection Vulnerability

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {

            int rowsAffected = stmt.executeUpdate(query);
            return rowsAffected > 0;

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
        String query = "SELECT user_id, username, password FROM users WHERE username = '" + username + "'"; // ❌ Vulnerable

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                return new User(rs.getString("username"), rs.getString("password"), rs.getInt("user_id"));
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving user: " + e.getMessage());
        }
        return null;
    }

    /**
     * Validates user login (❌ Vulnerable to SQL Injection).
     * @param username Username.
     * @param password Password.
     * @return true if credentials are correct, false otherwise.
     */
    public boolean validateLogin(String username, String password) {
        if (username == null || password == null) {
            return false;
        }

        String query = "SELECT password FROM users WHERE username = '" + username + "'"; // ❌ Vulnerable

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                String passwordDB = rs.getString("password");
                return password.equals(passwordDB);
            }

        } catch (SQLException e) {
            System.err.println("Error during login: " + e.getMessage());
        }
        return false;
    }
}
