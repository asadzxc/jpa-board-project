package com.example.start.entity.objective;

import com.example.start.entity.post.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Objective {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;         // 목표 제목
    private String description;   // 목표 설명

    private LocalDate startDate;  // 시작일 (기본: 오늘)
    private LocalDate endDate;    // 종료일 (기본: 시작일 + 3개월)

    // 향후 연관될 KeyResult 리스트 (아직 클래스 안 만들었지만 자리만 미리 잡기)
    @OneToMany(mappedBy = "objective", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<KeyResult> keyResults = new ArrayList<>();

    // 나중에 로그인한 유저별 목표로 연결할 예정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 저장 전 자동 날짜 계산
    @PrePersist
    public void prePersist() {
        if (startDate == null) {
            startDate = LocalDate.now();
        }
        if (endDate == null) {
            endDate = startDate.plusMonths(3);
        }
    }
}
