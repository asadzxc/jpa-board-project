package com.example.start.dto.objective;


import lombok.Getter;

import java.util.List;

@Getter
public class KeyResultMonthCalendarResponse {
    private final Long keyResultId;
    private final String ym;              // "2026-01"
    private final List<String> checkedDates; // ["2026-01-01", "2026-01-03", ...]

    public KeyResultMonthCalendarResponse(Long keyResultId, String ym, List<String> checkedDates) {
        this.keyResultId = keyResultId;
        this.ym = ym;
        this.checkedDates = checkedDates;
    }
}
