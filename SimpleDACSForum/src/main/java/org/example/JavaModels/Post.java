package org.example.JavaModels;

import java.sql.Timestamp;

public record Post(int id, String username, String content, Timestamp createdAt) {
}

