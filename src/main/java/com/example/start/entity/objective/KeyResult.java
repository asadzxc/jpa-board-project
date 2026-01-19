package com.example.start.entity.objective;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
public class KeyResult {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private int progress;

    // ✅ DB에 NOT NULL인 weight 컬럼과 매핑
    @Column(nullable = false)
    private Integer weight;

    // ✅ [NEW] 90일 분기 D-Day 기준 시작일
    @Column(nullable = false)
    private LocalDate quarterStartDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "objective_id", nullable = false)
    private Objective objective;

    // ✅ KR 삭제 시 DailyCheck도 같이 삭제
    @OneToMany(mappedBy = "keyResult", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DailyCheck> dailyChecks = new ArrayList<>();

    @PrePersist
    @PreUpdate
    private void ensureDefaults() {
        if (weight == null) weight = 1;      // 기본값 보장
        if (content == null) content = "";
        if (progress < 0) progress = 0;
        // ✅ [NEW] 시작일이 없으면 "오늘"로 자동 세팅
        if (quarterStartDate == null) quarterStartDate = LocalDate.now();
    }
}