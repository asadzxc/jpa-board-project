package com.example.start.serviceimpl.objective;

import com.example.start.entity.objective.DailyCheck;
import com.example.start.entity.objective.KeyResult;
import com.example.start.repository.objective.DailyCheckRepository;
import com.example.start.repository.objective.KeyResultRepository;
import com.example.start.service.objective.DailyCheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DailyCheckServiceImpl implements DailyCheckService {

    private final DailyCheckRepository dailyCheckRepository;
    private final KeyResultRepository keyResultRepository;

    @Override
    public void toggleCheck(Long keyResultId) {
        KeyResult keyResult = keyResultRepository.findById(keyResultId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 핵심 결과입니다."));

        LocalDate today = LocalDate.now();

        DailyCheck dailyCheck = dailyCheckRepository.findByKeyResultAndDate(keyResult, today)
                .orElse(null);

        if (dailyCheck == null) {
            // ✅ 체크 안 되어있으면 → 새로 생성
            dailyCheck = new DailyCheck(keyResult, today, true);
            dailyCheckRepository.save(dailyCheck);
        } else {
            // ✅ 체크 되어있으면 → 삭제 (체크 해제)
            dailyCheckRepository.delete(dailyCheck);
        }
    }

    @Override
    public boolean isCheckedToday(Long keyResultId) {
        KeyResult keyResult = keyResultRepository.findById(keyResultId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 핵심 결과입니다."));

        return dailyCheckRepository.findByKeyResultAndDate(keyResult, LocalDate.now())
                .map(DailyCheck::isChecked)
                .orElse(false);
    }

    @Override
    public boolean isChecked(Long keyResultId, LocalDate date) {
        return dailyCheckRepository.existsByKeyResultIdAndDate(keyResultId, date);
    }
}