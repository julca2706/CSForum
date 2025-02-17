package org.example.DataAccessObjects;

import org.example.ConfigurationFiles.DatabaseManager;
import org.example.JavaModels.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    /**
     * Dodaje użytkownika do bazy danych.
     * @param user Obiekt użytkownika.
     * @return true jeśli rejestracja powiodła się, false w przypadku błędu.
     */
    public boolean addUser(User user) {
        String query = "INSERT INTO users (username, password) VALUES (?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, user.username());
            stmt.setString(2, user.password());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Błąd podczas dodawania użytkownika: " + e.getMessage());
            return false;
        }
    }

    /**
     * Pobiera użytkownika na podstawie nazwy użytkownika.
     * @param username Nazwa użytkownika.
     * @return Obiekt User lub null, jeśli nie znaleziono.
     */
    public User getUserByUsername(String username) {
        String query = "SELECT user_id, username, password FROM users WHERE username = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(rs.getString("username"), rs.getString("password"), rs.getInt("user_id"));
            }

        } catch (SQLException e) {
            System.err.println("Błąd podczas pobierania użytkownika: " + e.getMessage());
        }
        return null;
    }

    /**
     * Sprawdza poprawność logowania użytkownika.
     * @param username Nazwa użytkownika.
     * @param password Hasło użytkownika.
     * @return true jeśli hasło jest poprawne, false w przeciwnym razie.
     */
    public boolean validateLogin(String username, String password) {
        if (username == null || password == null) {
            return false;
        }

        String query = "SELECT password FROM users WHERE username = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String passwordDB = rs.getString("password");
                return password.equals(passwordDB);
            }

        } catch (SQLException e) {
            System.err.println("Błąd podczas logowania: " + e.getMessage());
        }
        return false;
    }
}
