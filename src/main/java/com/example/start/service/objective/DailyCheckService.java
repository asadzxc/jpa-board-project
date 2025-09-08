
package com.example.start.service.objective;

import com.example.start.dto.objective.KeyResultWeekDetailResponse;
import com.example.start.entity.post.User;

public interface DailyCheckService {
    KeyResultWeekDetailResponse getThisWeekDetail(Long keyResultId, User loginUser);
    KeyResultWeekDetailResponse toggleToday(Long keyResultId, User loginUser);

    boolean isCheckedToday(Long keyResultId, User loginUser);
}
