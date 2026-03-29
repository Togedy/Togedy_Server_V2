package com.togedy.togedy_server_v2.domain.study.dao;

import com.togedy.togedy_server_v2.domain.study.entity.StudyStatistics;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StudyStatisticsRepository extends JpaRepository<StudyStatistics, Long> {

    Optional<StudyStatistics> findByStudyId(Long studyId);

    @Query("""
            SELECT ss
            FROM StudyStatistics ss
            WHERE ss.updatedDate = :today
            """)
    List<StudyStatistics> findUpdatedToday(LocalDate today);
}
