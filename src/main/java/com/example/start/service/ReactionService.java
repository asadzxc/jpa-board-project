package com.example.start.service;

import com.example.start.entity.User;
import com.example.start.enums.ReactionType;



public interface ReactionService {
    void toggleReaction(User user, Long postId, ReactionType type);
    long countReactions(Long postId, ReactionType type);

}
