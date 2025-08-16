package com.example.start.repository.objective;

import com.example.start.entity.objective.DailyCheck;
import com.example.start.entity.objective.KeyResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyCheckRepository extends JpaRepository<DailyCheck, Long> {

    // ✅ 특정 KeyResult와 날짜로 체크 여부 조회
    Optional<DailyCheck> findByKeyResultAndDate(KeyResult keyResult, LocalDate date);

    // ✅ 해당 KeyResult에 대한 전체 체크 기록 조회 (예: 통계용)
    List<DailyCheck> findAllByKeyResult(KeyResult keyResult);

    boolean existsByKeyResultIdAndDate(Long keyResultId, LocalDate date);
}