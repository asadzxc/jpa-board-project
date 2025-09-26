package com.example.start.controller.objective;

import com.example.start.dto.objective.KeyResultWeekDetailResponse;
import com.example.start.entity.post.User;
import com.example.start.service.objective.DailyCheckService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/okr/kr")
public class KeyResultController {

    private final DailyCheckService dailyCheckService; // ✅ 이 서비스만 사용

    // KR 세부 페이지 (주간 7칸 + 퍼센트)
    @GetMapping("/{keyResultId}")
    public String detail(@PathVariable Long keyResultId, HttpSession session, Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        KeyResultWeekDetailResponse detail = dailyCheckService.getThisWeekDetail(keyResultId, loginUser);
        model.addAttribute("detail", detail);

        // ⬇️ 현재 프로젝트 트리상 templates/okr/detail.html 이 있으므로 여기에 맞춤
        return "okr/detail"; // ✅ detail.html 을 그대로 쓸 때
        // return "okr/kr-detail"; // ↔ 만약 파일명을 kr-detail.html 로 바꾸면 이 줄로 교체
    }

    // 오늘만 토글(체크/해제)
    @PostMapping("/{keyResultId}/toggle-today")
    public String toggleToday(@PathVariable Long keyResultId, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        dailyCheckService.toggleToday(keyResultId, loginUser);
        return "redirect:/okr/kr/" + keyResultId; // ★ 유지
    }

    // KR 삭제
    @PostMapping("/{keyResultId}/delete")
    public String deleteKeyResult(@PathVariable Long keyResultId, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        // ✅ DailyCheckService 에 붙여둔 삭제 메서드 사용
        dailyCheckService.deleteKeyResult(keyResultId, loginUser);
        return "redirect:/okr";
    }
}
