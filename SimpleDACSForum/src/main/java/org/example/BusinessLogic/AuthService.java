package org.example.BusinessLogic;

import org.example.DataAccessObjects.UserDAO;
import org.example.JavaModels.User;
import org.example.security.SessionManager;
import org.apache.commons.codec.digest.DigestUtils;

public class AuthService {
    private final UserDAO userDAO = new UserDAO();

    public boolean registerUser(String username, String password) {
        User existingUser = userDAO.getUserByUsername(username);
        System.out.println("Checking if user exists: " + username);
        System.out.println("UserDAO returned: " + existingUser);

        if (existingUser != null) {
            System.out.println("User already exists! Registration failed.");
            return false;
        }

        String hashedPassword = DigestUtils.sha512Hex(password);
        User newUser = new User(username, hashedPassword, 0);
        boolean success = userDAO.addUser(newUser);
        System.out.println(success ? "User registered successfully!" : "User registration failed.");
        return success;
    }


    public String loginUser(String username, String password) {
        User user = userDAO.getUserByUsername(username);
        if (user != null && user.password().equals(DigestUtils.sha512Hex(password))) {
            return SessionManager.createSession(user);
        }
        return null;
    }





    public User getUserFromSession(String sessionId) {
        return SessionManager.getUser(sessionId);
    }

    public void logoutUser(String sessionId) {
        SessionManager.removeSession(sessionId);
    }
}
