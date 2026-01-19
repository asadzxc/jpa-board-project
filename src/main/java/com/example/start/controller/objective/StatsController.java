package com.example.start.controller.objective;

import com.example.start.dto.objective.CompareStats;
import com.example.start.dto.objective.KeyResultMonthCalendarResponse; // ✅ [NEW]
import com.example.start.entity.post.User;
import com.example.start.service.objective.DailyCheckService;          // ✅ [NEW]
import com.example.start.service.objective.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.example.start.dto.objective.QuarterDdayResponse;
import jakarta.servlet.http.HttpSession;
import com.example.start.service.objective.QuarterDdayService;

import java.time.YearMonth;                                           // ✅ [NEW]

@RestController
@RequiredArgsConstructor
@RequestMapping("/okr/stats")
public class StatsController {

    private final StatsService statsService;
    private final DailyCheckService dailyCheckService; // ✅ [NEW] 달력은 DailyCheckService가 담당
    private final QuarterDdayService quarterDdayService;

    // =========================
    // KeyResult 단위 (사용자 기준)
    // =========================

    @GetMapping("/kr/{krId}/week")
    public CompareStats krWeek(@PathVariable Long krId, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        return statsService.weeklyStatsForKeyResult(krId, loginUser.getId());
    }

    @GetMapping("/kr/{krId}/month")
    public CompareStats krMonth(@PathVariable Long krId, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        return statsService.monthlyStatsForKeyResult(krId, loginUser.getId());
    }

    // ✅ ✅ ✅ [NEW] 월 달력(체크 히스토리 날짜들)
    // 호출 예: /okr/stats/kr/3/calendar?ym=2026-01
    @GetMapping("/kr/{krId}/calendar")
    public KeyResultMonthCalendarResponse krCalendar(
            @PathVariable Long krId,
            @RequestParam(required = false) String ym,
            HttpSession session
    ) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }

        YearMonth target = (ym == null || ym.isBlank())
                ? YearMonth.now()
                : YearMonth.parse(ym); // "yyyy-MM" 형식

        return dailyCheckService.getMonthCalendar(krId, loginUser, target);
    }

    // =========================
    // Objective 단위 (사용자 기준)
    // =========================

    @GetMapping("/objective/{objectiveId}/week")
    public CompareStats objectiveWeek(@PathVariable Long objectiveId, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        return statsService.weeklyStatsForObjective(objectiveId, loginUser.getId());
    }

    @GetMapping("/objective/{objectiveId}/month")
    public CompareStats objectiveMonth(@PathVariable Long objectiveId, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        return statsService.monthlyStatsForObjective(objectiveId, loginUser.getId());
    }

    @GetMapping("/kr/{krId}/quarter-dday")
    public QuarterDdayResponse quarterDday(@PathVariable Long krId, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        return quarterDdayService.getQuarterDday(krId, loginUser.getId());
    }
}