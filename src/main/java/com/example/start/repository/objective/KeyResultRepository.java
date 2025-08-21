package com.example.start.repository.objective;

import com.example.start.entity.objective.KeyResult;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface KeyResultRepository extends JpaRepository<KeyResult, Long> {
    // Objective.user.id = ?
    List<KeyResult> findByObjective_User_Id(Long userId);
}