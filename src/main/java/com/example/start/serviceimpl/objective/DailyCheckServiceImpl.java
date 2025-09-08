
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
import com.example.start.service.objective.DailyCheckService;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DailyCheckServiceImpl implements DailyCheckService {

    private final DailyCheckRepository dailyCheckRepository;
    private final KeyResultRepository keyResultRepository;

    @Override
    @Transactional(readOnly = true)
    public KeyResultWeekDetailResponse getThisWeekDetail(Long keyResultId, User loginUser) {
        KeyResult kr = keyResultRepository.findById(keyResultId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 핵심 결과입니다."));

        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd   = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        List<DailyCheck> checks = dailyCheckRepository
                .findByKeyResultIdAndUserIdAndDateBetween(kr.getId(), loginUser.getId(), weekStart, weekEnd);

        return new KeyResultWeekDetailResponse(
                kr, checks.stream().map(DailyCheck::getDate).toList()
        );
    }

    @Override
    @Transactional
    public KeyResultWeekDetailResponse toggleToday(Long keyResultId, User loginUser) {
        KeyResult kr = keyResultRepository.findById(keyResultId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 핵심 결과입니다."));

        LocalDate today = LocalDate.now();

        // 오늘만 체크 가능
        dailyCheckRepository.findByKeyResultIdAndUserIdAndDate(kr.getId(), loginUser.getId(), today)
                .ifPresentOrElse(
                        // 이미 있으면 언체크
                        dailyCheckRepository::delete,
                        // 없으면 체크 생성
                        () -> dailyCheckRepository.save(
                                DailyCheck.builder()
                                        .keyResult(kr)
                                        .user(loginUser)
                                        .date(today)
                                        .build()
                        )
                );

        // 갱신된 당주 데이터 반환
        return getThisWeekDetail(keyResultId, loginUser);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isCheckedToday(Long keyResultId, User loginUser) {
        LocalDate today = LocalDate.now();
        return dailyCheckRepository
                .existsByKeyResultIdAndUserIdAndDate(keyResultId, loginUser.getId(), today);
    }
}
