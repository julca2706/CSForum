package org.example.security;

import org.example.JavaModels.User;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class SessionManager {
    private static final Map<String, User> sessions = new ConcurrentHashMap<>();
    private static final Map<String, Integer> failedAttempts = new ConcurrentHashMap<>();
    private static final Map<String, Long> lockedUsers = new ConcurrentHashMap<>();

    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCK_TIME_MS = TimeUnit.MINUTES.toMillis(5);

    public static String createSession(User user) {
        String sessionId = UUID.randomUUID().toString();
        sessions.put(sessionId, user);
        return sessionId;
    }

    public static boolean isUserLocked(String username) {
        if (lockedUsers.containsKey(username)) {
            long lockTime = lockedUsers.get(username);
            if (System.currentTimeMillis() - lockTime > LOCK_TIME_MS) {
                lockedUsers.remove(username);
                failedAttempts.put(username, 0);
                return false;
            }
            return true;
        }
        return false;
    }

    public static void recordFailedLogin(String username) {
        failedAttempts.put(username, failedAttempts.getOrDefault(username, 0) + 1);
        if (failedAttempts.get(username) >= MAX_ATTEMPTS) {
            lockedUsers.put(username, System.currentTimeMillis());
        }
    }

    public static void resetFailedAttempts(String username) {
        failedAttempts.remove(username);
        lockedUsers.remove(username);
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