package com.example.start.serviceimpl.objective;

import com.example.start.dto.objective.CompareStats;
import com.example.start.dto.objective.KeyResultResponse;
import com.example.start.dto.objective.ObjectiveForm;
import com.example.start.dto.objective.ObjectiveResponse;
import com.example.start.entity.objective.KeyResult;
import com.example.start.entity.objective.Objective;
import com.example.start.entity.post.User;
import com.example.start.repository.objective.DailyCheckRepository;
import com.example.start.repository.objective.ObjectiveRepository;
import com.example.start.service.objective.DailyCheckService;
import com.example.start.service.objective.ObjectiveService;
import com.example.start.service.objective.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ObjectiveServiceImpl implements ObjectiveService {

    private final ObjectiveRepository objectiveRepository;
    private final DailyCheckService dailyCheckService;
    private final StatsService statsService;
    private final DailyCheckRepository dailyCheckRepository;

    // ------------------------------------------------------------
    // CREATE
    // ------------------------------------------------------------
    @Override
    @Transactional
    public void saveObjective(Objective objective, User user) {
        objective.setUser(user);
        objectiveRepository.save(objective);
    }

    // ------------------------------------------------------------
    // READ (목록/단건)
    // ------------------------------------------------------------
    @Override
    @Transactional(readOnly = true)
    public List<Objective> findByUser(User user) {
        return objectiveRepository.findByUserId(user.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public Objective findById(Long id) {
        return objectiveRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 Objective 입니다."));
    }

    // ------------------------------------------------------------
    // UPDATE
    // ------------------------------------------------------------
    @Override
    @Transactional
    public void updateObjective(Long id, ObjectiveForm form, User loginUser) {
        Objective obj = objectiveRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 Objective 입니다."));

        // 권한 체크(소유자 또는 관리자)
        if (loginUser != null) {
            Long ownerId = obj.getUser().getId();
            if (!ownerId.equals(loginUser.getId()) && !loginUser.isAdmin()) {
                throw new IllegalStateException("수정 권한이 없습니다.");
            }
        }

        if (form.getTitle() != null) obj.setTitle(form.getTitle());
        if (form.getDescription() != null) obj.setDescription(form.getDescription());

        objectiveRepository.save(obj);
    }

    // ------------------------------------------------------------
    // DELETE
    // ------------------------------------------------------------
    @Override
    @Transactional
    public void deleteById(Long id) {
        // (컨트롤러/다른 메서드에서 권한 체크 후 호출한다고 가정)
        objectiveRepository.deleteById(id);
    }

    // ✅ 권한 포함 버전 (컨트롤러에서 사용)
    @Override
    @Transactional
    public void deleteObjective(Long objectiveId, User loginUser) {
        if (loginUser == null) throw new IllegalStateException("로그인이 필요합니다.");

        Objective obj = objectiveRepository.findById(objectiveId)
                .orElseThrow(() -> new IllegalArgumentException("Objective가 존재하지 않습니다."));

        Long ownerId = obj.getUser().getId();
        if (!ownerId.equals(loginUser.getId()) && !loginUser.isAdmin()) {
            throw new IllegalStateException("본인의 Objective만 삭제할 수 있습니다.");
        }
        objectiveRepository.delete(obj); // cascade로 KR/DailyCheck까지 삭제
    }

    // ------------------------------------------------------------
    // VIEW MODEL for UI (Objective + KeyResultResponses)
    // ------------------------------------------------------------
    @Override
    @Transactional(readOnly = true)
    public List<ObjectiveResponse> findObjectiveResponsesByUser(User user) {
        List<Objective> objectives = objectiveRepository.findByUserId(user.getId());
        List<ObjectiveResponse> responses = new ArrayList<>();

        for (Objective objective : objectives) {
            List<KeyResultResponse> keyResultResponses = new ArrayList<>();

            // ===== KR 리스트 만들기 (기존 로직 유지) =====
            for (KeyResult kr : objective.getKeyResults()) {
                boolean checkedToday = dailyCheckService.isCheckedToday(kr.getId(), user);

                CompareStats weekStats = statsService.weeklyStatsForKeyResult(kr.getId(), user.getId());
                CompareStats monthStats = statsService.monthlyStatsForKeyResult(kr.getId(), user.getId());
                int streak = statsService.streakForKeyResult(kr.getId(), user.getId());

                KeyResultResponse dto = new KeyResultResponse(kr);
                dto.setCheckedToday(checkedToday);
                dto.setWeekCount((int) weekStats.getCurrent().getCheckedDays());     // 이번 주 ‘오늘까지’
                dto.setMonthCount((int) monthStats.getCurrent().getCheckedDays());  // 이번 달 ‘오늘까지’
                dto.setStreak(streak);

                keyResultResponses.add(dto);
            }

            // ===== base 계산 (기존 유지) =====
            LocalDate base;
            if (objective.getStartDate() != null) {
                base = objective.getStartDate().toLocalDate();
            } else if (objective.getCreatedDate() != null) {
                base = objective.getCreatedDate().toLocalDate();
            } else {
                base = LocalDate.now();
            }

            // ===== 기존 quarterRemain (base 기준 분기) =====
            ObjectiveResponse resp = new ObjectiveResponse(objective, keyResultResponses, 0);

            // ==================================================
            // ✅ [NEW] Objective 카드 통계 채우기 (주/월/분기 + 퍼센트)
            // ==================================================
            LocalDate today = LocalDate.now();

            // 이번주/이번달 범위
            LocalDate weekStart = today.with(java.time.DayOfWeek.MONDAY);
            LocalDate weekEnd = today.with(java.time.DayOfWeek.SUNDAY);

            LocalDate monthStart = today.withDayOfMonth(1);
            LocalDate monthEnd = today.withDayOfMonth(today.lengthOfMonth());

            // ✅ 분기 범위: "오늘이 속한 분기" 기준 + 90일 고정
            LocalDate qStart = quarterStartByBase(today);   // 🔥 base -> today 로 변경
            LocalDate qEnd   = qStart.plusDays(89);         // 🔥 90일 고정(시작일 포함)

// Objective 단위 체크 기록 수 (DailyCheck 테이블에서 count)
            long weekChecks = dailyCheckRepository.countCheckedInRangeByObjectiveAndUser(
                    objective.getId(), user.getId(), weekStart, weekEnd);

            long monthChecks = dailyCheckRepository.countCheckedInRangeByObjectiveAndUser(
                    objective.getId(), user.getId(), monthStart, monthEnd);

            long quarterChecks = dailyCheckRepository.countCheckedInRangeByObjectiveAndUser(
                    objective.getId(), user.getId(), qStart, qEnd);

// KR 개수 / 오늘 체크된 KR 개수
            int krCount = keyResultResponses.size();

            int todayCheckedCount = (int) keyResultResponses.stream()
                    .filter(KeyResultResponse::isCheckedToday)   // 컴파일 에러면 getCheckedToday()
                    .count();

// ✅ 분기 진행률(90일 고정)
            int totalDays = 90;

            int elapsedDays = (int) ChronoUnit.DAYS.between(qStart, today) + 1;
            elapsedDays = Math.max(0, Math.min(totalDays, elapsedDays)); // 0~90으로 clamp

            int progressPercent = (int) Math.round(elapsedDays * 100.0 / totalDays);
            progressPercent = Math.max(0, Math.min(100, progressPercent)); // 0~100 clamp








            // 달성률(오늘까지): 실제 체크 / (경과일수 * KR개수)
            long possible = (long) elapsedDays * krCount;
            int achievementPercent = (possible == 0) ? 0 : (int) Math.round(quarterChecks * 100.0 / possible);
            achievementPercent = Math.max(0, Math.min(100, achievementPercent));

            // resp에 세팅 (ObjectiveResponse에 @Setter + 필드 추가 필요)
            resp.setKrCount(krCount);
            resp.setTodayCheckedCount(todayCheckedCount);

            resp.setWeekChecks(weekChecks);
            resp.setMonthChecks(monthChecks);
            resp.setQuarterChecks(quarterChecks);

            resp.setQuarterTotalDays(totalDays);
            resp.setQuarterElapsedDays(elapsedDays);
            resp.setQuarterProgressPercent(progressPercent);

            resp.setAchievementPercent(achievementPercent);

            // (선택) quarterRemain을 "분기 종료일 기준 D-day"로 통일하고 싶으면 덮어쓰기
            resp.setQuarterRemain((int) ChronoUnit.DAYS.between(today, qEnd));

            responses.add(resp);
        }

        return responses;
    }

    // ✅ base가 속한 분기의 시작일(1/4/7/10월 1일)
    private LocalDate quarterStartByBase(LocalDate base) {
        int q = (base.getMonthValue() - 1) / 3; // 0~3
        int startMonth = q * 3 + 1;            // 1,4,7,10
        return LocalDate.of(base.getYear(), startMonth, 1);
    }
}
