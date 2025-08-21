package com.example.start.entity.objective;

import com.example.start.entity.post.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor
@Table(
        name = "daily_check",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_user_kr_date",
                columnNames = {"user_id", "key_result_id", "check_date"}
        )
)
public class DailyCheck {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✔ 날짜 필드명 명확화(컬럼명 지정)
    @Column(name = "check_date", nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private boolean checked;

    // ✔ 메모(선택)
    @Column(length = 500)
    private String memo;

    // ✔ 누가 체크했는가 (직접 참조)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    // ✔ 어떤 KR에 대한 체크인가 (기존 유지)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "key_result_id")
    private KeyResult keyResult;

    // ✔ 어떤 Objective의 KR인가 (직접 참조: 통계/조회 최적화)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "objective_id")
    private Objective objective;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // 편의 생성자
    public DailyCheck(User user, Objective objective, KeyResult keyResult, LocalDate date, boolean checked) {
        this.user = user;
        this.objective = objective;
        this.keyResult = keyResult;
        this.date = date;
        this.checked = checked;
    }

    // 팩토리 메서드(메모 없는 기본 생성)
    public static DailyCheck of(User user, KeyResult kr, LocalDate date) {
        return new DailyCheck(user, kr.getObjective(), kr, date, false);
    }
}