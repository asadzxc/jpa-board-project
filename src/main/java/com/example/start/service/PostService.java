package com.example.start.service;

import com.example.start.entity.Post;
import java.util.List;
import com.example.start.entity.User;
import com.example.start.dto.PostForm;

public interface PostService {
    Post save(Post post);
    List<Post> findAll();
    Post findById(Long id);
    void deletePostById(Long postId, User loginUser);
    List<Post> findPostsByAuthor(User user);
    void updatePost(Long id, PostForm form, User loginUser);
    List<Post> searchPosts(String title, String content, String username);


    long count();
    void deletePostByAdmin(Long postId);
}
