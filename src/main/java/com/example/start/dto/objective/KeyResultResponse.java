package com.example.start.dto.objective;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KeyResultResponse {
    private Long id;
    private String content;
    private int progress;
    private boolean checkedToday;
}