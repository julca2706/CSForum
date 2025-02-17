package org.example.ConfigurationFiles;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseManager {

    public static Connection getConnection() {
        try (FileInputStream fis = new FileInputStream("src/main/resources/db.properties")) {
            Properties props = new Properties();
            props.load(fis);
            String url = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String password = props.getProperty("db.password");

            return DriverManager.getConnection(url, user, password); // Tworzenie nowego połączenia
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
