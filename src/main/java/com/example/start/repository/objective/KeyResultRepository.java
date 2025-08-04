package com.example.start.repository.objective;

import com.example.start.entity.objective.KeyResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KeyResultRepository extends JpaRepository<KeyResult, Long> {
    // 특정 Objective에 포함된 KR들 조회
    List<KeyResult> findByObjectiveId(Long objectiveId);
}