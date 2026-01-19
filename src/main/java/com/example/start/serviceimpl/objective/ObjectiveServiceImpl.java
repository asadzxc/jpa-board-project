package com.example.start.serviceimpl.objective;

import com.example.start.dto.objective.KeyResultResponse;
import com.example.start.dto.objective.ObjectiveForm;
import com.example.start.dto.objective.ObjectiveResponse;
import com.example.start.dto.objective.CompareStats;           // ✅ [NEW] 주입 값 읽기
import com.example.start.entity.objective.KeyResult;
import com.example.start.entity.objective.Objective;
import com.example.start.entity.post.User;
import com.example.start.repository.objective.ObjectiveRepository;
import com.example.start.service.objective.DailyCheckService;
import com.example.start.service.objective.ObjectiveService;
import com.example.start.service.objective.StatsService;        // ✅ [NEW]
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.start.util.QuarterDdayCalculator;
import java.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ObjectiveServiceImpl implements ObjectiveService {

    private final ObjectiveRepository objectiveRepository;
    private final DailyCheckService dailyCheckService;
    private final StatsService statsService;                    // ✅ [NEW]

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

        if (form.getTitle() != null)       obj.setTitle(form.getTitle());
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

    // ✅ [KEEP] 권한 포함 버전 (컨트롤러에서 사용)
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


            for (KeyResult kr : objective.getKeyResults()) {




                boolean checkedToday = dailyCheckService.isCheckedToday(kr.getId(), user);

                // ✅ [NEW] 동일 집계 로직으로 리스트 값 채우기 (세부 보기와 100% 일치)
                CompareStats weekStats  = statsService.weeklyStatsForKeyResult(kr.getId(), user.getId());
                CompareStats monthStats = statsService.monthlyStatsForKeyResult(kr.getId(), user.getId());
                int streak              = statsService.streakForKeyResult(kr.getId(), user.getId());

                KeyResultResponse dto = new KeyResultResponse(kr);
                dto.setCheckedToday(checkedToday);
                dto.setWeekCount( (int) weekStats.getCurrent().getCheckedDays() );   // 이번 주 ‘오늘까지’ 카운트
                dto.setMonthCount( (int) monthStats.getCurrent().getCheckedDays() ); // 이번 달 ‘오늘까지’ 카운트
                dto.setStreak(streak);

                keyResultResponses.add(dto);
            }

            LocalDate base;
            if (objective.getStartDate() != null) {
                base = objective.getStartDate().toLocalDate();
            } else if (objective.getCreatedDate() != null) {
                base = objective.getCreatedDate().toLocalDate();
            } else {
                base = LocalDate.now();
            }

            int quarterRemain = QuarterDdayCalculator.calc(base).remain();
            responses.add(new ObjectiveResponse(objective, keyResultResponses, quarterRemain));
        }
        return responses;
    }
}