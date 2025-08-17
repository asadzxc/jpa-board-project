package com.example.start.repository.objective;

import com.example.start.entity.objective.KeyResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface KeyResultRepository extends JpaRepository<KeyResult, Long> {
    // 특정 Objective에 포함된 KR들 조회
    List<KeyResult> findByObjectiveId(Long objectiveId);

    @Query("""
        select (count(kr) > 0)
        from KeyResult kr
        join kr.objective o
        join o.user u
        where kr.id = :krId and u.id = :userId
    """)
    boolean existsOwnedBy(@Param("krId") Long krId, @Param("userId") Long userId);
}