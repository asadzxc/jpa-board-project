package com.example.start.serviceimpl.objective;

import com.example.start.dto.objective.KeyResultForm;
import com.example.start.entity.objective.KeyResult;
import com.example.start.entity.objective.Objective;
import com.example.start.entity.post.User;
import com.example.start.repository.objective.KeyResultRepository;
import com.example.start.repository.objective.ObjectiveRepository;
import com.example.start.service.objective.KeyResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class KeyResultServiceImpl implements KeyResultService {

    private final ObjectiveRepository objectiveRepository;
    private final KeyResultRepository keyResultRepository;

    @Override
    @Transactional
    public Long addKeyResult(Long objectiveId, KeyResultForm form, User loginUser) {
        Objective obj = objectiveRepository.findById(objectiveId)
                .orElseThrow(() -> new IllegalArgumentException("Objective가 존재하지 않습니다."));

        if (!obj.getUser().getId().equals(loginUser.getId())) {
            throw new IllegalStateException("본인의 Objective에만 KR을 추가할 수 있습니다.");
        }
        if (form.getContent() == null || form.getContent().isBlank()) {
            throw new IllegalArgumentException("KR 내용은 비어 있을 수 없습니다.");
        }

        KeyResult kr = new KeyResult();
        kr.setObjective(obj);
        kr.setContent(form.getContent().trim());
        if (form.getProgress() != null) kr.setProgress(form.getProgress());

        KeyResult saved = keyResultRepository.save(kr);
        obj.getKeyResults().add(saved); // 양방향일 때
        return saved.getId();
    }

    @Override
    @Transactional
    public void updateKeyResult(Long krId, KeyResultForm form, User loginUser) {
        KeyResult kr = keyResultRepository.findById(krId)
                .orElseThrow(() -> new IllegalArgumentException("KeyResult가 존재하지 않습니다."));

        if (!kr.getObjective().getUser().getId().equals(loginUser.getId())) {
            throw new IllegalStateException("본인의 KR만 수정할 수 있습니다.");
        }

        if (form.getContent() != null && !form.getContent().isBlank()) {
            kr.setContent(form.getContent().trim());
        }
        if (form.getProgress() != null) {
            kr.setProgress(form.getProgress());
        }
        // Dirty checking으로 반영
    }
}