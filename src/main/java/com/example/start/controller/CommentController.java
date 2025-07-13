package com.example.start.controller;

import com.example.start.dto.CommentForm;
import com.example.start.entity.User;
import com.example.start.service.CommentService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/posts/{postId}/comments")
    public String createComment(@PathVariable Long postId,
                                @ModelAttribute CommentForm form,
                                HttpSession session) {

        System.out.println("✅ 댓글 컨트롤러 도착");
        System.out.println("내용: " + form.getContent());

        // 로그인 상태 확인
        User loginUser = (User) session.getAttribute("loginUser");

        // ✅ 핵심: 로그인 여부에 따라 작성자(author) 결정
        String author = (loginUser != null)
                ? loginUser.getUsername()
                : "익명";

        commentService.create(postId, form.getContent(), author);

        return "redirect:/posts/" + postId;
    }
}