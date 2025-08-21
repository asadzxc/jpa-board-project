package com.example.start.repository.objective;

import com.example.start.entity.objective.Objective;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ObjectiveRepository extends JpaRepository<Objective, Long> {

    // ✅ 서비스에서 쓰는 기본 메서드
    List<Objective> findByUser_IdOrderByIdAsc(Long userId);

    // ✅ (선택) N+1 방지용 fetch join 버전
    @Query("""
           select distinct o
           from Objective o
           left join fetch o.keyResults
           where o.user.id = :userId
           order by o.id asc
           """)
    List<Objective> findWithKeyResultsByUserIdOrderByIdAsc(@Param("userId") Long userId);
}