package com.togedy.togedy_server_v2.domain.planner.entity;

import com.togedy.togedy_server_v2.domain.planner.enums.StudyTaskStatus;
import com.togedy.togedy_server_v2.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
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

    @Column(name = "study_subject_id", nullable = false)
    private Long studySubjectId;

    @Column(name = "name", nullable = false, updatable = true)
    private String name;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "varchar(20)")
    private StudyTaskStatus status;

    public boolean isCompleted() {
        return this.status == StudyTaskStatus.SUCCESS;
    }
}
