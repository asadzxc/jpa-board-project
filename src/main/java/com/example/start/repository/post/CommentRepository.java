package com.example.start.repository.post;


import com.example.start.entity.post.Comment;
import com.example.start.entity.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPostAndParentIsNull(Post post);
}