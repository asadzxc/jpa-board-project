
package com.example.start.dto.objective;

import com.example.start.entity.objective.KeyResult;
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Getter
public class KeyResultWeekDetailResponse {

    private final Long keyResultId;
    private final String keyResultContent;

    private final LocalDate weekStart;   // 월요일
    private final LocalDate weekEnd;     // 일요일
    private final List<LocalDate> days;  // 7칸 날짜
    private final Set<LocalDate> checkedDays; // 체크된 날짜 집합(당주)
    private final int checkedCount;      // 체크된 개수
    private final int percent;           // (checkedCount / 7) * 100

    public KeyResultWeekDetailResponse(KeyResult kr, Collection<LocalDate> checked) {
        this.keyResultId = kr.getId();
        this.keyResultContent = kr.getContent();

        LocalDate today = LocalDate.now();
        this.weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        this.weekEnd   = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        List<LocalDate> tmp = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            tmp.add(weekStart.plusDays(i));
        }
        this.days = Collections.unmodifiableList(tmp);

        this.checkedDays = Set.copyOf(checked);
        this.checkedCount = (int) days.stream().filter(checkedDays::contains).count();
        this.percent = (int) Math.round((checkedCount / 7.0) * 100.0);
    }

    public boolean isChecked(LocalDate date) {
        return checkedDays.contains(date);
    }
}

