package com.example.start.repository;


import com.example.start.entity.Post;
import java.util.List;

public interface PostRepositoryCustom {
    List<Post> searchByConditions(String title, String content, String username);
}