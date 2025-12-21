package com.togedy.togedy_server_v2.domain.study.dao;

import com.togedy.togedy_server_v2.domain.study.entity.StudyStatistics;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyStatisticsRepository extends JpaRepository<StudyStatistics, Long> {
    StudyStatistics findByStudyId(Long studyId);
}
