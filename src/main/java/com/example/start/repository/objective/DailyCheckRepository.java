package com.example.start.repository.objective;

import com.example.start.entity.objective.DailyCheck;
import com.example.start.entity.objective.KeyResult;
import com.example.start.entity.post.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyCheckRepository extends JpaRepository<DailyCheck, Long> {

    List<DailyCheck> findByKeyResultAndUserAndDateBetween(
            KeyResult keyResult, User user, LocalDate start, LocalDate end
    );

    Optional<DailyCheck> findByKeyResultAndUserAndDate(
            KeyResult keyResult, User user, LocalDate date
    );

    boolean existsByKeyResultAndUserAndDate(
            KeyResult keyResult, User user, LocalDate date
    );

    void deleteByKeyResultAndUserAndDate(
            KeyResult keyResult, User user, LocalDate date
    );

    boolean existsByKeyResultIdAndUserIdAndDate(Long keyResultId, Long userId, LocalDate date);


    @Query("""
           select count(dc)
           from DailyCheck dc
           where dc.keyResult.id = :krId
             and dc.user.id = :userId
             and dc.date between :start and :end
           """)
    long countCheckedInRangeByKrAndUser(@Param("krId") Long krId,
                                        @Param("userId") Long userId,
                                        @Param("start") LocalDate start,
                                        @Param("end") LocalDate end);

    @Query("""
           select dc.date
           from DailyCheck dc
           where dc.keyResult.id = :krId
             and dc.user.id = :userId
             and dc.date between :start and :end
           order by dc.date asc
           """)
    List<LocalDate> findCheckedDatesInRangeByKrAndUser(@Param("krId") Long krId,
                                                       @Param("userId") Long userId,
                                                       @Param("start") LocalDate start,
                                                       @Param("end") LocalDate end);

    @Query("""
           select count(dc)
           from DailyCheck dc
           where dc.keyResult.objective.id = :objectiveId
             and dc.user.id = :userId
             and dc.date between :start and :end
           """)
    long countCheckedInRangeByObjectiveAndUser(@Param("objectiveId") Long objectiveId,
                                               @Param("userId") Long userId,
                                               @Param("start") LocalDate start,
                                               @Param("end") LocalDate end);


}
