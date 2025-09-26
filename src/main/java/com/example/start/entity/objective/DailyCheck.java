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
        uniqueConstraints = @UniqueConstraint(
                name = "uk_daily_check_kr_user_date",
                columnNames = {"key_result_id", "user_id", "check_date"}
        )
)
public class DailyCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "key_result_id", nullable = false)
    private KeyResult keyResult;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "check_date", nullable = false, updatable = false)
    private LocalDate date;

    @PrePersist
    void prePersist() {
        if (this.date == null) this.date = LocalDate.now();
    }
}
