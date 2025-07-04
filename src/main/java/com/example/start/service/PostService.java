package com.example.start.service;

import com.example.start.entity.Post;
import java.util.List;
import com.example.start.entity.User;

public interface PostService {
    Post save(Post post);
    List<Post> findAll();
    Post findById(Long id);
    void deletePostById(Long postId, User loginUser);
    List<Post> findPostsByAuthor(User user);
}
