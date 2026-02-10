
package com.example.start.dto.objective;

import com.example.start.entity.objective.KeyResult;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter @Setter
@NoArgsConstructor
public class KeyResultWeekDetailResponse {

    // ===== 기본 식별/표시 =====
    private Long keyResultId;
    private String keyResultContent;

    // [NEW] 같은 OKR의 다른 KR 목록을 가져오기 위한 Objective id
    private Long objectiveId;

    // ===== 기준 날짜/주간 범위 =====
    private LocalDate refDate;
    private LocalDate weekStart;
    private LocalDate weekEnd;

    // ===== 체크 정보 =====
    private Set<LocalDate> checkedDays = new LinkedHashSet<>();
    private int checkedCount;
    private int percent;

    // ===== Ajax 토글용 추가 집계 =====
    private boolean checkedToday;
    private int weekCount;
    private int monthCount;
    private int streak;

    public KeyResultWeekDetailResponse(KeyResult kr, LocalDate today, Set<LocalDate> checkedDays) {
        this.keyResultId = kr.getId();
        this.keyResultContent = kr.getContent();
        this.refDate = today;

        // ✅✅✅ [CHANGED] objectiveId 세팅 추가 (드롭다운에 필요한 핵심 값)
        if (kr.getObjective() != null) {
            this.objectiveId = kr.getObjective().getId();
        }
        // ✅✅✅ [CHANGED] 끝

        this.weekStart = startOfWeek(today);
        this.weekEnd   = endOfWeek(today);

        if (checkedDays != null) {
            this.checkedDays = new LinkedHashSet<>(checkedDays);
        }

        this.checkedCount = this.checkedDays.size();
        int totalDays = 7;
        this.percent = Math.round((this.checkedCount * 100.0f) / totalDays);
    }

    private LocalDate startOfWeek(LocalDate d) {
        return d.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    private LocalDate endOfWeek(LocalDate d) {
        return d.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
    }

    /**
     * [NEW] 템플릿에서 사용하는 detail.days
     * 주(weekStart ~ weekEnd) 날짜를 월~일 순으로 반환합니다.
     * 필드가 없어도 게터만 있으면 SpEL에서 'days' 프로퍼티로 인식됩니다.
     */
    public List<LocalDate> getDays() {
        LocalDate start = (weekStart != null) ? weekStart
                : startOfWeek(refDate != null ? refDate : LocalDate.now());
        LocalDate end   = (weekEnd != null) ? weekEnd
                : endOfWeek(refDate != null ? refDate : LocalDate.now());

        List<LocalDate> days = new ArrayList<>(7);
        for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
            days.add(d);
        }
        return days;
    }

    public boolean isCheckedOn(LocalDate date) {
        return checkedDays != null && checkedDays.contains(date);
    }
}

