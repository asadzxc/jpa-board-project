package com.example.start.dto.objective;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class PeriodStats {
    private LocalDate start;
    private LocalDate end;
    private long checkedDays;
    private long totalDays;
    private int rate;
    private List<LocalDate> checkedDates;
}