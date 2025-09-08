package com.example.start.repository.objective;

import com.example.start.entity.objective.Objective;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ObjectiveRepository extends JpaRepository<Objective, Long> {
    List<Objective> findByUserId(Long userId);
}