package com.example.start.service.post;

import com.example.start.entity.post.Comment;
import com.example.start.entity.post.User;
import java.util.List;

public interface CommentService {
    void create(Long postId, String content, Long parentId, User loginUser);
    List<Comment> findByPostId(Long postId);
    void deleteComment(Long commentId, User loginUser);
    List<Comment> findAll();
    long count();


    void deleteById(Long id);

}
