package org.example.MainApplicationControllers;

import io.javalin.http.Handler;
import org.example.BusinessLogic.AuthService;
import org.example.BusinessLogic.PostService;
import org.example.security.SessionManager;
import org.example.JavaModels.User;
import org.example.JavaModels.Post;
import java.util.UUID;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ForumController {
    private final AuthService authService = new AuthService();
    private final PostService postService = new PostService();


    public Handler getCSRFToken = ctx -> {
        String csrfToken = UUID.randomUUID().toString();
        ctx.sessionAttribute("csrfToken", csrfToken);  // Zapisz token w sesji

        ctx.json(Map.of("csrfToken", csrfToken));  // Wysyłamy token jako JSON
    };

    /**
     * Obsługuje rejestrację użytkownika.
     */
    public Handler register = ctx -> {
        String username = ctx.formParam("username");
        String password = ctx.formParam("password");

        if (username == null || password == null) {
            ctx.status(400).result("Username and password are required.");
            return;
        }

        boolean success = authService.registerUser(username, password);
        if (success) {
            ctx.result("Registration successful.");
        } else {
            ctx.status(400).result("Username already exists.");
        }
    };

    /**
     * Obsługuje logowanie użytkownika.
     */
    public Handler login = ctx -> {
        String username = ctx.formParam("username");
        String password = ctx.formParam("password");

        String sessionId = authService.loginUser(username, password);
        if (sessionId != null) {
            ctx.cookie("sessionId", sessionId);
            ctx.redirect("/posts");  // 🔄 Przekierowanie na stronę postów
        } else {
            ctx.status(401).result("Invalid username or password.");
        }
    };


    /**
     * Obsługuje wylogowanie użytkownika.
     */
    public Handler logout = ctx -> {
        String sessionId = ctx.cookie("sessionId");
        if (sessionId != null) {
            SessionManager.removeSession(sessionId);
            ctx.removeCookie("sessionId");
            ctx.result("Logout successful.");
        } else {
            ctx.status(400).result("No active session found.");
        }
    };

    /**
     * Pobiera wszystkie posty na forum.
     */
    public Handler getAllPosts = ctx -> {
        String sessionId = ctx.cookie("sessionId");
        User user = SessionManager.getUser(sessionId);

        if (user == null) {
            ctx.status(403).json(Map.of("error", "User not logged in"));
            return;
        }

        List<Post> posts = postService.getAllPosts();
        ctx.json(posts);
    };


    /**
     * Publikowanie nowego posta.
     */
    public Handler createPost = ctx -> {
        String sessionId = ctx.cookie("sessionId");
        User user = SessionManager.getUser(sessionId);

        if (user == null) {
            ctx.status(403).result("User not logged in.");
            return;
        }

        // ✅ Pobranie tokena CSRF z żądania
        String csrfTokenForm = ctx.formParam("csrfToken");
        String csrfTokenSession = ctx.sessionAttribute("csrfToken");

        // ✅ Sprawdzenie CSRF
        if (csrfTokenForm == null || csrfTokenSession == null || !csrfTokenForm.equals(csrfTokenSession)) {
            ctx.status(403).result("CSRF protection triggered. Invalid token.");
            return;
        }

        ctx.sessionAttribute("csrfToken", null); // ❌ Usunięcie tokena po użyciu (zapobiega ponownemu użyciu)

        String content = ctx.formParam("content");
        if (content == null || content.trim().isEmpty()) {
            ctx.status(400).result("Content cannot be empty.");
            return;
        }

        boolean success = postService.createPost(user.username(), content);
        if (success) {
            ctx.result("Post created successfully.");
        } else {
            ctx.status(500).result("Failed to create post.");
        }
    };


    /**
     * Usuwa post, jeśli należy do zalogowanego użytkownika.
     */
    public Handler deletePost = ctx -> {
        String sessionId = ctx.cookie("sessionId");
        User user = SessionManager.getUser(sessionId);

        if (user == null) {
            ctx.status(403).result("User not logged in.");
            return;
        }

        // ✅ Pobranie i weryfikacja tokena CSRF
        String csrfTokenForm = ctx.formParam("csrfToken");
        String csrfTokenSession = ctx.sessionAttribute("csrfToken");

        if (csrfTokenForm == null || csrfTokenSession == null || !csrfTokenForm.equals(csrfTokenSession)) {
            ctx.status(403).result("CSRF protection triggered.");
            return;
        }

        ctx.sessionAttribute("csrfToken", null); // ❌ Usunięcie tokena po użyciu

        int postId = Integer.parseInt(ctx.pathParam("id"));
        boolean success = postService.deletePost(postId, user.username());

        if (success) {
            ctx.result("Post deleted successfully.");
        } else {
            ctx.status(403).result("Unauthorized to delete this post.");
        }
    };





}
