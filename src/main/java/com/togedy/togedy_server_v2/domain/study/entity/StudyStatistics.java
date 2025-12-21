package com.togedy.togedy_server_v2.domain.study.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
    private long score;

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

    public void applyChallengeSuccess(Study study, int completedMemberCount) {
        if (study.isChallengeSuccessful(completedMemberCount)) {
            this.streakDays++;
            this.score += calculateStudyScore(study, completedMemberCount);
        }
    }

    private long calculateStudyScore(Study study, int completeMemberCount) {
        BigDecimal timeScore = study.challengeTimeScore();
        BigDecimal memberScore = calculateMemberScore(completeMemberCount);
        BigDecimal streakScore = calculateStreakDaysScore();

        BigDecimal finalScore = timeScore
                .add(memberScore.multiply(streakScore))
                .setScale(8, RoundingMode.HALF_UP);

        return finalScore
                .multiply(BigDecimal.TEN.pow(8))
                .longValueExact();
    }

    private BigDecimal calculateMemberScore(int completeMemberCount) {
        return BigDecimal.valueOf(2)
                .multiply(
                        BigDecimal.valueOf(
                                Math.log(completeMemberCount + 1) / Math.log(2)
                        )
                );
    }

    private BigDecimal calculateStreakDaysScore() {
        return BigDecimal.valueOf(5)
                .divide(BigDecimal.valueOf(3), 20, RoundingMode.HALF_UP)
                .multiply(
                        BigDecimal.valueOf(
                                Math.log(this.streakDays + 1) / Math.log(2)
                        )
                );
    }
}
