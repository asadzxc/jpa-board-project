package com.example.start.serviceimpl.objective;

import com.example.start.entity.objective.Objective;
import com.example.start.entity.post.User;
import com.example.start.repository.objective.ObjectiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.start.service.objective.ObjectiveService;
import com.example.start.dto.objective.ObjectiveForm;
import java.util.ArrayList;
import com.example.start.entity.objective.KeyResult;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ObjectiveServiceImpl implements ObjectiveService {

    private final ObjectiveRepository objectiveRepository;

    @Override
    public void saveObjective(Objective objective, User user) {
        objective.setUser(user);  // 로그인한 사용자 설정
        objectiveRepository.save(objective);  // Cascade로 KeyResult도 자동 저장
    }

    @Override
    public List<Objective> findByUser(User user) {
        return objectiveRepository.findByUserId(user.getId());
    }

    @Override
    public Objective findById(Long id) {
        return objectiveRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 목표를 찾을 수 없습니다."));
    }

    @Override
    public void deleteById(Long id) {
        objectiveRepository.deleteById(id);
    }

    @Override
    public void updateObjective(Long id, ObjectiveForm form, User loginUser) {
        // 1. 기존 Objective 조회 및 권한 확인
        Objective objective = objectiveRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 목표가 없습니다."));

        if (!objective.getUser().getId().equals(loginUser.getId())) {
            throw new IllegalStateException("본인의 OKR만 수정할 수 있습니다.");
        }

        // 2. 제목 & 설명 수정
        objective.setTitle(form.getTitle());
        objective.setDescription(form.getDescription());

        // 3. KeyResult 초기화
        if (objective.getKeyResults() == null) {
            objective.setKeyResults(new ArrayList<>());
        } else {
            objective.getKeyResults().clear();
        }

        // 4. 새 KeyResult 추가 (빈 값 제외)
        if (form.getKeyResults() != null) {
            for (var krForm : form.getKeyResults()) {
                if (krForm.getContent() != null && !krForm.getContent().trim().isEmpty()) {
                    var newKR = new KeyResult();
                    newKR.setContent(krForm.getContent());
                    newKR.setProgress(krForm.getProgress() == null ? 0 : krForm.getProgress());
                    newKR.setObjective(objective); // 연관관계 설정

                    objective.getKeyResults().add(newKR); // ✅ 여기서 빨간 줄 생기던 부분 해결됨
                }
            }
        }

        objectiveRepository.save(objective);
    }






}