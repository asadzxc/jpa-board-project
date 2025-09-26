package com.example.start.controller.objective;

import com.example.start.dto.objective.CompareStats;
import com.example.start.entity.post.User;                 // 👈 세션에서 꺼낼 User
import com.example.start.service.objective.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;                 // 👈 401 처리용
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpSession;                   // 👈 세션 주입

@RestController
@RequiredArgsConstructor
@RequestMapping("/okr/stats")
public class StatsController {

    private final StatsService statsService;

    // =========================
    // KeyResult 단위 (사용자 기준)
    // =========================

    @GetMapping("/kr/{krId}/week")
    public CompareStats krWeek(@PathVariable Long krId, HttpSession session) {
        // ♻️ 변경: 세션에서 로그인 사용자 확인 후 userId 전달
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            // REST API이므로 401로 응답 (템플릿 리다이렉트가 아님)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        return statsService.weeklyStatsForKeyResult(krId, loginUser.getId());
    }

    @GetMapping("/kr/{krId}/month")
    public CompareStats krMonth(@PathVariable Long krId, HttpSession session) {
        // ♻️ 변경: userId 전달
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        return statsService.monthlyStatsForKeyResult(krId, loginUser.getId());
    }

    // =========================
    // Objective 단위 (사용자 기준)
    // =========================

    @GetMapping("/objective/{objectiveId}/week")
    public CompareStats objectiveWeek(@PathVariable Long objectiveId, HttpSession session) {
        // ♻️ 변경: userId 전달
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        return statsService.weeklyStatsForObjective(objectiveId, loginUser.getId());
    }

    @GetMapping("/objective/{objectiveId}/month")
    public CompareStats objectiveMonth(@PathVariable Long objectiveId, HttpSession session) {
        // ♻️ 변경: userId 전달
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        return statsService.monthlyStatsForObjective(objectiveId, loginUser.getId());
    }
}