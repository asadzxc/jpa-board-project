package com.example.start.repository.objective;

import com.example.start.entity.objective.Objective;
import com.example.start.enums.ObjectiveStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ObjectiveRepository extends JpaRepository<Objective, Long> {

    // ✅ 전체(상태 상관없이) 목록
    List<Objective> findByUser_IdOrderByIdDesc(Long userId);

    // ✅ 상태별 목록 (ACTIVE / ARCHIVED)
    List<Objective> findByUser_IdAndStatusOrderByIdDesc(Long userId, ObjectiveStatus status);

    // ✅ 소유자 검증 포함 단건 조회 (보관/복원/삭제 등에 유용)
    Optional<Objective> findByIdAndUser_Id(Long id, Long userId);
}