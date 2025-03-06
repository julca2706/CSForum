package org.example;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import java.nio.file.Paths;
import org.example.MainApplicationControllers.ForumController;
import org.example.UI.WebUI;

public class Main {
    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/static", Location.CLASSPATH); // ✅ Ensures CSS & JS load
            config.jetty.server(() -> {
                Server server = new Server();

                // HTTP Connector (Redirect HTTP to HTTPS)
                ServerConnector http = new ServerConnector(server);
                http.setPort(8080);

                // ✅ Fixing SSL Context
                SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();
                sslContextFactory.setKeyStorePath(Paths.get("C:/Users/antek/Downloads/pobrane/CSForum/CSForum/keystore.jks").toAbsolutePath().toString());
                sslContextFactory.setKeyStorePassword("yourpassword");  // Use actual password
                sslContextFactory.setKeyManagerPassword("yourpassword");  // Must match keystore password
                sslContextFactory.setCertAlias("selfsigned");  // Ensure alias matches keystore

                // ✅ Ensure Proper TLS and Cipher Settings
                sslContextFactory.setIncludeProtocols("TLSv1.2", "TLSv1.3"); // Allow modern TLS
                sslContextFactory.setExcludeCipherSuites("TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256"); // Exclude weak cipher
                sslContextFactory.setEndpointIdentificationAlgorithm(null); // Fix SNI validation issues

                // ✅ SecureRequestCustomizer Fix
                HttpConfiguration httpsConfig = new HttpConfiguration();
                httpsConfig.addCustomizer(new SecureRequestCustomizer());
                httpsConfig.setSecureScheme("https");
                httpsConfig.setSecurePort(8443);

                ServerConnector https = new ServerConnector(
                        server,
                        new SslConnectionFactory(sslContextFactory, "http/1.1"),
                        new HttpConnectionFactory(httpsConfig)
                );
                https.setPort(8443);

                server.setConnectors(new Connector[]{http, https});
                return server;
            });
        }).start();

        // ✅ Restore Route Registrations
        WebUI.configure(app);
        ForumController forumController = new ForumController();
        app.post("/register", forumController.register);
        app.post("/login", forumController.login);
        app.post("/logout", forumController.logout);
        app.get("/api/posts", forumController.getAllPosts);
        app.post("/api/posts", forumController.createPost);
        app.delete("/api/posts/{id}", forumController.deletePost);

        System.out.println("HTTPS Server running at https://localhost:8443");
    }
}
