package com.example.start.service.objective;

import com.example.start.entity.objective.Objective;
import com.example.start.entity.post.User;
import com.example.start.dto.objective.ObjectiveForm;

import java.util.List;

public interface ObjectiveService {

    // 목표 + 핵심 결과 저장
    void saveObjective(Objective objective, User user);

    // 유저별 OKR 목록 조회
    List<Objective> findByUser(User user);

    // 목표 단건 조회
    Objective findById(Long id);

    // 목표 삭제
    void deleteById(Long id);

    // 목표 수정
    void updateObjective(Long id, ObjectiveForm form, User user);

}