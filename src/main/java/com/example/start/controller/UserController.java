package com.example.start.controller;


import com.example.start.entity.User;
import com.example.start.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;



    //  회원가입 폼 보여주기
    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }


    //  회원가입 처리
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

    @GetMapping("/")
    public String mainPage(@RequestParam(required = false) String name, Model model) {
        model.addAttribute("name", name);
        return "main"; // -> templates/main.html 렌더링
    }


    // 로그인
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
        if (user == null || !user.getPassword().equals(password)) {

            ra.addAttribute("error", "true");
            return "redirect:/login";
        }

        session.setAttribute("loginUser", user);



        return "redirect:/";

    }

    // 로그아웃
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // 세션 전체 무효화 (로그아웃)
        return "redirect:/"; // 메인 페이지로 이동
    }
}