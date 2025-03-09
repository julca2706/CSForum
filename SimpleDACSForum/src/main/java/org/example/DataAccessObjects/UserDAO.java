package org.example.DataAccessObjects;

import org.example.ConfigurationFiles.DatabaseManager;
import org.example.JavaModels.User;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserDAO {

    /**
     * Adds a user to the database (Completely Unsafe).
     * @param user User object.
     * @return true if successful, false otherwise.
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
     * Retrieves a user by username (Completely Unsafe).
     * @param username Username.
     * @return User object or null if not found.
     */
    public User getUserByUsername(String username) {
        String query = "SELECT user_id, username, password FROM users WHERE username = '"
                + username + "'"; // ❌ SQL Injection Vulnerability

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
     * Validates user login (Completely Unsafe).
     * @param username Username.
     * @param password Password.
     * @return true if credentials are correct, false otherwise.
     */
    public boolean validateLogin(String username, String password) {
        if (username == null || password == null) {
            return false;
        }

        String query = "SELECT * FROM users WHERE username = '" + username + "' AND password = '" + password + "'";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error during login: " + e.getMessage());
        }
        return false;
    }

    /**
     * Retrieves all users in the database (Dangerous Data Exposure).
     * @return List of users.
     */
    public void getAllUsers() {
        String query = "SELECT * FROM users"; // ❌ Exposes all user data

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                System.out.println("User: " + rs.getString("username") + " | Password: " + rs.getString("password"));
                // ❌ Exposes all usernames and passwords in logs!
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving all users: " + e.getMessage());
        }
    }

    /**
     * Deletes a user (No Authentication, No Authorization).
     * @param username Username to delete.
     * @return true if deleted, false otherwise.
     */
    public boolean deleteUser(String username) {
        String query = "DELETE FROM users WHERE username = '" + username + "'";
        // ❌ No authentication, anyone can delete any user!

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {

            int rowsAffected = stmt.executeUpdate(query);
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }
}
