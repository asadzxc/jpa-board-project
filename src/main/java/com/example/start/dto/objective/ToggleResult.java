package com.example.start.dto.objective;


import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ToggleResult {
    private Long krId;
    private boolean checked;
    private int weekCount;
    private int monthCount;
    private int streak;
    private String toast;

    public static ToggleResult fromWeekDetail(KeyResultWeekDetailResponse r) {
        return ToggleResult.builder()
                .krId(r.getKeyResultId())
                .checked(r.isCheckedToday())
                .weekCount(r.getWeekCount())
                .monthCount(r.getMonthCount())
                .streak(r.getStreak())
                .toast(r.isCheckedToday() ? "오늘 체크 완료! 💪" : "오늘 체크 해제")
                .build();
    }
}
