package org.example.UI;

import io.javalin.Javalin;
import io.javalin.http.Context;
import org.example.security.SessionManager;
import org.example.JavaModels.User;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WebUI {

    public static void configure(Javalin app) {
        app.get("/register", ctx -> ctx.render("templates/register.html"));
        app.get("/", ctx -> ctx.render("templates/login.html"));
        app.get("/posts", ctx -> ctx.render("templates/posts.html")); // ✅ No need to pass username here

        // ✅ Endpoint do pobierania nazwy użytkownika
        app.get("/api/username", ctx -> {
            String sessionId = ctx.cookie("sessionId");
            User user = SessionManager.getUser(sessionId);

            if (user == null) {
                ctx.status(401).json(Map.of("error", "User not logged in"));
                return;
            }

            ctx.json(Map.of("username", user.username())); // ✅ Return JSON with username
        });

        // ✅ Endpoint do generowania tokena CSRF
        app.get("/api/csrf", ctx -> {
            String csrfToken = UUID.randomUUID().toString(); // Tworzymy unikalny token
            ctx.sessionAttribute("csrfToken", csrfToken); // Przechowujemy w sesji
            ctx.json(Map.of("csrfToken", csrfToken)); // Wysyłamy jako JSON
        });
    }

    private static void renderPostsPage(Context ctx) {
        String sessionId = ctx.cookie("sessionId");

        // Debugging: Check if sessionId is being retrieved
        System.out.println("Session ID: " + sessionId);

        User user = SessionManager.getUser(sessionId);

        // Debugging: Check if user is being retrieved
        if (user == null) {
            System.out.println("User is null. Redirecting to login.");
            ctx.redirect("/login");
            return;
        }

        System.out.println("Logged-in user: " + user.username());

        Map<String, Object> model = new HashMap<>();
        model.put("username", user.username()); // ✅ Ensure the username is added to the model

        ctx.render("templates/posts.html", model);
    }



}
