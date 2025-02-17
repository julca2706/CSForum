package org.example.UI;

import io.javalin.Javalin;

public class WebUI {
    public static void configure(Javalin app) {
        app.get("/register", ctx -> ctx.render("templates/register.html"));
        app.get("/", ctx -> ctx.render("templates/login.html"));
    }
}
