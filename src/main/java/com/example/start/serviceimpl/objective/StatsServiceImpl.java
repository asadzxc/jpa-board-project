package com.example.start.serviceimpl.objective;

import com.example.start.dto.objective.CompareStats;
import com.example.start.dto.objective.PeriodStats;
import com.example.start.repository.objective.DailyCheckRepository;
import com.example.start.service.objective.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final DailyCheckRepository dailyCheckRepository;

    // =======================
    // Public APIs (userId 필수)
    // =======================

    @Override
    public CompareStats weeklyStatsForKeyResult(Long krId, Long userId) {
        // 이번 주 (월~일)
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(DayOfWeek.MONDAY);
        LocalDate weekEnd   = today.with(DayOfWeek.SUNDAY);

        // ♻️ 분모는 '오늘까지'로 제한 → 미래일 제외
        PeriodStats current  = buildKrPeriodStats(krId, userId, weekStart, min(weekEnd, today));
        // 전주는 전체 주간 기간 고정
        PeriodStats previous = buildKrPeriodStats(krId, userId, weekStart.minusWeeks(1), weekEnd.minusWeeks(1));

        return buildCompare(current, previous);
    }

    @Override
    public CompareStats monthlyStatsForKeyResult(Long krId, Long userId) {
        // 이번 달 (1일~말일)
        LocalDate today = LocalDate.now();
        YearMonth ym = YearMonth.from(today);
        LocalDate monthStart = ym.atDay(1);
        LocalDate monthEnd   = ym.atEndOfMonth();

        // ♻️ 분모는 '오늘까지'로 제한
        PeriodStats current  = buildKrPeriodStats(krId, userId, monthStart, min(monthEnd, today));

        // 지난달은 완전한 달(1~말일)
        YearMonth prevYm = ym.minusMonths(1);
        PeriodStats previous = buildKrPeriodStats(krId, userId, prevYm.atDay(1), prevYm.atEndOfMonth());

        return buildCompare(current, previous);
    }

    @Override
    public CompareStats weeklyStatsForObjective(Long objectiveId, Long userId) {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(DayOfWeek.MONDAY);
        LocalDate weekEnd   = today.with(DayOfWeek.SUNDAY);

        PeriodStats current  = buildObjectivePeriodStats(objectiveId, userId, weekStart, min(weekEnd, today));
        PeriodStats previous = buildObjectivePeriodStats(objectiveId, userId, weekStart.minusWeeks(1), weekEnd.minusWeeks(1));

        return buildCompare(current, previous);
    }

    @Override
    public CompareStats monthlyStatsForObjective(Long objectiveId, Long userId) {
        LocalDate today = LocalDate.now();
        YearMonth ym = YearMonth.from(today);
        LocalDate monthStart = ym.atDay(1);
        LocalDate monthEnd   = ym.atEndOfMonth();

        PeriodStats current  = buildObjectivePeriodStats(objectiveId, userId, monthStart, min(monthEnd, today));

        YearMonth prevYm = ym.minusMonths(1);
        PeriodStats previous = buildObjectivePeriodStats(objectiveId, userId, prevYm.atDay(1), prevYm.atEndOfMonth());

        return buildCompare(current, previous);
    }

    // =======================
    // Private Helpers (userId 버전만 유지)
    // =======================

    /**
     * KR 단위 기간 통계 (사용자 기준)
     */
    private PeriodStats buildKrPeriodStats(Long krId, Long userId, LocalDate start, LocalDate end) {
        long totalDays = daysInclusive(start, end);
        if (totalDays < 0) totalDays = 0; // 안전장치

        // ✅ 사용자 기준 체크 카운트
        long checked = (totalDays == 0) ? 0 :
                dailyCheckRepository.countCheckedInRangeByKrAndUser(krId, userId, start, end);

        // ✅ 스파크라인용 체크 날짜들
        List<LocalDate> checkedDates =
                dailyCheckRepository.findCheckedDatesInRangeByKrAndUser(krId, userId, start, end);

        int rate = (totalDays == 0) ? 0 : (int) Math.round(checked * 100.0 / totalDays);

        return PeriodStats.builder()
                .start(start)
                .end(end)
                .checkedDays(checked)
                .totalDays(totalDays)
                .rate(rate)
                .checkedDates(checkedDates)
                .build();
    }

    /**
     * Objective 단위 기간 통계 (사용자 기준, 여러 KR 합산)
     * - checkedDates는 필요시 null (목표 단위에선 일자별 점 필요 없으면)
     */
    private PeriodStats buildObjectivePeriodStats(Long objectiveId, Long userId, LocalDate start, LocalDate end) {
        long totalDays = daysInclusive(start, end);
        if (totalDays < 0) totalDays = 0;

        long checked = (totalDays == 0) ? 0 :
                dailyCheckRepository.countCheckedInRangeByObjectiveAndUser(objectiveId, userId, start, end);

        int rate = (totalDays == 0) ? 0 : (int) Math.round(checked * 100.0 / totalDays);

        return PeriodStats.builder()
                .start(start)
                .end(end)
                .checkedDays(checked)
                .totalDays(totalDays)
                .rate(rate)
                .checkedDates(null)
                .build();
    }

    /**
     * 전/현 기간 비교 결과 빌드
     */
    private CompareStats buildCompare(PeriodStats current, PeriodStats previous) {
        int delta = current.getRate() - previous.getRate(); // 증감(퍼센트 포인트)
        return CompareStats.builder()
                .current(current)
                .previous(previous)
                .delta(delta)
                .build();
    }

    // =======================
    // Small Utilities
    // =======================

    private static LocalDate min(LocalDate a, LocalDate b) {
        return (a.isBefore(b)) ? a : b;
    }

    private static long daysInclusive(LocalDate start, LocalDate end) {
        return (start == null || end == null) ? 0 : (end.toEpochDay() - start.toEpochDay() + 1);
    }
}
