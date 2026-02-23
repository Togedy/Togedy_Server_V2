package com.togedy.togedy_server_v2.domain.planner.dao;

import com.togedy.togedy_server_v2.domain.planner.entity.StudyTask;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StudyTaskRepository extends JpaRepository<StudyTask, Long> {
    @Query("""
                SELECT st
                FROM StudyTask st
                WHERE st.studySubjectId IN :studySubjectIds
                    AND st.date = :date
            """)
    List<StudyTask> findAllByStudySubjectIdsAndDate(
            List<Long> studySubjectIds,
            LocalDate date
    );
}
