package com.example.start.dto.objective;

import com.example.start.entity.objective.Objective;
import lombok.Getter;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Getter
public class ObjectiveResponse {
    private long id;
    private String title;
    private String description;
    private String dDay;
    private List<KeyResultResponse> keyResults;
    private int quarterRemain;

    // ✅ 기존 생성자 유지(필요하면)
    public ObjectiveResponse(Objective objective, List<KeyResultResponse> keyResults) {
        this(objective, keyResults, 0);
    }

    // ✅ NEW: quarterRemain까지 받는 생성자
    public ObjectiveResponse(Objective objective, List<KeyResultResponse> keyResults, int quarterRemain) {
        this.id = objective.getId();
        this.title = objective.getTitle();
        this.description = objective.getDescription();

        LocalDate baseDate = null;
        if (objective.getEndDate() != null) {
            baseDate = objective.getEndDate().toLocalDate();
        } else if (objective.getStartDate() != null) {
            baseDate = objective.getStartDate().toLocalDate();
        } else if (objective.getCreatedDate() != null) {
            baseDate = objective.getCreatedDate().toLocalDate();
        }

        this.dDay = (baseDate != null) ? calculateDDay(baseDate) : "-";
        this.keyResults = keyResults;

        this.quarterRemain = quarterRemain; // ✅ 여기!
    }


    // ✅ 오늘 기준 D-day 문자열 계산: D-3 / D-Day / D+2
    private String calculateDDay(LocalDate targetDate) {
        long diff = ChronoUnit.DAYS.between(LocalDate.now(), targetDate);
        if (diff == 0) return "D-Day";
        if (diff > 0) return "D-" + diff;
        return "D+" + Math.abs(diff);
    }
}
