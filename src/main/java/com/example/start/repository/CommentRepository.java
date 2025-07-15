package com.example.start.repository;


import com.example.start.entity.Comment;
import com.example.start.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPostAndParentIsNull(Post post);
}