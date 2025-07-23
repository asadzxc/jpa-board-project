package com.example.start.service;

import com.example.start.entity.Comment;
import com.example.start.entity.User;
import java.util.List;

public interface CommentService {
    void create(Long postId, String content, Long parentId, User loginUser);
    List<Comment> findByPostId(Long postId);
    void deleteComment(Long commentId, User loginUser);
    List<Comment> findAll();
    long count();


    void deleteById(Long id);

}
