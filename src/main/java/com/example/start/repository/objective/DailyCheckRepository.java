package com.example.start.repository.objective;

import com.example.start.entity.objective.DailyCheck;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyCheckRepository extends JpaRepository<DailyCheck, Long> {

    // ✅ 기본: 유저의 특정 날짜 체크리스트 전체 조회 (화면 렌더링용)
    List<DailyCheck> findByUser_IdAndDateOrderByIdAsc(Long userId, LocalDate date);

    // ✅ 생성/중복검사용: 유저 + KR + 날짜
    Optional<DailyCheck> findByUser_IdAndKeyResult_IdAndDate(Long userId, Long keyResultId, LocalDate date);

    // ✅ 빠른 존재 여부 체크
    boolean existsByUser_IdAndKeyResult_IdAndDate(Long userId, Long keyResultId, LocalDate date);

    // --- 아래는 이전 코드 호환을 위해 잠시 유지 가능 (권장: 점진 폐기) ---

    /**
     * ⚠️ 다중 유저 환경에서 모호. User 조건이 없어 충돌 위험.
     *    새 코드에선 사용 지양하고 위의 메서드로 대체하세요.
     */
    @Deprecated
    Optional<DailyCheck> findByKeyResult_IdAndDate(Long keyResultId, LocalDate date);

    /**
     * ⚠️ 통계용으로 KR 전체 이력을 볼 수 있으나,
     *    유저 범위를 좁히지 않으면 팀/다계정 혼재 시 의도와 다를 수 있음.
     *    필요 시 findAllByKeyResult_IdAndUser_Id(...) 같은 범위 제한 메서드 추가 권장.
     */
    @Deprecated
    List<DailyCheck> findAllByKeyResult_Id(Long keyResultId);
}