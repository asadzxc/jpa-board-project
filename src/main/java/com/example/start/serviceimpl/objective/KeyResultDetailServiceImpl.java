package com.example.start.serviceimpl.objective;


import com.example.start.dto.objective.KeyResultWeekDetailResponse;
import com.example.start.entity.objective.DailyCheck;
import com.example.start.entity.objective.KeyResult;
import com.example.start.entity.post.User;
import com.example.start.repository.objective.DailyCheckRepository;
import com.example.start.repository.objective.KeyResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.start.service.objective.KeyResultDetailService;

import com.example.start.dto.objective.KeyResultMonthCellResponse;
import com.example.start.dto.objective.KeyResultMonthDetailResponse;

import java.time.YearMonth;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters; // ✅ 추가
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KeyResultDetailServiceImpl implements KeyResultDetailService {

    private final KeyResultRepository keyResultRepository;
    private final DailyCheckRepository dailyCheckRepository;

    @Override
    @Transactional(readOnly = true)
    public KeyResultWeekDetailResponse getWeekDetail(Long keyResultId, User loginUser, LocalDate refDate) {
        KeyResult kr = keyResultRepository.findById(keyResultId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 핵심 결과입니다."));

        // 소유자 검증
        if (kr.getObjective() != null && kr.getObjective().getUser() != null) {
            Long ownerId = kr.getObjective().getUser().getId();
            if (!ownerId.equals(loginUser.getId())) {
                throw new IllegalStateException("접근 권한이 없습니다.");
            }
        }

        // ✅ 주간 범위 계산 (월~일)
        LocalDate weekStart = refDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = refDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        // 이번 주 체크된 날짜들
        Set<LocalDate> checkedDays = dailyCheckRepository
                .findByKeyResultAndUserAndDateBetween(kr, loginUser, weekStart, weekEnd)
                .stream()
                .map(DailyCheck::getDate)
                .collect(Collectors.toCollection(java.util.LinkedHashSet::new));

        return new KeyResultWeekDetailResponse(kr, refDate, checkedDays);
    }

    @Override
    @Transactional
    public void toggleToday(Long keyResultId, User loginUser, LocalDate today) {
        KeyResult kr = keyResultRepository.findById(keyResultId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 핵심 결과입니다."));

        // 소유자 검증
        if (kr.getObjective() != null && kr.getObjective().getUser() != null) {
            Long ownerId = kr.getObjective().getUser().getId();
            if (!ownerId.equals(loginUser.getId())) {
                throw new IllegalStateException("접근 권한이 없습니다.");
            }
        }

        // 오늘 토글
        dailyCheckRepository.findByKeyResultAndUserAndDate(kr, loginUser, today)
                .ifPresentOrElse(
                        dailyCheckRepository::delete,
                        () -> {
                            DailyCheck dc = new DailyCheck();
                            dc.setKeyResult(kr);
                            dc.setUser(loginUser); // ⚠️ 반드시 세팅
                            dc.setDate(today);
                            dailyCheckRepository.save(dc);
                        }
                );
    }


    @Override
    @Transactional(readOnly = true)
    public KeyResultMonthDetailResponse getMonthDetail(Long keyResultId,
                                                       User loginUser,
                                                       Integer year,
                                                       Integer month) {

        KeyResult kr = keyResultRepository.findById(keyResultId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 핵심 결과입니다."));

        // 소유자 검증
        if (kr.getObjective() != null && kr.getObjective().getUser() != null) {
            Long ownerId = kr.getObjective().getUser().getId();
            if (!ownerId.equals(loginUser.getId())) {
                throw new IllegalStateException("접근 권한이 없습니다.");
            }
        }

        LocalDate today = LocalDate.now();

        int targetYear = (year != null) ? year : today.getYear();
        int targetMonth = (month != null) ? month : today.getMonthValue();

        YearMonth yearMonth = YearMonth.of(targetYear, targetMonth);
        LocalDate firstDayOfMonth = yearMonth.atDay(1);

        // 달력 시작일 (월요일 기준)
        LocalDate calendarStartDate =
                firstDayOfMonth.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));

        // 6주 고정 달력
        LocalDate calendarEndDate = calendarStartDate.plusDays(41);

        // 체크된 날짜 조회
        List<LocalDate> checkedDates =
                dailyCheckRepository.findCheckedDatesInRangeByKrAndUser(
                        keyResultId,
                        loginUser.getId(),
                        calendarStartDate,
                        calendarEndDate
                );

        Set<LocalDate> checkedSet = new HashSet<>(checkedDates);

        KeyResultMonthDetailResponse response = new KeyResultMonthDetailResponse();

        response.setYear(targetYear);
        response.setMonth(targetMonth);
        response.setFirstDayOfMonth(firstDayOfMonth);
        response.setCalendarStartDate(calendarStartDate);
        response.setCalendarEndDate(calendarEndDate);

        YearMonth prev = yearMonth.minusMonths(1);
        YearMonth next = yearMonth.plusMonths(1);

        response.setPrevYear(prev.getYear());
        response.setPrevMonth(prev.getMonthValue());
        response.setNextYear(next.getYear());
        response.setNextMonth(next.getMonthValue());

        LocalDate cursor = calendarStartDate;

        for (int week = 0; week < 6; week++) {

            List<KeyResultMonthCellResponse> row = new ArrayList<>();

            for (int day = 0; day < 7; day++) {

                KeyResultMonthCellResponse cell = new KeyResultMonthCellResponse();

                cell.setDate(cursor);
                cell.setDayOfMonth(cursor.getDayOfMonth());
                cell.setCurrentMonth(cursor.getMonthValue() == targetMonth);
                cell.setToday(cursor.equals(today));
                cell.setChecked(checkedSet.contains(cursor));

                row.add(cell);

                cursor = cursor.plusDays(1);
            }

            response.getWeeks().add(row);
        }

        return response;
    }
}