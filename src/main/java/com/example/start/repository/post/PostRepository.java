package com.example.start.repository.post;

import com.example.start.entity.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.start.entity.post.User;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {

    // 로그인한 사용자가 작성한 게시글만 조회
    List<Post> findByAuthor(User author);


}
