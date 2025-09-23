package com.togedy.togedy_server_v2.domain.planner.dao;

import com.togedy.togedy_server_v2.domain.planner.entity.DailyStudySummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

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
}
