package org.example.BusinessLogic;

import org.example.DataAccessObjects.UserDAO;
import org.example.JavaModels.User;
import org.example.security.SessionManager;
import org.mindrot.jbcrypt.BCrypt; // ✅ Import BCrypt

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AuthService {
    private final UserDAO userDAO = new UserDAO();
    private static final int MAX_ATTEMPTS = 5;
    private static final long BLOCK_TIME_MS = 15 * 60 * 1000;
    private static final Map<String, Integer> failedAttempts = new ConcurrentHashMap<>();
    private static final Map<String, Long> blockedUsers = new ConcurrentHashMap<>();

    /**
     * Registers a new user with secure password hashing.
     */
    public boolean registerUser(String username, String password) {
        User existingUser = userDAO.getUserByUsername(username);
        System.out.println("Checking if user exists: " + username);

        if (existingUser != null) {
            System.out.println("User already exists! Registration failed.");
            return false;
        }

        // ✅ Securely hash the password using BCrypt
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12)); // 12 = cost factor
        User newUser = new User(username, hashedPassword, 0);
        boolean success = userDAO.addUser(newUser);
        System.out.println(success ? "User registered successfully!" : "User registration failed.");
        return success;
    }

    /**
     * Logs in a user and prevents brute force attempts.
     */
    public String loginUser(String username, String password) {
        // Check if user is blocked due to too many failed attempts
        if (isUserBlocked(username)) {
            System.out.println("User " + username + " is temporarily blocked from logging in.");
            return null;
        }

        User user = userDAO.getUserByUsername(username);
        if (user != null && BCrypt.checkpw(password, user.password())) { // ✅ Secure password check
            // Reset failed attempts on successful login
            failedAttempts.remove(username);
            blockedUsers.remove(username);

            return SessionManager.createSession(user);
        } else {
            // Increase failed attempts
            failedAttempts.put(username, failedAttempts.getOrDefault(username, 0) + 1);

            // Block user if limit is reached
            if (failedAttempts.get(username) >= MAX_ATTEMPTS) {
                blockedUsers.put(username, System.currentTimeMillis() + BLOCK_TIME_MS);
                System.out.println("User " + username + " is now blocked for 15 minutes.");
            }
            return null;
        }
    }

    // Helper method to check if the user is blocked
    private boolean isUserBlocked(String username) {
        if (!blockedUsers.containsKey(username)) return false;

        long blockEndTime = blockedUsers.get(username);
        if (System.currentTimeMillis() > blockEndTime) {
            blockedUsers.remove(username); // Unblock user after time limit
            return false;
        }
        return true;
    }

    public User getUserFromSession(String sessionId) {
        return SessionManager.getUser(sessionId);
    }

    public void logoutUser(String sessionId) {
        SessionManager.removeSession(sessionId);
    }
}
