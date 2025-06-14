package com.example.start.controller;


import com.example.start.entity.User;
import com.example.start.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // 1) 회원가입 폼 보여주기
    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute User user,
                         RedirectAttributes ra) {
        // 회원 저장 후, 저장된 User 객체를 반환받음
        User savedUser = userService.register(user);

        // 저장된 사용자의 id로 내 정보 페이지로 리다이렉트
        return "redirect:/users/" + savedUser.getId();
    }

    @GetMapping("/users/{id}")
    public String myPage(@PathVariable Long id, Model model) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        return "user-info";
    }
}