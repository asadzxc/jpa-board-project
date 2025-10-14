package com.example.start.service.objective;

import com.example.start.dto.objective.CompareStats;

public interface StatsService {

    // ✅ KeyResult 단위 (사용자 기준)
    CompareStats weeklyStatsForKeyResult(Long keyResultId, Long userId);
    CompareStats monthlyStatsForKeyResult(Long keyResultId, Long userId);

    // ✅ Objective 단위 (사용자 기준)
    CompareStats weeklyStatsForObjective(Long objectiveId, Long userId);
    CompareStats monthlyStatsForObjective(Long objectiveId, Long userId);

    // ✅ [NEW] 연속일(오늘부터 역방향) — 리스트/세부 모두 같은 기준으로 사용
    int streakForKeyResult(Long keyResultId, Long userId);
}