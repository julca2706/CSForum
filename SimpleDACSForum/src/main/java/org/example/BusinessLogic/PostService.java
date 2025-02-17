package org.example.BusinessLogic;

import org.example.DataAccessObjects.PostDAO;
import org.example.JavaModels.Post;

import java.sql.Timestamp;
import java.util.List;

public class PostService {
    private final PostDAO postDAO = new PostDAO();

    public boolean createPost(int userId, String content) {
        if (content == null || content.trim().isEmpty()) {
            return false; // Post nie może być pusty
        }

        Post newPost = new Post(0, userId, content, new Timestamp(System.currentTimeMillis()));
        return postDAO.insertPost(newPost);
    }

    public List<Post> getAllPosts() {
        return postDAO.getAllPosts();
    }

    public List<Post> getPostsByUser(int userId) {
        return postDAO.getPostsByUser(userId);
    }

    public boolean deletePost(int postId) {
        return postDAO.deletePost(postId);
    }
}