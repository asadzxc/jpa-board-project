package com.example.start.entity.objective;

import com.example.start.entity.post.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "daily_check",
        // ✅ 유저별로 같은 KR을 같은 날에 중복 체크 불가
        uniqueConstraints = @UniqueConstraint(columnNames = {"key_result_id", "user_id", "check_date"})
)
public class DailyCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ 어떤 KR에 대한 체크인지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "key_result_id", nullable = false)
    private KeyResult keyResult;

    // ✅ 누가 체크했는지 (세션의 loginUser)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // ✅ ‘하루’ 단위 체크 날짜
    @Column(name = "check_date", nullable = false, updatable = false)
    private LocalDate date;

    /**
     * ✅ 굳이 상태값(checked)을 둘 필요가 없어서 제거 권장
     *    - 레코드가 존재하면 '체크됨', 없으면 '체크 안 됨'
     *    - 기존 필드를 유지하고 싶다면 주석 해제하고 기본 true로 저장하세요.
     */
    // @Column(nullable = false)
    // private boolean checked;

    public DailyCheck(KeyResult keyResult, User user, LocalDate date /*, boolean checked */) {
        this.keyResult = keyResult;
        this.user = user;
        this.date = date;
        // this.checked = checked;
    }

    @PrePersist
    void prePersist() {
        if (this.date == null) this.date = LocalDate.now();
        // if (this.checked == false) this.checked = true; // checked 필드를 유지한다면 기본값 세팅
    }
}
