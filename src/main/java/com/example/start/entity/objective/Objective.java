package com.example.start.entity.objective;

import com.example.start.entity.post.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
public class Objective {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    @Column(updatable = false)
    private LocalDateTime createdDate;  // ✅ 초기값 제거

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // ✅ Objective 삭제/수정 시 KR을 함께 관리
    @OneToMany(mappedBy = "objective", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<KeyResult> keyResults = new ArrayList<>();

    @PrePersist
    private void prePersist() {
        ZoneId seoul = ZoneId.of("Asia/Seoul");
        LocalDateTime now = LocalDateTime.now(seoul);

        // ✅ 최초 생성 시각 고정
        if (createdDate == null) createdDate = now;

        // ✅ “생성일(날짜)”을 시작일로 쓰고 싶으면 createdDate를 그대로 쓰거나,
        //    날짜만 깔끔하게 쓰고 싶으면 아래처럼 00:00으로 맞춰도 됨.
        if (startDate == null) startDate = createdDate; // 또는 createdDate.toLocalDate().atStartOfDay()

        // ✅ 마감일 = 시작일 + 90일
        if (endDate == null) endDate = startDate.plusDays(90); // “오늘 포함 90일”이면 89
    }
}

