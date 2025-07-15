package com.example.start.controller;

import com.example.start.dto.CommentForm;
import com.example.start.entity.Post;
import com.example.start.service.PostService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.example.start.entity.User;
import com.example.start.dto.PostForm;
import com.example.start.service.CommentService;


@Controller
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final CommentService commentService;

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

    // 글 목록 페이지
    @GetMapping("/posts")
    public String listPosts(Model model, HttpSession session) {
        List<Post> posts = postService.findAll();  // 전체 글 목록 조회
        model.addAttribute("posts", posts);        // 모델에 담기

        // 로그인 사용자 정보를 템플릿에 전달
        User loginUser = (User) session.getAttribute("loginUser");
        model.addAttribute("loginUser", loginUser);

        return "post-list";                        // templates/post-list.html 렌더링
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
        Post post = postService.findById(id); // ID로 게시글 조회
        model.addAttribute("post", post);

        // 로그인 유저 정보를 model에 담기
        User loginUser = (User) session.getAttribute("loginUser");
        model.addAttribute("loginUser", loginUser);

        // 댓글 작성 폼 바인딩용 모델 추가
        model.addAttribute("commentForm", new CommentForm());

        // 추가: 해당 게시글의 댓글 목록 조회
        model.addAttribute("comments", commentService.findByPostId(post.getId())); //




        return "post-detail"; // templates/post-detail.html 렌더링
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
}