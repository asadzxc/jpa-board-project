package com.example.start.dto.objective;

import com.example.start.entity.objective.Objective;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.time.LocalDateTime;

@Getter @Setter
public class ObjectiveResponse {
    private long id;
    private String title;
    private String description;
    private String dDay;
    private List<KeyResultResponse> keyResults;
    private int quarterRemain;

    private int quarterTotalDays;        // 분기 총 일수
    private int quarterElapsedDays;      // 분기 경과 일수(오늘 포함)
    private int quarterProgressPercent;  // 분기 진행률(0~100)

    private int krCount;                // KR 개수
    private int todayCheckedCount;      // 오늘 체크된 KR 개수

    private long weekChecks;            // 이번주 체크 횟수(기록 수)
    private long monthChecks;           // 이번달 체크 횟수
    private long quarterChecks;         // 분기 체크 횟수(누적)

    private int achievementPercent;     // (오늘까지) 달성률 %

    private LocalDateTime createdDate;
    private LocalDateTime startDate;
    private LocalDateTime endDate;



    // ✅ 기존 생성자 유지(필요하면)
    public ObjectiveResponse(Objective objective, List<KeyResultResponse> keyResults) {
        this(objective, keyResults, 0);
    }

    // ✅ NEW: quarterRemain까지 받는 생성자
    public ObjectiveResponse(Objective objective, List<KeyResultResponse> keyResults, int quarterRemain) {
        this.id = objective.getId();
        this.title = objective.getTitle();
        this.description = objective.getDescription();

        // ✅ 생성일/시작일/마감일 확정
        this.createdDate = objective.getCreatedDate();

        this.startDate = (objective.getStartDate() != null)
                ? objective.getStartDate()
                : this.createdDate;

        this.endDate = (objective.getEndDate() != null)
                ? objective.getEndDate()
                : (this.startDate != null ? this.startDate.plusDays(90) : null);
        // "오늘 포함 90일"이면 plusDays(89)

        // ✅ D-day는 마감일(endDate) 기준
        LocalDate baseDate = null;
        if (this.endDate != null) baseDate = this.endDate.toLocalDate();
        else if (this.startDate != null) baseDate = this.startDate.toLocalDate();
        else if (this.createdDate != null) baseDate = this.createdDate.toLocalDate();

        this.dDay = (baseDate != null) ? calculateDDay(baseDate) : "-";
        this.keyResults = keyResults;
        this.quarterRemain = quarterRemain;
    }


    // ✅ 오늘 기준 D-day 문자열 계산: D-3 / D-Day / D+2
    private String calculateDDay(LocalDate targetDate) {
        long diff = ChronoUnit.DAYS.between(LocalDate.now(), targetDate);
        if (diff == 0) return "D-Day";
        if (diff > 0) return "D-" + diff;
        return "D+" + Math.abs(diff);
    }
}
