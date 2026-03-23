package com.togedy.togedy_server_v2.domain.planner.dao;

import com.togedy.togedy_server_v2.domain.planner.entity.DailyStudySummary;
import com.togedy.togedy_server_v2.domain.study.dto.DailyStudySummaryRow;
import com.togedy.togedy_server_v2.domain.study.dto.DailyStudyTimeDto;
import jakarta.persistence.LockModeType;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DailyStudySummaryRepository extends JpaRepository<DailyStudySummary, Long> {

    @Query("""
            SELECT dss
            FROM DailyStudySummary dss
            WHERE dss.userId IN :userIds
                AND dss.date = :date
            """)
    List<DailyStudySummary> findAllByUserIdsAndDate(
            @Param("userIds") List<Long> userIds,
            @Param("date") LocalDate date
    );

    @Query("""
            SELECT dss
            FROM DailyStudySummary dss
            WHERE dss.userId = :userId
                AND dss.date = :date
            """)
    Optional<DailyStudySummary> findByUserIdAndDate(
            @Param("userId") Long userId,
            @Param("date") LocalDate date
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT dss
            FROM DailyStudySummary dss
            WHERE dss.userId = :userId
                AND dss.date = :date
            """)
    Optional<DailyStudySummary> findByUserIdAndDateForUpdate(
            @Param("userId") Long userId,
            @Param("date") LocalDate date
    );

    @Query("""
            SELECT sum(dss.studyTime)
            FROM DailyStudySummary dss
            WHERE dss.userId = :userId
            """)
    Optional<Long> findTotalStudyTimeByUserId(Long userId);

    @Query("""
            SELECT dss
            FROM DailyStudySummary dss
            WHERE dss.userId = :userId
                AND dss.date >= :startDate
                AND dss.date <= :endDate
            """)
    List<DailyStudySummary> findAllByUserIdAndPeriod(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("""
            SELECT dss.date
            FROM DailyStudySummary dss
            WHERE dss.userId = :userId
                AND dss.date <= :date
                AND dss.studyTime > 0
            ORDER BY dss.date DESC
            """)
    List<LocalDate> findStudyDatesByUserIdUntilDateOrderByDateDesc(
            @Param("userId") Long userId,
            @Param("date") LocalDate date
    );

    @Query("""
            SELECT new com.togedy.togedy_server_v2.domain.study.dto.DailyStudyTimeDto(
                d.userId as userId,
                d.date as date,
                SUM(d.studyTime) as studyTime
            )
            FROM DailyStudySummary d
            WHERE d.userId IN :userIds
                AND d.date >= :startDate
                AND d.date <= :endDate
            GROUP BY d.userId, d.date
            """)
    List<DailyStudyTimeDto> findDailyStudyTimeByUserIdsAndPeriod(
            @Param("userIds") List<Long> userIds,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("""
            SELECT new com.togedy.togedy_server_v2.domain.study.dto.DailyStudySummaryRow(
                us.studyId,
                ds.userId,
                ds.studyTime
            )
            FROM UserStudy us
            JOIN DailyStudySummary ds ON us.userId = ds.userId
            WHERE us.studyId IN :studyIds
                AND DATE(ds.date) = :targetDate
            """)
    List<DailyStudySummaryRow> findAllByStudyIdsAndDate(List<Long> studyIds, LocalDate targetDate);

    void deleteAllByDate(LocalDate targetDate);
}
