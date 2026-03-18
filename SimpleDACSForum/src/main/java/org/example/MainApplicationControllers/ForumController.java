package org.example.MainApplicationControllers;

import io.javalin.http.Handler;
import org.example.BusinessLogic.AuthService;
import org.example.BusinessLogic.PostService;
import org.example.security.SessionManager;
import org.example.JavaModels.User;
import org.example.JavaModels.Post;

import java.util.List;
//TODO
public class ForumController {
    private final AuthService authService = new AuthService(); //TODO
    private final PostService postService = new PostService();

    /**
     * Obsługuje rejestrację użytkownika.
     */ //TODO
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
     */ //TODO
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
     */ //TODO
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
     * Returns all the posts on the forum.
     */ //TODO
    public Handler getAllPosts = ctx -> {
        List<Post> posts = postService.getAllPosts();
        ctx.json(posts);
    };

    /**
     * Publikowanie nowego posta.
     */ //TODO
    public Handler createPost = ctx -> {
        String sessionId = ctx.cookie("sessionId");
        User user = SessionManager.getUser(sessionId);

        if (user == null) {
            ctx.status(403).result("User not logged in.");
            return;
        }

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
     * Deletes post.
     */ //TODO
    public Handler deletePost = ctx -> {
        String sessionId = ctx.cookie("sessionId");
        User user = SessionManager.getUser(sessionId);

        if (user == null) {
            ctx.status(403).result("User not logged in.");
            return;
        }

        int postId = Integer.parseInt(ctx.pathParam("id"));
        boolean success = postService.deletePost(postId);

        if (success) {
            ctx.result("Post deleted successfully.");
        } else {
            ctx.status(403).result("Unauthorized to delete this post.");
        }
    };


}
