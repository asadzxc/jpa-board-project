package com.example.start.entity.objective;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class DailyCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;      // 체크한 날짜
    private boolean checked;     // 실천 여부

    // ✅ KeyResult와 연관관계 (N:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "key_result_id")
    private KeyResult keyResult;

    // ✅ 생성자 (KeyResult, 날짜, 체크여부 입력용)
    public DailyCheck(KeyResult keyResult, LocalDate date, boolean checked) {
        this.keyResult = keyResult;
        this.date = date;
        this.checked = checked;
    }
}