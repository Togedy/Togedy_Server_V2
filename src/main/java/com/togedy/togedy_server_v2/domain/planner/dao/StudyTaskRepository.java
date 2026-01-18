package com.togedy.togedy_server_v2.domain.planner.dao;

import com.togedy.togedy_server_v2.domain.planner.entity.StudyTask;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StudyTaskRepository extends JpaRepository<StudyTask, Long> {
    @Query("""
                SELECT p
                FROM StudyTask p
                WHERE p.studySubjectId IN :studySubjectIds
                    AND p.createdAt BETWEEN :startOfDay AND :endOfDay
            """)
    List<StudyTask> findAllByStudySubjectIdsAndPeriod(
            List<Long> studySubjectIds,
            LocalDateTime startOfDay,
            LocalDateTime endOfDay
    );
}
