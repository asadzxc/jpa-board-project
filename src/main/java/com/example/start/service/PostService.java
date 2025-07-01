package com.example.start.service;

import com.example.start.entity.Post;

import java.util.List;

public interface PostService {
    Post save(Post post);
    List<Post> findAll();
    Post findById(Long id);
}
