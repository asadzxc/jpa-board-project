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

    private final DailyCheckService dailyCheckService;

    // KR 세부 페이지 (주간 7칸 + 퍼센트)
    @GetMapping("/{keyResultId}")
    public String detail(@PathVariable Long keyResultId, HttpSession session, Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        KeyResultWeekDetailResponse detail = dailyCheckService.getThisWeekDetail(keyResultId, loginUser);
        model.addAttribute("detail", detail);
        return "okr/detail"; // templates/okr/kr-detail.html
    }

    // 오늘만 토글(체크/해제)
    @PostMapping("/{keyResultId}/toggle-today")
    public String toggleToday(@PathVariable Long keyResultId, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        dailyCheckService.toggleToday(keyResultId, loginUser);
        return "redirect:/okr/kr/" + keyResultId;
    }
}
