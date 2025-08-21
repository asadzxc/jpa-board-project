package com.example.start.dto.objective;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ObjectiveResponse {
    private Long id;
    private String title;
    private String description;
    private List<KeyResultResponse> keyResults;
    private String dDay; // ✅ 오늘 기준 D-Day 문자열 (없으면 null)
}