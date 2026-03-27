package com.example.start.entity.objective;

import com.example.start.entity.post.User;
import com.example.start.enums.ObjectiveStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZoneId;
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

    private String title;
    private String description;

    @Column(updatable = false)
    private LocalDateTime createdDate;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ObjectiveStatus status = ObjectiveStatus.ACTIVE;

    private LocalDateTime archivedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "objective", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<KeyResult> keyResults = new ArrayList<>();

    @PrePersist
    private void prePersist() {
        ZoneId seoul = ZoneId.of("Asia/Seoul");
        LocalDateTime now = LocalDateTime.now(seoul);

        if (createdDate == null) {
            createdDate = now;
        }

        // 오늘 날짜의 시작 시각을 시작일로
        if (startDate == null) {
            startDate = now.toLocalDate().atStartOfDay();
        }

        // 시작일 포함 총 90일
        if (endDate == null) {
            endDate = startDate.plusDays(89);
        }
    }
}

