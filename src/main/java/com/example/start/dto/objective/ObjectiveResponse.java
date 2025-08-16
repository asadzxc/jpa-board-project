package com.example.start.dto.objective;

import com.example.start.dto.objective.KeyResultResponse;
import com.example.start.entity.objective.Objective;
import lombok.Getter;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Getter
public class ObjectiveResponse {
    private Long id;
    private String title;
    private String description;
    private String dDay;
    private List<KeyResultResponse> keyResults;

    // ✅ 이 생성자가 반드시 있어야 빨간 줄 사라짐!
    public ObjectiveResponse(Objective objective, List<KeyResultResponse> keyResults) {
        this.id = objective.getId();
        this.title = objective.getTitle();
        this.description = objective.getDescription();
        this.dDay = calculateDDay(objective.getCreatedDate());
        this.keyResults = keyResults;
    }

    private String calculateDDay(LocalDate createdDate) {
        if (createdDate == null) return "D+0";
        long days = ChronoUnit.DAYS.between(createdDate, LocalDate.now());
        return days == 0 ? "D-DAY" : (days > 0 ? "D+" + days : "D" + days);
    }
}