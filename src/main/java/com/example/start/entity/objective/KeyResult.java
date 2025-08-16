package com.example.start.entity.objective;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.ArrayList;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class KeyResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;   // 핵심 결과 내용 (예: 블로그 글 5개 작성)
    private int progress;     // 진행률 (0~100)

    // Objective와의 연관 관계 (N:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "objective_id")
    private Objective objective;

    // ✅ 실천 체크 기록 연관관계 추가 (1:N)
    @OneToMany(mappedBy = "keyResult", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DailyCheck> dailyChecks = new ArrayList<>();
}