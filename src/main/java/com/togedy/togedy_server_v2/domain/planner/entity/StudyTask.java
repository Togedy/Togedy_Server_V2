package com.togedy.togedy_server_v2.domain.planner.entity;

import com.togedy.togedy_server_v2.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "study_task")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyTask extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id", nullable = false, updatable = false)
    private Long id;

    @Column(nullable = false, updatable = false)
    private Long userId;

    @Column(name = "study_subject_id", nullable = false)
    private Long studySubjectId;

    @Column(name = "name", nullable = false, updatable = true)
    private String name;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "is_checked", nullable = false, updatable = true)
    private boolean isChecked;

    @Builder
    public StudyTask(Long userId, Long studySubjectId, String name, LocalDate date) {
        this.userId = userId;
        this.studySubjectId = studySubjectId;
        this.name = name;
        this.date = date;
        this.isChecked = false;
    }

    public void update(String name) {
        this.name = name;
    }

    public void toggleChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }
}
