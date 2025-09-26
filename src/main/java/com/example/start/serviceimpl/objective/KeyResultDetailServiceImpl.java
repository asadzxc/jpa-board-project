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
        LocalDate weekEnd   = refDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

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
}