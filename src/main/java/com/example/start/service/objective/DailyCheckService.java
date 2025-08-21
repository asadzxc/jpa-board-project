package com.example.start.service.objective;


import com.example.start.entity.post.User;
import java.time.LocalDate;

public interface DailyCheckService {

    // 오늘 기준 편의 오버로드
    void toggleCheck(User user, Long keyResultId);
    boolean isCheckedToday(User user, Long keyResultId);

    // 특정 날짜 기준
    void toggleCheck(User user, Long keyResultId, LocalDate date);
    boolean isChecked(User user, Long keyResultId, LocalDate date);
}