package com.example.start.service.objective;

import com.example.start.dto.objective.KeyResultWeekDetailResponse;
import com.example.start.entity.post.User;

import java.time.LocalDate;

public interface KeyResultDetailService {
    KeyResultWeekDetailResponse getWeekDetail(Long keyResultId, User loginUser, LocalDate refDate);
    void toggleToday(Long keyResultId, User loginUser, LocalDate today);
}