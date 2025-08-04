package com.example.start.dto.objective;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class ObjectiveForm {
    private String title;
    private String description;



    // 최대 5개의 핵심 결과를 위한 리스트
    private List<KeyResultForm> keyResults = new ArrayList<>();

    public ObjectiveForm() {
        // 최대 5개까지 미리 만들어 둠 (빈 입력 필드 포함)
        for (int i = 0; i < 5; i++) {
            keyResults.add(new KeyResultForm());
        }
    }
}
