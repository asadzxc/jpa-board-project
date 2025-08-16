package com.example.start.dto.objective;

import lombok.Getter;
import com.example.start.entity.objective.KeyResult;

@Getter
public class KeyResultResponse {
    private Long id;
    private String content;
    private int progress;
    private boolean checkedToday;

    public KeyResultResponse(KeyResult keyResult, boolean checkedToday) {
        this.id = keyResult.getId();
        this.content = keyResult.getContent();
        this.progress = keyResult.getProgress();
        this.checkedToday = checkedToday;
    }
}
