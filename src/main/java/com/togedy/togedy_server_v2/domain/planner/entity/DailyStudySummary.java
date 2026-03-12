package com.togedy.togedy_server_v2.domain.planner.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(
        name = "daily_study_summary",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "date"})
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyStudySummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "daily_study_summary_id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "study_time", nullable = false)
    private Long studyTime;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Builder
    public DailyStudySummary(Long userId, Long studyTime, LocalDate date) {
        this.userId = userId;
        this.studyTime = studyTime;
        this.date = date;
    }

    public void addStudyTime(long additionalStudyTime) {
        if (additionalStudyTime < 0) {
            throw new IllegalArgumentException("additionalStudyTime must be >= 0");
        }
        if (this.studyTime == null) {
            this.studyTime = 0L;
        }
        this.studyTime += additionalStudyTime;
    }

}
