package com.togedy.togedy_server_v2.domain.planner.entity;

import com.togedy.togedy_server_v2.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "daily_study_summary")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyStudySummary extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "daily_study_summary_id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "study_time", nullable = false)
    private Long studyTime;

    @Builder
    public DailyStudySummary(Long userId, Long studyTime) {
        this.userId = userId;
        this.studyTime = studyTime;
    }

}
