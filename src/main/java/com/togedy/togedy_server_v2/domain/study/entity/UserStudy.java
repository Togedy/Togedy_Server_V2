package com.togedy.togedy_server_v2.domain.study.entity;

import com.togedy.togedy_server_v2.domain.study.enums.StudyRole;
import com.togedy.togedy_server_v2.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_study")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserStudy extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_study_id", nullable = false)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "study_id", nullable = false)
    private Long studyId;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, columnDefinition = "varchar(20)")
    private StudyRole role;

    @Builder
    public UserStudy(Long userId, Long studyId, StudyRole role) {
        this.userId = userId;
        this.studyId = studyId;
        this.role = role;
    }

    public void modifyRole(StudyRole studyRole) {
        this.role = studyRole;
    }
}
