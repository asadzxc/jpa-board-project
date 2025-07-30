package com.example.start.repository;

import com.example.start.entity.Reaction;
import com.example.start.entity.Post;
import com.example.start.entity.User;
import com.example.start.enums.ReactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    Optional<Reaction> findByUserAndPost(User user, Post post);
    long countByPostAndType(Post post, ReactionType type);
}
