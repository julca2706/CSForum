package org.example.security;

import org.example.JavaModels.User;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
    private static final Map<String, User> sessions = new ConcurrentHashMap<>();

    public static String createSession(User user) {
        String sessionId = UUID.randomUUID().toString();
        sessions.put(sessionId, user);
        return sessionId;
    }

    public static User getUser(String sessionId) {
        return sessions.get(sessionId);
    }

    public static void removeSession(String sessionId) {
        sessions.remove(sessionId);
    }

    public static boolean isSessionValid(String sessionId) {
        return sessions.containsKey(sessionId);
    }
}