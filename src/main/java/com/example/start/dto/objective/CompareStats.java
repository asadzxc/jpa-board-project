package com.example.start.dto.objective;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class CompareStats {
    private PeriodStats current;
    private PeriodStats previous;
    private int delta;
}
