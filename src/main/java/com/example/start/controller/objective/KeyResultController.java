package com.example.start.controller.objective;

import com.example.start.dto.objective.KeyResultForm;
import com.example.start.dto.objective.KeyResultMonthDetailResponse;
import com.example.start.dto.objective.KeyResultResponse;
import com.example.start.dto.objective.KeyResultWeekDetailResponse;
import com.example.start.entity.objective.KeyResult;
import com.example.start.entity.post.User;
import com.example.start.repository.objective.KeyResultRepository;
import com.example.start.service.objective.DailyCheckService;
import com.example.start.service.objective.KeyResultDetailService;
import com.example.start.service.objective.KeyResultService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/okr/kr")
public class KeyResultController {

    private final DailyCheckService dailyCheckService;
    private final KeyResultService keyResultService;
    private final KeyResultRepository keyResultRepository;
    private final KeyResultDetailService keyResultDetailService;

    // KR 세부 페이지
    @GetMapping("/{keyResultId}")
    public String detail(@PathVariable Long keyResultId,
                         @RequestParam(required = false) Integer year,
                         @RequestParam(required = false) Integer month,
                         HttpSession session,
                         Model model) {

        User loginUser = (User) session.getAttribute("loginUser");

        KeyResultWeekDetailResponse detail =
                dailyCheckService.getThisWeekDetail(keyResultId, loginUser);

        KeyResultMonthDetailResponse monthDetail =
                keyResultDetailService.getMonthDetail(keyResultId, loginUser, year, month);

        Long objectiveId = detail.getObjectiveId();
        if (objectiveId != null) {
            List<KeyResultResponse> krOptions = keyResultRepository
                    .findByObjective_IdOrderByIdAsc(objectiveId)
                    .stream()
                    .map(KeyResultResponse::new)
                    .toList();

            model.addAttribute("krOptions", krOptions);

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
        model.addAttribute("monthDetail", monthDetail);
        return "okr/detail";
    }

    @GetMapping("/go")
    public String go(@RequestParam Long id, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");

        KeyResult kr = keyResultRepository.findByIdWithObjectiveAndUser(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Long ownerId = kr.getObjective().getUser().getId();
        if (!ownerId.equals(loginUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        return "redirect:/okr/kr/" + id;
    }

    // 오늘 토글
    @PostMapping("/{keyResultId}/toggle-today")
    public String toggleToday(@PathVariable Long keyResultId, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");

        dailyCheckService.toggleToday(keyResultId, loginUser);
        return "redirect:/okr/kr/" + keyResultId;
    }

    @PostMapping("/{keyResultId}/toggle-date")
    public String toggleDate(@PathVariable Long keyResultId,
                             @RequestParam String date,
                             @RequestParam(required = false) Integer year,
                             @RequestParam(required = false) Integer month,
                             HttpSession session) {

        User loginUser = (User) session.getAttribute("loginUser");

        keyResultDetailService.toggleToday(
                keyResultId,
                loginUser,
                LocalDate.parse(date)
        );

        String redirectUrl = "/okr/kr/" + keyResultId;

        if (year != null && month != null) {
            redirectUrl += "?year=" + year + "&month=" + month;
        }

        return "redirect:" + redirectUrl;
    }

    // KR 삭제
    @PostMapping("/{keyResultId}/delete")
    public String deleteKeyResult(@PathVariable Long keyResultId, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");

        dailyCheckService.deleteKeyResult(keyResultId, loginUser);
        return "redirect:/okr";
    }

    // KR 생성
    @PostMapping("/objective/{objectiveId}/create")
    public String createKr(@PathVariable Long objectiveId,
                           @ModelAttribute KeyResultForm form,
                           HttpSession session) {

        User loginUser = (User) session.getAttribute("loginUser");

        keyResultService.addKeyResult(objectiveId, form, loginUser);
        return "redirect:/okr";
    }

    // KR 수정
    @PostMapping("/{krId}/edit")
    public String editKr(@PathVariable Long krId,
                         @ModelAttribute KeyResultForm form,
                         HttpSession session) {

        User loginUser = (User) session.getAttribute("loginUser");

        keyResultService.updateKeyResult(krId, form, loginUser);
        return "redirect:/okr";
    }
}


