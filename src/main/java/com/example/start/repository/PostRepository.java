package com.example.start.repository;

import com.example.start.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.start.entity.User;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 로그인한 사용자가 작성한 게시글만 조회
    List<Post> findByAuthor(User author);
}
