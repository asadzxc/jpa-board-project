package com.example.start.repository.post;


import com.example.start.entity.post.Post;
import java.util.List;

public interface PostRepositoryCustom {
    List<Post> searchByConditions(String title, String content, String username);
}