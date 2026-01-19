
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
import com.example.start.dto.objective.KeyResultMonthCalendarResponse;



import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        LocalDate weekStart = startOfWeek(today);
        LocalDate weekEnd   = endOfWeek(today);

        // ✅ 엔티티 기반 주간 조회 (기존 유지)
        List<DailyCheck> checks = dailyCheckRepository
                .findByKeyResultAndUserAndDateBetween(kr, loginUser, weekStart, weekEnd);

        Set<LocalDate> checkedDays = checks.stream()
                .map(DailyCheck::getDate)
                .collect(Collectors.toCollection(java.util.LinkedHashSet::new));

        // [NEW] 추가 계산: checkedToday / weekCount / monthCount / streak
        boolean checkedToday = dailyCheckRepository.existsByKeyResultAndUserAndDate(kr, loginUser, today);

        long weekCount = dailyCheckRepository.countCheckedInRangeByKrAndUser(
                keyResultId, loginUser.getId(), weekStart, weekEnd);

        LocalDate monthStart = today.withDayOfMonth(1);
        LocalDate monthEnd   = today.withDayOfMonth(today.lengthOfMonth());
        long monthCount = dailyCheckRepository.countCheckedInRangeByKrAndUser(
                keyResultId, loginUser.getId(), monthStart, monthEnd);

        int streak = calcStreak(keyResultId, loginUser.getId(), today);

        // [CHANGED] KeyResultWeekDetailResponse가 아래 필드를 받도록 생성자/세터 반영되어 있어야 함
        KeyResultWeekDetailResponse resp = new KeyResultWeekDetailResponse(kr, today, checkedDays);
        resp.setKeyResultId(kr.getId());       // [NEW] 필요 시 세터로 채움
        resp.setCheckedToday(checkedToday);    // [NEW]
        resp.setWeekCount((int) weekCount);    // [NEW]
        resp.setMonthCount((int) monthCount);  // [NEW]
        resp.setStreak(streak);                // [NEW]
        return resp;
    }

    @Override
    @Transactional
    public KeyResultWeekDetailResponse toggleToday(Long keyResultId, User loginUser) {
        if (loginUser == null) { // [CHANGED] 방어 로직
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        KeyResult kr = keyResultRepository.findById(keyResultId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 핵심 결과입니다."));

        LocalDate today = LocalDate.now();

        // ✅ 존재하면 해제(삭제), 없으면 체크(삽입)
        dailyCheckRepository.findByKeyResultAndUserAndDate(kr, loginUser, today)
                .ifPresentOrElse(
                        dailyCheckRepository::delete,
                        () -> {
                            DailyCheck dc = DailyCheck.builder()
                                    .keyResult(kr)
                                    .user(loginUser)        // [CHANGED] 반드시 세팅
                                    .date(today)
                                    .build();
                            dailyCheckRepository.save(dc);
                        }
                );

        // [CHANGED] 토글 후 최신 주간 상세(집계 포함) 반환
        return getThisWeekDetail(keyResultId, loginUser);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isCheckedToday(Long keyResultId, User loginUser) {
        KeyResult kr = keyResultRepository.findById(keyResultId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 핵심 결과입니다."));
        LocalDate today = LocalDate.now();
        return dailyCheckRepository.existsByKeyResultAndUserAndDate(kr, loginUser, today);
    }

    @Override
    @Transactional
    public void deleteKeyResult(Long keyResultId, User loginUser) {
        KeyResult kr = keyResultRepository.findById(keyResultId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 핵심 결과입니다."));
        Long ownerId = kr.getObjective().getUser().getId();
        if (!ownerId.equals(loginUser.getId()) && !loginUser.isAdmin()) {
            throw new IllegalStateException("삭제 권한이 없습니다.");
        }
        keyResultRepository.delete(kr);
    }

    // =====[NEW] 유틸 메서드들=====

    private LocalDate startOfWeek(LocalDate d) {
        return d.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }
    private LocalDate endOfWeek(LocalDate d) {
        return d.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
    }

    // [NEW] 오늘부터 과거로 연속 체크일 계산 (존재 여부 기반)
    private int calcStreak(Long krId, Long userId, LocalDate startDay) {
        int streak = 0;
        LocalDate cur = startDay;
        while (true) {
            boolean exists = dailyCheckRepository
                    .existsByKeyResultIdAndUserIdAndDate(krId, userId, cur); // [CHANGED]
            if (!exists) break;
            streak++;
            cur = cur.minusDays(1);
        }
        return streak;
    }

    @Override
    @Transactional(readOnly = true)
    public KeyResultMonthCalendarResponse getMonthCalendar(Long keyResultId, User loginUser, YearMonth ym) {
        if (loginUser == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        // KR 존재 검증(네 getThisWeekDetail이랑 같은 패턴)
        KeyResult kr = keyResultRepository.findById(keyResultId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 핵심 결과입니다."));

        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        // ✅ 너 Repository에 이미 있음: findCheckedDatesInRangeByKrAndUser :contentReference[oaicite:1]{index=1}
        List<String> checkedDates = dailyCheckRepository
                .findCheckedDatesInRangeByKrAndUser(keyResultId, loginUser.getId(), start, end)
                .stream()
                .map(LocalDate::toString) // "yyyy-MM-dd"
                .distinct()
                .collect(Collectors.toList());

        return new KeyResultMonthCalendarResponse(kr.getId(), ym.toString(), checkedDates);
    }
}
