package com.example.start.service;

import com.example.start.entity.Comment;

import java.util.List;

public interface CommentService {
    void create(Long postId, String content, String author);
    List<Comment> findByPostId(Long postId);
}
