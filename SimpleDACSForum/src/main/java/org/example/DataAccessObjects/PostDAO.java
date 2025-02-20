package org.example.DataAccessObjects;

import org.example.ConfigurationFiles.DatabaseManager;
import org.example.JavaModels.Post;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostDAO {

    /**
     * Dodaje nowy post do bazy danych.
     * @param post Obiekt Post zawierający dane.
     * @return true jeśli dodano post, false w przeciwnym razie.
     */
    public static boolean insertPost(Post post) {
        String query = "INSERT INTO posts (username, content, created_at) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, post.username());
            stmt.setString(2, post.content());
            stmt.setTimestamp(3, post.createdAt());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Błąd podczas dodawania posta: " + e.getMessage());
            return false;
        }
    }

    /**
     * Pobiera wszystkie posty z bazy danych.
     * @return Lista postów.
     */
    public List<Post> getAllPosts() {
        List<Post> posts = new ArrayList<>();
        String query = "SELECT * FROM posts ORDER BY created_at ASC";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Post post = new Post(
                        rs.getInt("post_id"),
                        rs.getString("username"),
                        rs.getString("content"),
                        rs.getTimestamp("created_at")
                          // ✅ Now we include username
                );
                posts.add(post);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posts;
    }


    /**
     * Pobiera posty danego użytkownika.
     * @param username użytkownika.
     * @return Lista postów użytkownika.
     */
    public static List<Post> getPostsByUser(String username) {
        List<Post> posts = new ArrayList<>();
        String query = "SELECT * FROM posts WHERE username = ? ORDER BY created_at DESC";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                posts.add(new Post(
                        rs.getInt("post_id"),
                        rs.getString("username"),
                        rs.getString("content"),
                        rs.getTimestamp("created_at")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Błąd podczas pobierania postów użytkownika: " + e.getMessage());
        }
        return posts;
    }

    /**
     * Usuwa post z bazy danych na podstawie ID.
     * @param postId ID posta do usunięcia.
     * @return true jeśli post został usunięty, false jeśli nie znaleziono posta.
     */
    public static boolean deletePost(int postId) {
        if (postId <= 0) {
            System.err.println("Błąd: Nieprawidłowy `postId`.");
            return false;
        }

        String query = "DELETE FROM posts WHERE post_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, postId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Błąd podczas usuwania posta: " + e.getMessage());
            return false;
        }
    }

    /**
     * Pobiera pojedynczy post na podstawie ID.
     * @param postId ID posta.
     * @return Obiekt Post lub null jeśli nie znaleziono.
     */
    public static Post getPostById(int postId) {
        if (postId <= 0) {
            System.err.println("Błąd: Nieprawidłowy `postId`.");
            return null;
        }

        String query = "SELECT * FROM posts WHERE post_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, postId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Post(
                        rs.getInt("post_id"),
                        rs.getString("username"),
                        rs.getString("content"),
                        rs.getTimestamp("created_at")
                );
            }

        } catch (SQLException e) {
            System.err.println("Błąd podczas pobierania posta: " + e.getMessage());
        }
        return null;
    }
}
