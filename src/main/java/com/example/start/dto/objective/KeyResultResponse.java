package com.example.start.dto.objective;

import com.example.start.entity.objective.KeyResult;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class KeyResultResponse {
    private Long id;
    private String content;
    private int progress = 0;

    // ▼ 템플릿/JS에서 참조하는 필드들
    private boolean checkedToday = false;  // [NEW]
    private int weekCount = 0;             // [NEW]
    private int monthCount = 0;            // [NEW]
    private int streak = 0;                // [NEW]

    public KeyResultResponse(KeyResult kr) {  // [KEEP]
        this.id = kr.getId();
        this.content = kr.getContent();
        this.progress = kr.getProgress();
    }

    // (선택) 한 번에 채우고 싶을 때 쓰는 풀 생성자
    public KeyResultResponse(KeyResult kr,
                             boolean checkedToday,
                             int weekCount,
                             int monthCount,
                             int streak) {     // [NEW]
        this(kr);
        this.checkedToday = checkedToday;
        this.weekCount = weekCount;
        this.monthCount = monthCount;
        this.streak = streak;
    }
}
