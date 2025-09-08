package com.example.start.service.objective;

import com.example.start.entity.objective.Objective;
import com.example.start.entity.post.User;
import com.example.start.dto.objective.ObjectiveForm;
import com.example.start.dto.objective.ObjectiveResponse;

import java.util.List;

public interface ObjectiveService {
    void saveObjective(Objective objective, User user);
    List<Objective> findByUser(User user);
    Objective findById(Long id);
    void deleteById(Long id);
    void updateObjective(Long id, ObjectiveForm form, User loginUser);
    List<ObjectiveResponse> findObjectiveResponsesByUser(User user);
}