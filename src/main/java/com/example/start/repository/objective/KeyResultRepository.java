package com.example.start.repository.objective;

import com.example.start.entity.objective.KeyResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface KeyResultRepository extends JpaRepository<KeyResult, Long> {

    // 소유자 검증용 (Objective + User fetch)
    @Query("""
        select kr from KeyResult kr
        join fetch kr.objective o
        join fetch o.user u
        where kr.id = :id
    """)
    Optional<KeyResult> findByIdWithObjectiveAndUser(@Param("id") Long id);

    // 같은 Objective의 KR 목록
    List<KeyResult> findByObjective_IdOrderByIdAsc(Long objectiveId);

    // ✅ 이전 KR
    Optional<KeyResult> findFirstByObjective_IdAndIdLessThanOrderByIdDesc(Long objectiveId, Long currentKrId);

    // ✅ 다음 KR
    Optional<KeyResult> findFirstByObjective_IdAndIdGreaterThanOrderByIdAsc(Long objectiveId, Long currentKrId);
}
