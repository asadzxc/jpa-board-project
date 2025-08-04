package com.example.start.repository.post;

import com.example.start.entity.post.Reaction;
import com.example.start.entity.post.Post;
import com.example.start.entity.post.User;
import com.example.start.enums.ReactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    Optional<Reaction> findByUserAndPost(User user, Post post);
    long countByPostAndType(Post post, ReactionType type);
}
