package com.example.start.controller.objective;

import com.example.start.dto.objective.KeyResultForm;
import com.example.start.dto.objective.KeyResultResponse;                 // ✅✅✅ [CHANGED] 추가
import com.example.start.dto.objective.KeyResultWeekDetailResponse;
import com.example.start.entity.post.User;
import com.example.start.repository.objective.KeyResultRepository;        // ✅✅✅ [CHANGED] 추가
import com.example.start.service.objective.DailyCheckService;
import com.example.start.service.objective.KeyResultService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.start.entity.objective.KeyResult;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;                                                   // ✅✅✅ [CHANGED] 추가

@Controller
@RequiredArgsConstructor
@RequestMapping("/okr/kr")
public class KeyResultController {

    private final DailyCheckService dailyCheckService;
    private final KeyResultService keyResultService;

    // ✅✅✅ [CHANGED] 드롭다운 옵션(같은 OKR의 다른 KR들) 조회용
    private final KeyResultRepository keyResultRepository;
    // ✅✅✅ [CHANGED] 끝

    // KR 세부 페이지 (주간 7칸 + 퍼센트)
    @GetMapping("/{keyResultId}")
    public String detail(@PathVariable Long keyResultId, HttpSession session, Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        KeyResultWeekDetailResponse detail = dailyCheckService.getThisWeekDetail(keyResultId, loginUser);

        // ✅✅✅ [CHANGED] 같은 OKR의 다른 KR 목록(드롭다운 옵션) 내려주기
        Long objectiveId = detail.getObjectiveId(); // DTO에서 반드시 세팅되어 있어야 함!
        if (objectiveId != null) {
            List<KeyResultResponse> krOptions = keyResultRepository
                    .findByObjective_IdOrderByIdAsc(objectiveId)
                    .stream()
                    .map(KeyResultResponse::new)  // id/content/progress 세팅됨
                    .toList();

            model.addAttribute("krOptions", krOptions);

            // ✅ [NEW] 이전/다음 KR id
            Long prevKrId = keyResultRepository
                    .findFirstByObjective_IdAndIdLessThanOrderByIdDesc(objectiveId, keyResultId)
                    .map(KeyResult::getId)
                    .orElse(null);

            Long nextKrId = keyResultRepository
                    .findFirstByObjective_IdAndIdGreaterThanOrderByIdAsc(objectiveId, keyResultId)
                    .map(KeyResult::getId)
                    .orElse(null);

            model.addAttribute("prevKrId", prevKrId);
            model.addAttribute("nextKrId", nextKrId);
        }




        model.addAttribute("detail", detail);
        return "okr/detail";
    }

    @GetMapping("/go")
    public String go(@RequestParam Long id, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        KeyResult kr = keyResultRepository.findByIdWithObjectiveAndUser(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Long ownerId = kr.getObjective().getUser().getId();
        if (!ownerId.equals(loginUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        return "redirect:/okr/kr/" + id;
    }

    // ✅✅✅ [CHANGED] 끝

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


