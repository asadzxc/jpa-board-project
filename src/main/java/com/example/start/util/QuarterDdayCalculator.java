package com.example.start.util;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public class QuarterDdayCalculator {

    private static final int SPAN_DAYS = 90;           // 표시 범위: 90..0
    private static final int CYCLE_UNIT = SPAN_DAYS + 1; // 91일 단위로 반복(0 포함)
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    public static QuarterDdayResult calc(LocalDate base, LocalDate today) {
        if (base == null) base = today;

        if (today.isBefore(base)) {
            int remainToStart = (int) ChronoUnit.DAYS.between(today, base);
            return QuarterDdayResult.beforeStart(base, remainToStart);
        }

        long diff = ChronoUnit.DAYS.between(base, today); // 0부터
        long cycleIndex = diff / CYCLE_UNIT;

        LocalDate cycleStart = base.plusDays(cycleIndex * CYCLE_UNIT);
        LocalDate deadline   = cycleStart.plusDays(SPAN_DAYS); // ✅ +90일 (마감일은 0)

        int remain = (int) ChronoUnit.DAYS.between(today, deadline); // ✅ 90..0
        int dayNo  = SPAN_DAYS - remain;                              // ✅ 0..90

        return new QuarterDdayResult(cycleIndex + 1, cycleStart, deadline, dayNo, remain);
    }

    public static QuarterDdayResult calc(LocalDate base) {
        return calc(base, LocalDate.now(KST));
    }

    public record QuarterDdayResult(
            long cycle,
            LocalDate start,
            LocalDate end,   // 여기서는 deadline(0이 되는 날)
            int dayNo,       // 0..90
            int remain       // 90..0
    ) {
        static QuarterDdayResult beforeStart(LocalDate start, int remainToStart) {
            return new QuarterDdayResult(0, start, start.plusDays(SPAN_DAYS), 0, remainToStart);
        }
    }
}