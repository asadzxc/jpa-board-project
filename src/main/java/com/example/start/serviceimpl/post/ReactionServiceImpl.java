package com.example.start.serviceimpl.post;

import com.example.start.entity.post.Post;
import com.example.start.entity.post.Reaction;
import com.example.start.entity.post.User;
import com.example.start.enums.ReactionType;
import com.example.start.repository.post.PostRepository;
import com.example.start.repository.post.ReactionRepository;
import com.example.start.service.post.ReactionService;


import java.util.Optional;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReactionServiceImpl implements ReactionService {

    private final ReactionRepository reactionRepository;
    private final PostRepository postRepository;

    @Override
    public void toggleReaction(User user, Long postId, ReactionType type) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다."));

        // 자기 자신의 글엔 반응 불가
        if (post.getAuthor().getId().equals(user.getId())) {
            throw new RuntimeException("자신의 게시글에는 반응할 수 없습니다.");
        }

        Optional<Reaction> existing = reactionRepository.findByUserAndPost(user, post);

        if (existing.isPresent()) {
            Reaction reaction = existing.get();
            if (reaction.getType() == type) {
                // 이미 같은 반응 -> 취소
                reactionRepository.delete(reaction);
            } else {
                // 반응 변경
                reaction.setType(type);
                reactionRepository.save(reaction);
            }
        } else {
            // 새로 반응 추가
            Reaction reaction = new Reaction();
            reaction.setUser(user);
            reaction.setPost(post);
            reaction.setType(type);
            reactionRepository.save(reaction);
        }
    }

    @Override
    public long countReactions(Long postId, ReactionType type) {
        Post post = postRepository.findById(postId).orElseThrow();
        return reactionRepository.countByPostAndType(post, type);
    }
}