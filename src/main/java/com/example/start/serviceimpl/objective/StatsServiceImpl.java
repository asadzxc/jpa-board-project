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
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final DailyCheckRepository dailyCheckRepository;

    // ✅ [NEW] 모든 집계는 서울 타임존 ‘오늘’ 기준
    private static final ZoneId ZONE = ZoneId.of("Asia/Seoul");

    // =======================
    // Public APIs (userId 필수)
    // =======================

    @Override
    public CompareStats weeklyStatsForKeyResult(Long krId, Long userId) {
        // ✅ 이번 주 (월~일), 분모는 ‘오늘까지’
        LocalDate today = LocalDate.now(ZONE);
        LocalDate weekStart = today.with(DayOfWeek.MONDAY);
        LocalDate weekEnd   = today.with(DayOfWeek.SUNDAY);

        PeriodStats current  = buildKrPeriodStats(krId, userId, weekStart, min(weekEnd, today));
        // 지난 주(월~일) 전체
        PeriodStats previous = buildKrPeriodStats(krId, userId, weekStart.minusWeeks(1), weekEnd.minusWeeks(1));

        return buildCompare(current, previous);
    }

    @Override
    public CompareStats monthlyStatsForKeyResult(Long krId, Long userId) {
        // ✅ 이번 달 (1~말), 분모는 ‘오늘까지’
        LocalDate today = LocalDate.now(ZONE);
        YearMonth ym = YearMonth.from(today);
        LocalDate monthStart = ym.atDay(1);
        LocalDate monthEnd   = ym.atEndOfMonth();

        PeriodStats current  = buildKrPeriodStats(krId, userId, monthStart, min(monthEnd, today));
        // 지난 달(1~말) 전체
        YearMonth prevYm = ym.minusMonths(1);
        PeriodStats previous = buildKrPeriodStats(krId, userId, prevYm.atDay(1), prevYm.atEndOfMonth());

        return buildCompare(current, previous);
    }

    @Override
    public CompareStats weeklyStatsForObjective(Long objectiveId, Long userId) {
        LocalDate today = LocalDate.now(ZONE);
        LocalDate weekStart = today.with(DayOfWeek.MONDAY);
        LocalDate weekEnd   = today.with(DayOfWeek.SUNDAY);

        PeriodStats current  = buildObjectivePeriodStats(objectiveId, userId, weekStart, min(weekEnd, today));
        PeriodStats previous = buildObjectivePeriodStats(objectiveId, userId, weekStart.minusWeeks(1), weekEnd.minusWeeks(1));

        return buildCompare(current, previous);
    }

    @Override
    public CompareStats monthlyStatsForObjective(Long objectiveId, Long userId) {
        LocalDate today = LocalDate.now(ZONE);
        YearMonth ym = YearMonth.from(today);
        LocalDate monthStart = ym.atDay(1);
        LocalDate monthEnd   = ym.atEndOfMonth();

        PeriodStats current  = buildObjectivePeriodStats(objectiveId, userId, monthStart, min(monthEnd, today));
        YearMonth prevYm = ym.minusMonths(1);
        PeriodStats previous = buildObjectivePeriodStats(objectiveId, userId, prevYm.atDay(1), prevYm.atEndOfMonth());

        return buildCompare(current, previous);
    }

    // ✅ [NEW] streak(연속일): 오늘부터 과거로 연속 체크가 끊길 때까지 카운트
    @Override
    public int streakForKeyResult(Long krId, Long userId) {
        LocalDate cursor = LocalDate.now(ZONE);
        int streak = 0;
        while (true) {
            boolean exists = dailyCheckRepository.existsByKeyResultIdAndUserIdAndDate(krId, userId, cursor);
            if (!exists) break;
            streak++;
            cursor = cursor.minusDays(1);
        }
        return streak;
    }

    // =======================
    // Private Helpers (userId 버전만 유지)
    // =======================

    private PeriodStats buildKrPeriodStats(Long krId, Long userId, LocalDate start, LocalDate end) {
        long totalDays = daysInclusive(start, end);
        if (totalDays < 0) totalDays = 0;

        long checked = (totalDays == 0) ? 0 :
                dailyCheckRepository.countCheckedInRangeByKrAndUser(krId, userId, start, end);

        List<LocalDate> checkedDates =
                dailyCheckRepository.findCheckedDatesInRangeByKrAndUser(krId, userId, start, end);

        int rate = (totalDays == 0) ? 0 : (int) Math.round(checked * 100.0 / totalDays);

        return PeriodStats.builder()
                .start(start)
                .end(end)
                .checkedDays(checked)
                .totalDays(totalDays) // ✅ 프런트 분모(퍼센트)와 동일하게 ‘오늘까지’ 반영됨
                .rate(rate)
                .checkedDates(checkedDates)
                .build();
    }

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

    private CompareStats buildCompare(PeriodStats current, PeriodStats previous) {
        int delta = current.getRate() - previous.getRate(); // 증감(퍼센트 포인트)
        return CompareStats.builder()
                .current(current)
                .previous(previous)
                .delta(delta)
                .build();
    }

    private static LocalDate min(LocalDate a, LocalDate b) {
        return (a.isBefore(b)) ? a : b;
    }

    private static long daysInclusive(LocalDate start, LocalDate end) {
        return (start == null || end == null) ? 0 : (end.toEpochDay() - start.toEpochDay() + 1);
    }
}