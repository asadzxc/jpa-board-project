package com.example.start.serviceimpl.objective;

import com.example.start.dto.objective.QuarterDdayResponse;
import com.example.start.entity.objective.KeyResult;
import com.example.start.repository.objective.KeyResultRepository;
import com.example.start.service.objective.QuarterDdayService;
import com.example.start.util.QuarterDdayCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class QuarterDdayServiceImpl implements QuarterDdayService {

    private final KeyResultRepository keyResultRepository;

    @Override
    public QuarterDdayResponse getQuarterDday(Long krId, Long userId) {
        KeyResult kr = keyResultRepository.findById(krId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "KR not found"));

        // ✅ 권한 체크(네 프로젝트 방식에 맞춰 조정)
        // 예: kr.getObjective().getUser().getId() 이런 식으로 owner 확인
        Long ownerId = kr.getObjective().getUser().getId(); // Objective에 user가 있다고 가정
        if (!ownerId.equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No permission");
        }

        LocalDate base;
        if (kr.getObjective().getStartDate() != null) {
            base = kr.getObjective().getStartDate().toLocalDate();
        } else if (kr.getObjective().getCreatedDate() != null) {
            base = kr.getObjective().getCreatedDate().toLocalDate();
        } else {
            base = LocalDate.now();
        }

        var r = QuarterDdayCalculator.calc(base);

        return new QuarterDdayResponse(
                r.cycle(),
                r.start().toString(),
                r.end().toString(),
                r.dayNo(),
                r.remain()
        );
    }
}