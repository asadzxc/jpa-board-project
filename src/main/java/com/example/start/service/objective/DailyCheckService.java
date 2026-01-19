
package com.example.start.service.objective;

import com.example.start.dto.objective.KeyResultWeekDetailResponse;
import com.example.start.dto.objective.KeyResultMonthCalendarResponse;
import com.example.start.entity.post.User;

import java.time.YearMonth;

public interface DailyCheckService {
    KeyResultWeekDetailResponse getThisWeekDetail(Long keyResultId, User loginUser);
    KeyResultWeekDetailResponse toggleToday(Long keyResultId, User loginUser);

    boolean isCheckedToday(Long keyResultId, User loginUser);

    void deleteKeyResult(Long keyResultId, User loginUser);

    // ✅ [NEW] 월 달력(체크 날짜들)
    KeyResultMonthCalendarResponse getMonthCalendar(Long keyResultId, User loginUser, YearMonth ym);
}

