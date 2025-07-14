package com.example.start.serviceimpl;

import com.example.start.entity.Comment;
import com.example.start.entity.Post;
import com.example.start.repository.CommentRepository;
import com.example.start.repository.PostRepository;
import com.example.start.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.start.entity.User;


import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    @Override
    public void create(Long postId, String content, User loginUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setAuthor(loginUser);
        comment.setPost(post);

        commentRepository.save(comment);
    }

    @Override
    public List<Comment> findByPostId(Long postId) {
        return commentRepository.findByPostId(postId);
    }

    @Override
    public void deleteComment(Long commentId, User loginUser) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));

        // 작성자 본인인지 확인
        if (!comment.getAuthor().getId().equals(loginUser.getId())) {
            throw new IllegalStateException("댓글 작성자만 삭제할 수 있습니다.");
        }

        commentRepository.delete(comment);
    }
}