package org.example.ConfigurationFiles;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseManager {

    public static Connection getConnection() {
        try {
            String url = "jdbc:mysql://localhost:3306/DACSForum";
            String user = "root";
            String password = "julia123";

            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
