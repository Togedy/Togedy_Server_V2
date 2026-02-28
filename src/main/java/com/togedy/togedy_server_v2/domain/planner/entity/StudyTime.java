package com.togedy.togedy_server_v2.domain.planner.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "study_time")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_time_id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "study_subject_id", nullable = false)
    private Long studySubjectId;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = true)
    private LocalDateTime endTime;

    @Builder
    public StudyTime(Long userId, Long studySubjectId, LocalDateTime startTime, LocalDateTime endTime) {
        this.userId = userId;
        this.studySubjectId = studySubjectId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public void stop(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}
