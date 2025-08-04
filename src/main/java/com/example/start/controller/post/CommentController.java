package com.example.start.controller.post;

import com.example.start.dto.post.CommentForm;
import com.example.start.entity.post.User;
import com.example.start.service.post.CommentService;
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

        if (loginUser == null) {
            // 로그인 안 된 경우 익명 댓글 불허
            return "redirect:/login";
        }

        commentService.create(postId, form.getContent(), form.getParentId(), loginUser);

        return "redirect:/posts/" + postId;
    }

    @PostMapping("/comments/{id}/delete")
    public String deleteComment(@PathVariable("id") Long commentId,
                                @RequestParam("postId") Long postId,
                                HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");

        if (loginUser == null) {
            return "redirect:/login";
        }

        try {
            commentService.deleteComment(commentId, loginUser);
        } catch (IllegalStateException e) {
            // 작성자가 아닌 경우 → 다시 게시글 페이지로
            return "redirect:/posts/" + postId + "?error=unauthorized";
        }

        return "redirect:/posts/" + postId;
    }
}