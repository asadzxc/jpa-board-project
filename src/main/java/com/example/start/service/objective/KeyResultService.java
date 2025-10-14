package com.example.start.service.objective;

import com.example.start.dto.objective.KeyResultForm;
import com.example.start.entity.post.User;



public interface KeyResultService {
    Long addKeyResult(Long objectiveId, KeyResultForm form, User loginUser);
    void updateKeyResult(Long krId, KeyResultForm form, User loginUser);
}
