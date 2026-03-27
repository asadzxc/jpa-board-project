package com.example.start.controller.post;

import com.example.start.entity.post.User;
import com.example.start.service.post.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    // 회원가입 폼
    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }

    // 회원가입 처리
    @PostMapping("/signup")
    public String signup(@ModelAttribute User user,
                         Model model,
                         HttpSession session) {
        try {
            User savedUser = userService.register(user);

            // 회원가입 후 자동 로그인
            session.setAttribute("loginUser", savedUser);

            return "redirect:/users/" + savedUser.getId();
        } catch (IllegalStateException | IllegalArgumentException e) {
            model.addAttribute("user", user);
            model.addAttribute("message", e.getMessage());
            return "signup";
        }
    }

    // 내 정보 페이지 (로그인 자체는 인터셉터가 확인)
    // 여기서는 본인 정보만 조회 가능하도록 추가 체크
    @GetMapping("/users/{id}")
    public String myPage(@PathVariable Long id,
                         HttpSession session,
                         Model model) {

        User loginUser = (User) session.getAttribute("loginUser");

        // 본인 정보만 접근 가능
        if (!loginUser.getId().equals(id)) {
            return "redirect:/";
        }

        User user = userService.findById(id);
        model.addAttribute("user", user);
        return "user-info";
    }

    // 메인 페이지
    @GetMapping("/")
    public String mainPage(@RequestParam(required = false) String name, Model model) {
        model.addAttribute("name", name);
        return "main";
    }

    // 로그인 폼
    @GetMapping("/login")
    public String showLoginForm(@RequestParam(value = "error", required = false) String error,
                                Model model) {
        if (error != null) {
            model.addAttribute("error", "아이디 또는 비밀번호가 잘못되었습니다.");
        }
        return "login";
    }

    // 로그인 처리
    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        RedirectAttributes ra,
                        HttpSession session) {

        User user = userService.findByUsername(username);

        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            ra.addAttribute("error", "true");
            return "redirect:/login";
        }

        session.setAttribute("loginUser", user);



        return "redirect:/";
    }

    // 로그아웃
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}