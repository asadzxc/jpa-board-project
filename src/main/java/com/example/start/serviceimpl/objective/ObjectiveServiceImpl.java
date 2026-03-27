package com.example.start.serviceimpl.objective;

import com.example.start.dto.objective.CompareStats;
import com.example.start.dto.objective.KeyResultResponse;
import com.example.start.dto.objective.ObjectiveForm;
import com.example.start.dto.objective.ObjectiveResponse;
import com.example.start.entity.objective.KeyResult;
import com.example.start.entity.objective.Objective;
import com.example.start.entity.post.User;
import com.example.start.enums.ObjectiveStatus;
import com.example.start.repository.objective.DailyCheckRepository;
import com.example.start.repository.objective.ObjectiveRepository;
import com.example.start.service.objective.DailyCheckService;
import com.example.start.service.objective.ObjectiveService;
import com.example.start.service.objective.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
        return objectiveRepository.findByUser_IdOrderByIdDesc(user.getId());
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
        objectiveRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteObjective(Long objectiveId, User loginUser) {
        if (loginUser == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        Objective obj = objectiveRepository.findById(objectiveId)
                .orElseThrow(() -> new IllegalArgumentException("Objective가 존재하지 않습니다."));

        Long ownerId = obj.getUser().getId();
        if (!ownerId.equals(loginUser.getId()) && !loginUser.isAdmin()) {
            throw new IllegalStateException("본인의 Objective만 삭제할 수 있습니다.");
        }

        objectiveRepository.delete(obj);
    }

    @Override
    @Transactional
    public void archiveObjective(Long objectiveId, User loginUser) {
        if (loginUser == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        Objective obj = objectiveRepository.findByIdAndUser_Id(objectiveId, loginUser.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        obj.setStatus(ObjectiveStatus.ARCHIVED);
        obj.setArchivedAt(LocalDateTime.now(ZoneId.of("Asia/Seoul")));
    }

    @Override
    @Transactional
    public void restoreObjective(Long objectiveId, User loginUser) {
        if (loginUser == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        Objective obj = objectiveRepository.findByIdAndUser_Id(objectiveId, loginUser.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        obj.setStatus(ObjectiveStatus.ACTIVE);
        obj.setArchivedAt(null);
    }

    // ------------------------------------------------------------
    // VIEW MODEL
    // ------------------------------------------------------------
    @Override
    @Transactional(readOnly = true)
    public List<ObjectiveResponse> findObjectiveResponsesByUserAndStatus(User user, ObjectiveStatus status) {

        List<Objective> objectives = (status == null)
                ? objectiveRepository.findByUser_IdOrderByIdDesc(user.getId())
                : objectiveRepository.findByUser_IdAndStatusOrderByIdDesc(user.getId(), status);

        return buildObjectiveResponses(objectives, user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ObjectiveResponse> findObjectiveResponsesByUser(User user) {
        return findObjectiveResponsesByUserAndStatus(user, null);
    }

    private List<ObjectiveResponse> buildObjectiveResponses(List<Objective> objectives, User user) {
        List<ObjectiveResponse> responses = new ArrayList<>();

        for (Objective objective : objectives) {
            List<KeyResultResponse> keyResultResponses = new ArrayList<>();

            // ===== KR 리스트 만들기 =====
            for (KeyResult kr : objective.getKeyResults()) {
                boolean checkedToday = dailyCheckService.isCheckedToday(kr.getId(), user);

                CompareStats weekStats = statsService.weeklyStatsForKeyResult(kr.getId(), user.getId());
                CompareStats monthStats = statsService.monthlyStatsForKeyResult(kr.getId(), user.getId());
                int streak = statsService.streakForKeyResult(kr.getId(), user.getId());

                KeyResultResponse dto = new KeyResultResponse(kr);
                dto.setCheckedToday(checkedToday);
                dto.setWeekCount((int) weekStats.getCurrent().getCheckedDays());
                dto.setMonthCount((int) monthStats.getCurrent().getCheckedDays());
                dto.setStreak(streak);

                keyResultResponses.add(dto);
            }

            ObjectiveResponse resp = new ObjectiveResponse(objective, keyResultResponses, 0);

            // ==================================================
            // Objective 카드 통계 채우기 (주/월/분기 + 퍼센트)
            // ==================================================
            LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

            // 이번주 / 이번달 범위
            LocalDate weekStart = today.with(java.time.DayOfWeek.MONDAY);
            LocalDate weekEnd = today.with(java.time.DayOfWeek.SUNDAY);

            LocalDate monthStart = today.withDayOfMonth(1);
            LocalDate monthEnd = today.withDayOfMonth(today.lengthOfMonth());

            // 각 Objective 자신의 시작일 / 종료일 기준
            LocalDate qStart = (objective.getStartDate() != null)
                    ? objective.getStartDate().toLocalDate()
                    : objective.getCreatedDate().toLocalDate();

            LocalDate qEnd = (objective.getEndDate() != null)
                    ? objective.getEndDate().toLocalDate()
                    : qStart.plusDays(89);

            // 체크 기록 수
            long weekChecks = dailyCheckRepository.countCheckedInRangeByObjectiveAndUser(
                    objective.getId(), user.getId(), weekStart, weekEnd);

            long monthChecks = dailyCheckRepository.countCheckedInRangeByObjectiveAndUser(
                    objective.getId(), user.getId(), monthStart, monthEnd);

            long quarterChecks = dailyCheckRepository.countCheckedInRangeByObjectiveAndUser(
                    objective.getId(), user.getId(), qStart, qEnd);

            // KR 개수 / 오늘 체크된 KR 개수
            int krCount = keyResultResponses.size();

            int todayCheckedCount = (int) keyResultResponses.stream()
                    .filter(KeyResultResponse::isCheckedToday)
                    .count();

            // 분기 진행률 (각 Objective 날짜 기준)
            int totalDays = (int) ChronoUnit.DAYS.between(qStart, qEnd) + 1;
            totalDays = Math.max(totalDays, 1);

            int elapsedDays;
            if (today.isBefore(qStart)) {
                elapsedDays = 0;
            } else if (today.isAfter(qEnd)) {
                elapsedDays = totalDays;
            } else {
                elapsedDays = (int) ChronoUnit.DAYS.between(qStart, today) + 1;
            }

            int progressPercent = (int) Math.round(elapsedDays * 100.0 / totalDays);
            progressPercent = Math.max(0, Math.min(100, progressPercent));

            // 달성률(오늘까지): 실제 체크 / (경과일수 * KR개수)
            long possible = (long) elapsedDays * krCount;
            int achievementPercent = (possible == 0)
                    ? 0
                    : (int) Math.round(quarterChecks * 100.0 / possible);
            achievementPercent = Math.max(0, Math.min(100, achievementPercent));

            // 남은 일수
            int quarterRemain = (int) ChronoUnit.DAYS.between(today, qEnd);
            quarterRemain = Math.max(0, quarterRemain);

            // resp 세팅
            resp.setKrCount(krCount);
            resp.setTodayCheckedCount(todayCheckedCount);

            resp.setWeekChecks(weekChecks);
            resp.setMonthChecks(monthChecks);
            resp.setQuarterChecks(quarterChecks);

            resp.setQuarterTotalDays(totalDays);
            resp.setQuarterElapsedDays(elapsedDays);
            resp.setQuarterProgressPercent(progressPercent);

            resp.setAchievementPercent(achievementPercent);
            resp.setQuarterRemain(quarterRemain);

            responses.add(resp);
        }

        return responses;
    }
}
