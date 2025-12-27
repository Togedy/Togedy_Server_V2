package com.togedy.togedy_server_v2.domain.study.entity;

import com.togedy.togedy_server_v2.domain.study.enums.StudyRole;
import com.togedy.togedy_server_v2.domain.study.exception.StudyLeaderRequiredException;
import com.togedy.togedy_server_v2.domain.study.exception.StudyMemberRequiredException;
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
import java.time.temporal.ChronoUnit;
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

    public void validateStudyLeader() {
        if (!this.role.equals(StudyRole.LEADER)) {
            throw new StudyLeaderRequiredException();
        }
    }

    public void validateStudyMember() {
        if (!this.role.equals(StudyRole.MEMBER)) {
            throw new StudyMemberRequiredException();
        }
    }

    public void delegateLeader(UserStudy member) {
        this.validateStudyLeader();
        this.role = StudyRole.MEMBER;
        member.role = StudyRole.LEADER;
    }

    public int calculateElapsedDays() {
        LocalDate now = LocalDate.now();
        LocalDate createdDate = this.getCreatedAt().toLocalDate();

        return (int) ChronoUnit.DAYS.between(createdDate, now);
    }
}
