package com.example.start.serviceimpl;

import com.example.start.entity.Post;
import com.example.start.repository.PostRepository;
import com.example.start.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.start.entity.User;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    @Override
    public Post save(Post post) {
        return postRepository.save(post);
    }

    @Override
    public List<Post> findAll() {
        return postRepository.findAll();
    }

    @Override
    public Post findById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
    }

    // 본인이 작성한 글만 삭제할 수 있도록 검증
    @Override
    public void deletePostById(Long postId, User loginUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        if (!post.getAuthor().getId().equals(loginUser.getId())) {
            throw new SecurityException("본인이 작성한 글만 삭제할 수 있습니다.");
        }

        postRepository.delete(post);
    }


    // 내 글만 모아보기
    @Override
    public List<Post> findPostsByAuthor(User user) {
        return postRepository.findByAuthor(user);
    }
}