package com.example.start.dto.objective;

public record QuarterDdayResponse(
        long cycle,
        String start,
        String end,
        int dayNo,
        int remain
) {}
