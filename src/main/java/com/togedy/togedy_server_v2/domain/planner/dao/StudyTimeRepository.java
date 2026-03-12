package com.togedy.togedy_server_v2.domain.planner.dao;

import com.togedy.togedy_server_v2.domain.planner.entity.StudyTime;
import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StudyTimeRepository extends JpaRepository<StudyTime, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT st
            FROM StudyTime st
            WHERE st.id = :id
            """)
    Optional<StudyTime> findByIdForUpdate(Long id);

    @Query("""
            SELECT st
            FROM StudyTime st
            WHERE st.studySubjectId IN :studySubjectIds
                AND st.startTime < :endTime
                AND st.endTime > :startTime
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
                AND st.startTime < :endTime
                AND st.endTime > :startTime
                AND st.endTime IS NOT NULL
            """)
    List<StudyTime> findDailyStudyTimesByUserId(
            Long userId,
            LocalDateTime startTime,
            LocalDateTime endTime
    );

    @Query(value = """
            SELECT st.study_subject_id,
                   COALESCE(SUM(TIMESTAMPDIFF(SECOND,
                       GREATEST(st.start_time, :startTime),
                       LEAST(st.end_time, :endTime)
                   )), 0)
            FROM study_time st
            WHERE st.user_id = :userId
              AND st.start_time < :endTime
              AND st.end_time > :startTime
              AND st.end_time IS NOT NULL
            GROUP BY st.study_subject_id
            """, nativeQuery = true)
    List<Object[]> findDailyStudyTimeBySubject(
            Long userId,
            LocalDateTime startTime,
            LocalDateTime endTime
    );

    @Query(value = """
            SELECT COALESCE(SUM(TIMESTAMPDIFF(SECOND,
                GREATEST(st.start_time, :startTime),
                LEAST(st.end_time, :endTime)
            )), 0)
            FROM study_time st
            WHERE st.user_id = :userId
              AND st.start_time < :endTime
              AND st.end_time > :startTime
              AND st.end_time IS NOT NULL
            """, nativeQuery = true)
    Long sumDailyStudyTimeByUserId(
            Long userId,
            LocalDateTime startTime,
            LocalDateTime endTime
    );
}
