package com.example.start.service.objective;


import java.time.LocalDate;




public interface DailyCheckService {
    void toggleCheck(Long keyResultId);
    boolean isCheckedToday(Long keyResultId);
    boolean isChecked(Long keyResultId, LocalDate date);
}