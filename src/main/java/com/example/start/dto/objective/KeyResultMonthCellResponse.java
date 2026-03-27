package com.example.start.dto.objective;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class KeyResultMonthCellResponse {
    private LocalDate date;
    private int dayOfMonth;

    private boolean currentMonth;
    private boolean today;
    private boolean checked;
}
