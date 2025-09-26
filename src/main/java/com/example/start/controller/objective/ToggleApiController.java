package com.example.start.controller.objective;

import com.example.start.dto.objective.KeyResultWeekDetailResponse;
import com.example.start.dto.objective.ToggleResult;      // [NEW]
import com.example.start.entity.post.User;
import com.example.start.service.objective.DailyCheckService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/objective") // [NEW] objective prefix
public class ToggleApiController {

    private final DailyCheckService dailyCheckService;

    // [NEW] 오늘자 체크 토글 (KeyResult 기준)
    @PostMapping("/kr/{krId}/toggle")
    public ResponseEntity<ToggleResult> toggleToday(@PathVariable Long krId, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser"); // [NEW]
        KeyResultWeekDetailResponse detail = dailyCheckService.toggleToday(krId, loginUser); // 기존 시그니처 활용

        // [NEW] 서비스 리턴(KeyResultWeekDetailResponse)로부터 Ajax 응답 DTO 구성
        ToggleResult result = ToggleResult.fromWeekDetail(detail);
        return ResponseEntity.ok(result);
    }
}