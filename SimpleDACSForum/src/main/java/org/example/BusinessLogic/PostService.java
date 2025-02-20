package org.example.BusinessLogic;

import org.example.DataAccessObjects.PostDAO;
import org.example.JavaModels.Post;

import java.sql.Timestamp;
import java.util.List;

public class PostService {
    private final PostDAO postDAO = new PostDAO();

    public boolean createPost(String username, String content) {
        if (content == null || content.trim().isEmpty()) {
            return false;
        }

        Post newPost = new Post(0, username, content, new Timestamp(System.currentTimeMillis()));
        return postDAO.insertPost(newPost);
    }

    public List<Post> getAllPosts() {
        return postDAO.getAllPosts();
    }

    public List<Post> getPostsByUser(String username) {
        return postDAO.getPostsByUser(username);
    }

    public boolean deletePost(int postId) {
        return postDAO.deletePost(postId);
    }
}