package com.togedy.togedy_server_v2.domain.study.dao;

import com.togedy.togedy_server_v2.domain.study.entity.StudyReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyReportRepository extends JpaRepository<StudyReport, Long> {
}
