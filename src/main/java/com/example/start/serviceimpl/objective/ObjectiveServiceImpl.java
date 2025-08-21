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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ObjectiveServiceImpl implements ObjectiveService {

    private final ObjectiveRepository objectiveRepository;
    private final DailyCheckService dailyCheckService;

    @Override
    @Transactional
    public void saveObjective(Objective objective, User owner) {
        objective.setUser(owner); // 유저 연결
        objectiveRepository.save(objective);
    }

    @Override
    @Transactional
    public List<Objective> findByUser(User user) {
        return objectiveRepository.findByUser_IdOrderByIdAsc(user.getId());
    }

    @Override
    @Transactional
    public Objective findById(Long id) {
        return objectiveRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Objective not found: " + id));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        objectiveRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void updateObjective(Long id, ObjectiveForm form, User user) {
        Objective obj = findById(id);
        if (!obj.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("권한이 없습니다.");
        }
        obj.setTitle(form.getTitle());
        obj.setDescription(form.getDescription());
        // KR 수정 로직은 필요시 추가
    }

    @Override
    @Transactional
    public List<ObjectiveResponse> findObjectiveResponsesByUser(User user) {
        List<Objective> objectives = objectiveRepository.findByUser_IdOrderByIdAsc(user.getId());
        LocalDate today = LocalDate.now();

        List<ObjectiveResponse> result = new ArrayList<>();
        for (Objective obj : objectives) {
            List<KeyResultResponse> krDtos = new ArrayList<>();
            for (KeyResult kr : obj.getKeyResults()) {
                boolean checkedToday = dailyCheckService.isChecked(user, kr.getId(), today);
                krDtos.add(new KeyResultResponse(
                        kr.getId(),
                        kr.getContent(),    // ✅ 실제 필드명 맞춰서 교체
                        kr.getProgress(),   // ✅ 실제 필드명 맞춰서 교체
                        checkedToday
                ));
            }

            // ✅ ObjectiveResponse에 D-Day 포함 (마감일 있는 경우)
            String dDay = null;
            if (obj.getEndDate() != null) {
                long days = ChronoUnit.DAYS.between(today, obj.getEndDate());
                if (days > 0) dDay = "D-" + days;
                else if (days == 0) dDay = "D-Day";
                else dDay = "D+" + Math.abs(days);
            }

            result.add(new ObjectiveResponse(
                    obj.getId(),
                    obj.getTitle(),
                    obj.getDescription(),
                    krDtos,
                    dDay
            ));
        }
        return result;
    }
}