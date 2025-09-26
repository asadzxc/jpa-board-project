package com.example.start.service.objective;

import com.example.start.dto.objective.CompareStats;

public interface StatsService {

    // ✅ KeyResult 단위 (사용자 기준)
    CompareStats weeklyStatsForKeyResult(Long keyResultId, Long userId);
    CompareStats monthlyStatsForKeyResult(Long keyResultId, Long userId);

    // ✅ Objective 단위 (사용자 기준)
    CompareStats weeklyStatsForObjective(Long objectiveId, Long userId);
    CompareStats monthlyStatsForObjective(Long objectiveId, Long userId);
}