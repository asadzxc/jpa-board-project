package com.example.start.dto.objective;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class KeyResultMonthDetailResponse {

    private int year;
    private int month;

    private LocalDate firstDayOfMonth;
    private LocalDate calendarStartDate;
    private LocalDate calendarEndDate;

    private int prevYear;
    private int prevMonth;
    private int nextYear;
    private int nextMonth;

    private List<List<KeyResultMonthCellResponse>> weeks = new ArrayList<>();
}
