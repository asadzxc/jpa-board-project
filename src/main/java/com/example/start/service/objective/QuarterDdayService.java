package com.example.start.service.objective;

import com.example.start.dto.objective.QuarterDdayResponse;

public interface QuarterDdayService {
    QuarterDdayResponse getQuarterDday(Long krId, Long userId);
}
