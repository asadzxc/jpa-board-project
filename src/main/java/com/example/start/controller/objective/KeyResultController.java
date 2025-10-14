package com.example.start.controller.objective;


import com.example.start.dto.objective.KeyResultForm;
import com.example.start.dto.objective.KeyResultWeekDetailResponse;
import com.example.start.entity.post.User;
import com.example.start.service.objective.DailyCheckService;
import com.example.start.service.objective.KeyResultService;
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
    private final KeyResultService keyResultService;

    // KR 세부 페이지 (주간 7칸 + 퍼센트)
    @GetMapping("/{keyResultId}")
    public String detail(@PathVariable Long keyResultId, HttpSession session, Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        // DailyCheckService에 getThisWeekDetail(...) 이 있다고 전제 (없으면 KeyResultDetailService로 교체)
        KeyResultWeekDetailResponse detail = dailyCheckService.getThisWeekDetail(keyResultId, loginUser);
        model.addAttribute("detail", detail);
        return "okr/detail";
    }

    // 오늘만 토글(체크/해제)
    @PostMapping("/{keyResultId}/toggle-today")
    public String toggleToday(@PathVariable Long keyResultId, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        dailyCheckService.toggleToday(keyResultId, loginUser);
        return "redirect:/okr/kr/" + keyResultId;
    }

    // KR 삭제
    @PostMapping("/{keyResultId}/delete")
    public String deleteKeyResult(@PathVariable Long keyResultId, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        dailyCheckService.deleteKeyResult(keyResultId, loginUser);
        return "redirect:/okr";
    }

    // ✅ KR 생성 (Objective 카드의 인라인 폼에서 호출)
    @PostMapping("/objective/{objectiveId}/create")
    public String createKr(@PathVariable Long objectiveId,
                           @ModelAttribute KeyResultForm form,
                           HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        keyResultService.addKeyResult(objectiveId, form, loginUser);
        return "redirect:/okr";
    }

    // ✅ KR 수정 (KR 항목 인라인 폼에서 호출)
    @PostMapping("/{krId}/edit")
    public String editKr(@PathVariable Long krId,
                         @ModelAttribute KeyResultForm form,
                         HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        keyResultService.updateKeyResult(krId, form, loginUser);
        return "redirect:/okr"; // 필요시 "redirect:/okr/kr/" + krId
    }
}

