package com.example.start.serviceimpl.objective;

import com.example.start.dto.objective.KeyResultResponse;
import com.example.start.dto.objective.ObjectiveForm;
import com.example.start.dto.objective.ObjectiveResponse;
import com.example.start.entity.objective.KeyResult;
import com.example.start.entity.objective.Objective;
import com.example.start.entity.post.User;
import com.example.start.repository.objective.ObjectiveRepository;
import com.example.start.service.objective.DailyCheckService;
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
    private final DailyCheckService dailyCheckService;

    @Override
    @Transactional
    public void saveObjective(Objective objective, User user) {
        objective.setUser(user);
        objectiveRepository.save(objective);  // KR은 cascade로 저장
    }

    @Override
    @Transactional(readOnly = true)
    public List<Objective> findByUser(User user) {
        return objectiveRepository.findByUserId(user.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public Objective findById(Long id) {
        return objectiveRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 목표를 찾을 수 없습니다."));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        objectiveRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void updateObjective(Long id, ObjectiveForm form, User loginUser) {
        Objective objective = objectiveRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 목표가 없습니다."));

        if (!objective.getUser().getId().equals(loginUser.getId())) {
            throw new IllegalStateException("본인의 OKR만 수정할 수 있습니다.");
        }

        // 제목/설명 갱신
        objective.setTitle(form.getTitle());
        objective.setDescription(form.getDescription());

        // 기존 KR 정리(연관관계 설정에 의해 삭제)
        if (objective.getKeyResults() == null) {
            objective.setKeyResults(new ArrayList<>());
        } else {
            objective.getKeyResults().clear();
        }

        // 신규 KR 추가
        if (form.getKeyResults() != null) {
            form.getKeyResults().forEach(krForm -> {
                if (krForm.getContent() != null && !krForm.getContent().trim().isEmpty()) {
                    KeyResult newKR = new KeyResult();
                    newKR.setContent(krForm.getContent());
                    newKR.setProgress(krForm.getProgress() == null ? 0 : krForm.getProgress());
                    newKR.setWeight(1);           // ✅ weight 명시 세팅
                    newKR.setObjective(objective);
                    objective.getKeyResults().add(newKR);
                }
            });
        }

        objectiveRepository.save(objective);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ObjectiveResponse> findObjectiveResponsesByUser(User user) {
        List<Objective> objectives = objectiveRepository.findByUserId(user.getId());
        List<ObjectiveResponse> responses = new ArrayList<>();

        final LocalDate today = LocalDate.now();
        for (Objective objective : objectives) {
            List<KeyResultResponse> keyResultResponses = new ArrayList<>();
            for (KeyResult kr : objective.getKeyResults()) {
                boolean checked = dailyCheckService.isCheckedToday(kr.getId(), user);
                keyResultResponses.add(new KeyResultResponse(kr, checked));
            }
            responses.add(new ObjectiveResponse(objective, keyResultResponses));
        }
        return responses;
    }
}