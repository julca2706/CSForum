package org.example.JavaModels;

import java.sql.Timestamp;

public record Post(int id, int userId, String content, Timestamp createdAt) {
}
