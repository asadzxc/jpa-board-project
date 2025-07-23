package com.example.start.controller;

import com.example.start.service.UserService;
import com.example.start.service.PostService;
import com.example.start.service.CommentService;
import com.example.start.entity.User;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final PostService postService;
    private final CommentService commentService;

    // 관리자 대시보드
    @GetMapping("/dashboard")
    public String adminDashboard(HttpSession session, Model model) {
        User loginUser = (User) session.getAttribute("loginUser");

        if (loginUser == null || !loginUser.isAdmin()) {
            return "redirect:/"; // 비관리자는 접근 불가
        }

        model.addAttribute("userCount", userService.count());
        model.addAttribute("postCount", postService.count());
        model.addAttribute("commentCount", commentService.count());

        return "admin/dashboard"; // templates/admin/dashboard.html 또는 .jsp
    }

    // 회원 목록
    @GetMapping("/users")
    public String userList(HttpSession session, Model model) {
        User loginUser = (User) session.getAttribute("loginUser");

        if (loginUser == null || !loginUser.isAdmin()) {
            return "redirect:/";
        }

        model.addAttribute("users", userService.findAll());
        return "admin/user-list";
    }

    // 게시글 목록
    @GetMapping("/posts")
    public String postList(HttpSession session, Model model) {
        User loginUser = (User) session.getAttribute("loginUser");

        if (loginUser == null || !loginUser.isAdmin()) {
            return "redirect:/";
        }

        model.addAttribute("posts", postService.findAll());
        return "admin/post-list";
    }

    // 댓글 목록
    @GetMapping("/comments")
    public String commentList(HttpSession session, Model model) {
        User loginUser = (User) session.getAttribute("loginUser");

        if (loginUser == null || !loginUser.isAdmin()) {
            return "redirect:/";
        }

        model.addAttribute("comments", commentService.findAll());
        return "admin/comment-list";
    }

    @PostMapping("/comments/delete/{id}")
    public String deleteComment(@PathVariable Long id, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");

        if (loginUser == null || !loginUser.isAdmin()) {
            return "redirect:/";
        }

        commentService.deleteById(id);
        return "redirect:/admin/comments";
    }




}
