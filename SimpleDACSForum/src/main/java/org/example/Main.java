package org.example;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.json.JsonMapper;
import io.javalin.json.JavalinJackson;
import io.javalin.rendering.JavalinRenderer;
import io.javalin.rendering.template.JavalinThymeleaf;
import org.example.UI.WebUI;
import org.example.MainApplicationControllers.ForumController;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Main {
    public static void main(String[] args) {
        // 🔄 Konfiguracja Jacksona jako domyślny JSON Mapper
        JsonMapper jsonMapper = new JavalinJackson(new ObjectMapper());

        // ✅ Utworzenie aplikacji Javalin
        Javalin app = Javalin.create(config -> {
            config.jsonMapper(jsonMapper); // ✅ Rejestracja Jacksona
            config.staticFiles.add("/static", Location.CLASSPATH); // ✅ Dodano obsługę plików statycznych (CSS, JS)
        }).start(8080);

        // ✅ Rejestracja Thymeleaf
        JavalinRenderer.register(new JavalinThymeleaf(), ".html");

        // ✅ Konfiguracja WebUI
        WebUI.configure(app);

        // ✅ Konfiguracja ForumController
        ForumController forumController = new ForumController();
        app.post("/register", forumController.register);
        app.post("/login", forumController.login);
        app.post("/logout", forumController.logout);
        app.get("/api/posts", forumController.getAllPosts);
        app.post("/api/posts", forumController.createPost);
        app.delete("/api/posts/{id}", forumController.deletePost);

        System.out.println("Serwer uruchomiony na http://localhost:8080");
    }
}
