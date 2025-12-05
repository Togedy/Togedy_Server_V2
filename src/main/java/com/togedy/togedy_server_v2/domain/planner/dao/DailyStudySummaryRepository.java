package com.togedy.togedy_server_v2.domain.planner.dao;

import com.togedy.togedy_server_v2.domain.planner.entity.DailyStudySummary;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DailyStudySummaryRepository extends JpaRepository<DailyStudySummary, Long> {

    @Query("""
            SELECT dss
            FROM DailyStudySummary dss
            WHERE dss.userId IN :userIds
                AND dss.createdAt >= :startOfDay
                AND dss.createdAt < :endOfDay
            """)
    List<DailyStudySummary> findAllByUserIdsAndCreatedAt(
            @Param("userIds") List<Long> userIds,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
    );

    @Query("""
            SELECT dss
            FROM DailyStudySummary dss
            WHERE dss.userId = :userId
                AND dss.createdAt >= :startOfDay
                AND dss.createdAt < :endOfDay
            """)
    Optional<DailyStudySummary> findByUserIdAndCreatedAt(
            @Param("userId") Long userId,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
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
                AND dss.createdAt BETWEEN :startDateTime AND :endDateTime
            """)
    List<DailyStudySummary> findAllByUserIdAndPeriod(
            @Param("userId") Long userId,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );

    @Query("""
            SELECT dss
            FROM DailyStudySummary dss
            WHERE dss.userId IN :userIds
                AND dss.createdAt BETWEEN :startDateTime AND :endDateTime
            """)
    List<DailyStudySummary> findAllByUserIdsAndPeriod(
            @Param("userIds") List<Long> userIds,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );

    @Query("""
                SELECT dss.userId, SUM(dss.studyTime)
                FROM DailyStudySummary dss
                WHERE dss.userId IN :userIds
                    AND dss.createdAt BETWEEN :startDateTime AND :endDateTime
                GROUP BY dss.userId
            """)
    List<Object[]> findTotalStudyTimeByUserIdsAndPeriod(
            @Param("userIds") List<Long> userIds,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );

    List<DailyStudySummary> findAllByUserIdIn(Collection<Long> userIds);
}
