package com.example.start.repository.objective;

import com.example.start.entity.objective.DailyCheck;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyCheckRepository extends JpaRepository<DailyCheck, Long> {

    // ✅ 오늘(특정 날짜) 체크 여부/레코드 조회: KR + USER + DATE
    Optional<DailyCheck> findByKeyResultIdAndUserIdAndDate(Long keyResultId, Long userId, LocalDate date);

    boolean existsByKeyResultIdAndUserIdAndDate(Long keyResultId, Long userId, LocalDate date);

    // ✅ 당주(혹은 임의 범위) 7일치 조회: KR + USER + [start, end]
    List<DailyCheck> findByKeyResultIdAndUserIdAndDateBetween(Long keyResultId, Long userId,
                                                              LocalDate start, LocalDate end);

    // (선택) KR-USER 전체 이력 보고 싶을 때
    List<DailyCheck> findByKeyResultIdAndUserId(Long keyResultId, Long userId);
}
