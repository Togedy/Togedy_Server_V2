package com.togedy.togedy_server_v2.domain.planner.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(
        name = "study_time",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "is_running"})
)
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

    @Column(name = "is_running", nullable = true)
    private Boolean isRunning;

    @Builder
    public StudyTime(
            Long userId,
            Long studySubjectId,
            LocalDateTime startTime,
            LocalDateTime endTime,
            Boolean isRunning
    ) {
        this.userId = userId;
        this.studySubjectId = studySubjectId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isRunning = isRunning;
    }

    public void stop(LocalDateTime endTime) {
        if (endTime == null) {
            throw new IllegalArgumentException("endTime must not be null");
        }
        if (this.endTime != null) {
            throw new IllegalStateException("Timer is already stopped");
        }
        if (this.startTime == null || endTime.isBefore(this.startTime)) {
            throw new IllegalArgumentException("endTime must be after startTime");
        }
        this.endTime = endTime;
        this.isRunning = null;
    }
}
