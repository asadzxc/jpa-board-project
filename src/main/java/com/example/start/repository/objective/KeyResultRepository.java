package com.example.start.repository.objective;

import com.example.start.entity.objective.KeyResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface KeyResultRepository extends JpaRepository<KeyResult, Long> {

    // ✅ 추가: 소유자 검증을 위해 objective와 user를 한 번에 로딩
    @Query("""
        select kr from KeyResult kr
        join fetch kr.objective o
        join fetch o.user u
        where kr.id = :id
    """)
    Optional<KeyResult> findByIdWithObjectiveAndUser(@Param("id") Long id);
}