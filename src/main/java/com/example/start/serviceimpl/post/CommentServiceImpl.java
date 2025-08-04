package com.example.start.serviceimpl.post;

import com.example.start.entity.post.Comment;
import com.example.start.entity.post.Post;
import com.example.start.repository.post.CommentRepository;
import com.example.start.repository.post.PostRepository;
import com.example.start.service.post.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.start.entity.post.User;


import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    @Override
    public void create(Long postId, String content, Long parentId, User loginUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setAuthor(loginUser);
        comment.setPost(post);

        //  대댓글인 경우 부모 댓글 설정
        if (parentId != null) {
            Comment parent = commentRepository.findById(parentId)
                    .orElseThrow(() -> new IllegalArgumentException("Parent comment not found"));
            comment.setParent(parent);  // 핵심 라인: 부모 댓글 연결
        }


        commentRepository.save(comment);
    }

    @Override
    public List<Comment> findByPostId(Long postId) {

        // ✅ 수정됨: 모든 댓글이 아니라 최상위 댓글만 조회하도록 변경
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        return commentRepository.findByPostAndParentIsNull(post);
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

    @Override
    public List<Comment> findAll() {
        return commentRepository.findAll();
    }

    @Override
    public long count() {
        return commentRepository.count();
    }

    @Override
    public void deleteById(Long id) {
        commentRepository.deleteById(id);
    }

}