package com.example.start.serviceimpl.objective;

import com.example.start.entity.objective.DailyCheck;
import com.example.start.entity.objective.KeyResult;
import com.example.start.entity.post.User;
import com.example.start.repository.objective.DailyCheckRepository;
import com.example.start.repository.objective.KeyResultRepository;
import com.example.start.service.objective.DailyCheckService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DailyCheckServiceImpl implements DailyCheckService {

    private final DailyCheckRepository dailyCheckRepository;
    private final KeyResultRepository keyResultRepository;

    @Override
    @Transactional
    public void toggleCheck(User user, Long keyResultId) {
        toggleCheck(user, keyResultId, LocalDate.now());
    }

    @Override
    @Transactional
    public boolean isCheckedToday(User user, Long keyResultId) {
        return isChecked(user, keyResultId, LocalDate.now());
    }

    @Override
    @Transactional
    public void toggleCheck(User user, Long keyResultId, LocalDate date) {
        KeyResult kr = keyResultRepository.findById(keyResultId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 핵심 결과입니다."));

        // KR 소유권 검증 (이중 방어)
        if (!kr.getObjective().getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("권한이 없습니다.");
        }

        DailyCheck dc = dailyCheckRepository
                .findByUser_IdAndKeyResult_IdAndDate(user.getId(), keyResultId, date)
                .orElseGet(() -> {
                    // 없으면 생성(초기 checked=false)
                    DailyCheck created = DailyCheck.of(user, kr, date);
                    return dailyCheckRepository.save(created);
                });

        // 삭제 대신 checked 플래그 토글
        dc.setChecked(!dc.isChecked());
        dailyCheckRepository.save(dc);
    }

    @Override
    @Transactional
    public boolean isChecked(User user, Long keyResultId, LocalDate date) {
        return dailyCheckRepository
                .findByUser_IdAndKeyResult_IdAndDate(user.getId(), keyResultId, date)
                .map(DailyCheck::isChecked)
                .orElse(false);
    }
}