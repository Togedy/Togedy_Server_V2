package com.togedy.togedy_server_v2.domain.planner.dao;

import com.togedy.togedy_server_v2.domain.planner.entity.StudyTime;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StudyTimeRepository extends JpaRepository<StudyTime, Long> {
    @Query("""
            SELECT st
            FROM StudyTime st
            WHERE st.studySubjectId IN :studySubjectIds
                AND st.startTime >= :startTime
                AND st.startTime < :endTime
                AND st.endTime IS NOT NULL
            """)
    List<StudyTime> findDailyStudyTimesBySubjectIds(
            List<Long> studySubjectIds,
            LocalDateTime startTime,
            LocalDateTime endTime
    );

    Optional<StudyTime> findByUserIdAndEndTimeIsNull(Long userId);

    @Query("""
            SELECT st
            FROM StudyTime st
            WHERE st.userId = :userId
                AND st.startTime >= :startTime
                AND st.startTime < :endTime
                AND st.endTime IS NOT NULL
            """)
    List<StudyTime> findDailyStudyTimesByUserId(
            Long userId,
            LocalDateTime startTime,
            LocalDateTime endTime
    );
}
