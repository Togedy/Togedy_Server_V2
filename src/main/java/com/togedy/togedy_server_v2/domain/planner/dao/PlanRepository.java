package com.togedy.togedy_server_v2.domain.planner.dao;

import com.togedy.togedy_server_v2.domain.planner.entity.Plan;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PlanRepository extends JpaRepository<Plan, Long> {
    @Query("""
                SELECT p
                FROM Plan p
                WHERE p.studySubjectId IN :studySubjectIds
                    AND p.createdAt BETWEEN :startOfDay AND :endOfDay
            """)
    List<Plan> findAllByStudySubjectIdsAndPeriod(
            List<Long> studySubjectIds,
            LocalDateTime startOfDay,
            LocalDateTime endOfDay
    );
}
