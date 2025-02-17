package org.example.BusinessLogic;

import org.example.DataAccessObjects.UserDAO;
import org.example.JavaModels.User;
import org.example.security.SessionManager;

public class AuthService {
    private final UserDAO userDAO = new UserDAO();

    public boolean registerUser(String username, String password) {
        if (userDAO.getUserByUsername(username) != null) {
            return false;
        }

        User newUser = new User(username, password, 0);
        return userDAO.addUser(newUser);
    }

    public String loginUser(String username, String password) {
        if (userDAO.validateLogin(username, password)) {
            User user = userDAO.getUserByUsername(username);
            if (user != null) {
                return SessionManager.createSession(user);
            }
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
