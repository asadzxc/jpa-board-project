package com.example.start.repository.objective;

import com.example.start.entity.objective.Objective;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ObjectiveRepository extends JpaRepository<Objective, Long> {
    // 로그인한 유저의 OKR만 조회할 때 사용
    List<Objective> findByUserId(Long userId);
}