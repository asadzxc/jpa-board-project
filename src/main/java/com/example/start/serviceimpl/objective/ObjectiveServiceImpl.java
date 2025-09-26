package com.example.start.serviceimpl.objective;

import com.example.start.dto.objective.KeyResultResponse;
import com.example.start.dto.objective.ObjectiveForm;            // [NEW]
import com.example.start.dto.objective.ObjectiveResponse;
import com.example.start.entity.objective.KeyResult;
import com.example.start.entity.objective.Objective;
import com.example.start.entity.post.User;
import com.example.start.repository.objective.ObjectiveRepository;
import com.example.start.service.objective.DailyCheckService;      // [NEW]
import com.example.start.service.objective.ObjectiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ObjectiveServiceImpl implements ObjectiveService {

    private final ObjectiveRepository objectiveRepository;
    private final DailyCheckService dailyCheckService;            // [NEW] KR의 오늘 체크 여부 계산용

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
    public List<Objective> findByUser(User user) {                // [NEW]
        return objectiveRepository.findByUserId(user.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public Objective findById(Long id) {                          // [NEW]
        return objectiveRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 Objective 입니다."));
    }

    // ------------------------------------------------------------
    // UPDATE (인터페이스 시그니처와 100% 동일)
    // ------------------------------------------------------------
    @Override
    @Transactional
    public void updateObjective(Long id, ObjectiveForm form, User loginUser) { // [CHANGED] 시그니처 일치
        Objective obj = objectiveRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 Objective 입니다."));

        // (선택) 권한 체크: 소유자 또는 관리자
        if (loginUser != null) {
            Long ownerId = obj.getUser().getId();
            if (!ownerId.equals(loginUser.getId()) && !loginUser.isAdmin()) {
                throw new IllegalStateException("수정 권한이 없습니다.");
            }
        }

        // 폼의 값이 null이 아니면 반영 (프로젝트의 ObjectiveForm 필드에 맞춰 추가/수정하세요)
        if (form.getTitle() != null)        obj.setTitle(form.getTitle());
        if (form.getDescription() != null)  obj.setDescription(form.getDescription());
        // 아래는 폼에 존재한다면 사용 (없으면 삭제해도 OK)
        // if (form.getStartDate() != null)    obj.setStartDate(form.getStartDate());
        // if (form.getEndDate() != null)      obj.setEndDate(form.getEndDate());
        // if (form.getDueDate() != null)      obj.setDueDate(form.getDueDate());

        objectiveRepository.save(obj);
    }

    // ------------------------------------------------------------
    // DELETE
    // ------------------------------------------------------------
    @Override
    @Transactional
    public void deleteById(Long id) {                               // [NEW]
        // 권한 체크가 필요하면 User를 받는 다른 서비스/컨트롤러에서 검사하세요.
        objectiveRepository.deleteById(id);
    }

    // ------------------------------------------------------------
    // VIEW MODEL for UI (Objective + KeyResultResponses)
    // ------------------------------------------------------------
    @Override
    @Transactional(readOnly = true)
    public List<ObjectiveResponse> findObjectiveResponsesByUser(User user) {
        List<Objective> objectives = objectiveRepository.findByUserId(user.getId());
        List<ObjectiveResponse> responses = new ArrayList<>();

        final LocalDate today = LocalDate.now(); // 필요시 사용
        for (Objective objective : objectives) {
            List<KeyResultResponse> keyResultResponses = new ArrayList<>();

            for (KeyResult kr : objective.getKeyResults()) {
                // 오늘 체크 여부
                boolean checked = dailyCheckService.isCheckedToday(kr.getId(), user); // [NEW]

                // DTO 생성 + 값 주입 (주/월/연속일은 기본값 0, 필요 시 여기서 세팅)
                KeyResultResponse dto = new KeyResultResponse(kr);
                dto.setCheckedToday(checked);

                // (선택) 집계 값 주입하려면 아래 주석 해제하고 로직 추가
                // dto.setWeekCount(...);
                // dto.setMonthCount(...);
                // dto.setStreak(...);

                keyResultResponses.add(dto);
            }

            responses.add(new ObjectiveResponse(objective, keyResultResponses));
        }
        return responses;
    }
}