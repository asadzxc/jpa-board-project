package com.example.start.controller;

import com.example.start.entity.Post;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PostController {

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
}