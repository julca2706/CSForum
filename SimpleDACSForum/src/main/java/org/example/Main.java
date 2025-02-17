package org.example;

import io.javalin.Javalin;
import io.javalin.rendering.JavalinRenderer;
import io.javalin.rendering.template.JavalinThymeleaf;
import org.example.UI.WebUI;
import org.example.MainApplicationControllers.ForumController;

public class Main {
    public static void main(String[] args) {
        Javalin app = Javalin.create().start(8080);

        // ✅ Rejestracja Thymeleaf
        JavalinRenderer.register(new JavalinThymeleaf(), ".html");

        // Konfiguracja WebUI
        WebUI.configure(app);

        // Konfiguracja ForumController
        ForumController forumController = new ForumController();
        app.post("/register", forumController.register);
        app.post("/login", forumController.login);
        app.post("/logout", forumController.logout);
        app.get("/posts", forumController.getAllPosts);
        app.post("/posts", forumController.createPost);

        System.out.println("Serwer uruchomiony na http://localhost:8080");
    }
}
