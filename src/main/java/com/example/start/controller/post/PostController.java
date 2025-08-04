package com.example.start.controller.post;

import com.example.start.dto.post.CommentForm;
import com.example.start.entity.post.Post;
import com.example.start.service.post.PostService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.example.start.entity.post.User;
import com.example.start.dto.post.PostForm;
import com.example.start.service.post.CommentService;
import com.example.start.service.post.ReactionService;
import com.example.start.enums.ReactionType;


@Controller
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final CommentService commentService;
    private final ReactionService reactionService;

    // 글쓰기 폼 보여주기
    @GetMapping("/posts/new")
    public String showPostForm(HttpSession session, Model model) {
        // 로그인 상태 확인
        if (session.getAttribute("loginUser") == null) {
            return "redirect:/login";
        }

        model.addAttribute("post", new PostForm());
        return "post-form";  // resources/templates/post-form.html
    }

    // 글 저장 처리
    @PostMapping("/posts/new")
    public String createPost(@ModelAttribute PostForm form, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");

        if (loginUser == null) {
            return "redirect:/login";
        }

        // 엔티티 수동 생성
        Post post = new Post();
        post.setTitle(form.getTitle());
        post.setContent(form.getContent());
        post.setAuthor(loginUser);  // 작성자 설정 (중요)

        postService.save(post);
        return "redirect:/posts";
    }

    @GetMapping("/posts")
    public String listPosts(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) String username,
            Model model,
            HttpSession session
    ) {
        // 조건에 따라 검색된 게시글 리스트 조회
        List<Post> posts = postService.searchPosts(title, content, username);
        model.addAttribute("posts", posts);

        // 검색어 입력값 유지용
        model.addAttribute("title", title);
        model.addAttribute("content", content);
        model.addAttribute("username", username);

        // 로그인 사용자 정보
        User loginUser = (User) session.getAttribute("loginUser");
        model.addAttribute("loginUser", loginUser);

        return "post-list";
    }

    // 삭제 기능
    @PostMapping("/posts/{id}/delete")
    public String deletePost(@PathVariable Long id, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login";
        }

        postService.deletePostById(id, loginUser); // 작성자 검증 포함
        return "redirect:/posts";
    }

    // 내 글만 보여주기
    @GetMapping("/posts/my")
    public String myPosts(Model model, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login";
        }

        List<Post> myPosts = postService.findPostsByAuthor(loginUser);
        model.addAttribute("posts", myPosts);
        model.addAttribute("loginUser", loginUser);
        return "post-list"; // 재사용: 목록 페이지
    }

    // 게시글 상세 페이지
    @GetMapping("/posts/{id}")
    public String showPostDetail(@PathVariable Long id, Model model, HttpSession session) {
        Post post = postService.findById(id); // 게시글 조회
        model.addAttribute("post", post);

        // 로그인 유저 정보
        User loginUser = (User) session.getAttribute("loginUser");
        model.addAttribute("loginUser", loginUser);

        // 댓글 폼 + 댓글 목록
        model.addAttribute("commentForm", new CommentForm());
        model.addAttribute("comments", commentService.findByPostId(post.getId()));

        // Reaction 정보 추가
        long likeCount = reactionService.countReactions(id, ReactionType.LIKE);
        long dislikeCount = reactionService.countReactions(id, ReactionType.DISLIKE);
        model.addAttribute("likeCount", likeCount);
        model.addAttribute("dislikeCount", dislikeCount);

        return "post-detail";
    }

    // 글 수정 폼 보여주기
    @GetMapping("/posts/edit/{id}")
    public String editPostForm(@PathVariable Long id, HttpSession session, Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login";
        }

        Post post = postService.findById(id);
        if (!post.getAuthor().getId().equals(loginUser.getId())) {
            return "redirect:/posts";
        }

        PostForm form = new PostForm();
        form.setTitle(post.getTitle());
        form.setContent(post.getContent());

        model.addAttribute("postId", id);
        model.addAttribute("postForm", form); // 수정 폼에 전달할 DTO
        return "post-edit"; // templates/post-edit.html
    }

    // 글 수정 처리
    @PostMapping("/posts/edit/{id}")
    public String updatePost(@PathVariable Long id,
                             @ModelAttribute PostForm form,
                             HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login";
        }

        postService.updatePost(id, form, loginUser); // 서비스에서 권한 검증 포함
        return "redirect:/posts/" + id;
    }

    // 좋아요, 싫어요
    @PostMapping("/posts/{postId}/react")
    public String reactToPost(@PathVariable Long postId,
                              @RequestParam ReactionType type,
                              HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");

        if (loginUser == null) {
            return "redirect:/login";
        }

        reactionService.toggleReaction(loginUser, postId, type);
        return "redirect:/posts/" + postId;
    }


}