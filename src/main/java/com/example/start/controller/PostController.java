package com.example.start.controller;

import com.example.start.entity.Post;
import com.example.start.service.PostService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 글쓰기 폼 보여주기
    @GetMapping("/posts/new")
    public String showPostForm(HttpSession session, Model model) {
        // 로그인 상태 확인
        if (session.getAttribute("loginUser") == null) {
            return "redirect:/login";
        }

        model.addAttribute("post", new Post());
        return "post-form";  // resources/templates/post-form.html
    }

    // 글 저장 처리
    @PostMapping("/posts/new")
    public String createPost(@ModelAttribute Post post, HttpSession session) {
        if (session.getAttribute("loginUser") == null) {
            return "redirect:/login";
        }

        postService.save(post); //  저장 처리
        return "redirect:/posts"; // 글 목록 페이지로 리다이렉션
    }

    // 글 목록 페이지
    @GetMapping("/posts")
    public String listPosts(Model model) {
        List<Post> posts = postService.findAll();  // 전체 글 목록 조회
        model.addAttribute("posts", posts);        // 모델에 담기
        return "post-list";                        // templates/post-list.html 렌더링
    }
}