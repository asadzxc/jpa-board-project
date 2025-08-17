package com.example.start.controller.objective;

import com.example.start.repository.objective.KeyResultRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/okr/key-results")
public class KeyResultController {

    private final KeyResultRepository keyResultRepository;

    // ✅ 필터 없이 POST로 삭제
    @PostMapping("/{keyResultId}")
    public String deleteKeyResult(
            @PathVariable Long keyResultId

    ) {
        // (선택) 존재 여부만 확인
        if (!keyResultRepository.existsById(keyResultId)) {
            throw new EntityNotFoundException("핵심 결과가 없습니다. id=" + keyResultId);
        }

        // 삭제 (연관 DailyCheck는 cascade + orphanRemoval 로 함께 삭제)
        keyResultRepository.deleteById(keyResultId);

        // 삭제 후 목록/상세로 복귀
        return "redirect:/okr";
    }
}