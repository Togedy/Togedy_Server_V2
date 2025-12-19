package com.togedy.togedy_server_v2.domain.study.entity;

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
@Table(
        name = "study_statistics",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "study_id")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyStatistics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_statistics_id", nullable = false)
    private Long id;

    @Column(name = "study_id", nullable = false)
    private Long studyId;

    @Column(name = "score", nullable = false)
    private int score;

    @Column(name = "streak_days", nullable = false)
    private int streakDays;

    @Column(name = "last_processed_date", nullable = false)
    private LocalDate lastProcessedDate;

    @Builder
    public StudyStatistics(Long studyId) {
        this.studyId = studyId;
        this.score = 0;
        this.streakDays = 0;
        this.lastProcessedDate = LocalDate.MIN;
    }
}
