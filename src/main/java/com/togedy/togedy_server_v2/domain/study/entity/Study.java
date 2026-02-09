package com.togedy.togedy_server_v2.domain.study.entity;

import com.togedy.togedy_server_v2.domain.planner.entity.DailyStudySummary;
import com.togedy.togedy_server_v2.domain.study.enums.StudyTag;
import com.togedy.togedy_server_v2.domain.study.enums.StudyType;
import com.togedy.togedy_server_v2.domain.study.exception.InvalidStudyMemberLimitException;
import com.togedy.togedy_server_v2.domain.study.exception.StudyDescriptionContainsBadWordException;
import com.togedy.togedy_server_v2.domain.study.exception.StudyMemberCountExceededException;
import com.togedy.togedy_server_v2.domain.study.exception.StudyMemberLimitOutOfRangeException;
import com.togedy.togedy_server_v2.domain.study.exception.StudyMinimumMemberRequiredException;
import com.togedy.togedy_server_v2.domain.study.exception.StudyNameContainsBadWordException;
import com.togedy.togedy_server_v2.domain.study.exception.StudyPasswordMismatchException;
import com.togedy.togedy_server_v2.domain.study.exception.StudyPasswordRequiredException;
import com.togedy.togedy_server_v2.global.entity.BaseEntity;
import com.togedy.togedy_server_v2.global.enums.BadWords;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "study")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Study extends BaseEntity {

    private static final int MAX_MEMBER_LIMIT = 30;
    private static final int MIN_MEMBER_LIMIT = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_id", nullable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, updatable = false, columnDefinition = "varchar(20)")
    private StudyType type;

    @Column(name = "goal_time", nullable = true)
    private Long goalTime;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = true)
    private String description;

    @Column(name = "member_count", nullable = false)
    private int memberCount;

    @Column(name = "member_limit", nullable = false)
    private int memberLimit;

    @Enumerated(EnumType.STRING)
    @Column(name = "tag", nullable = false, columnDefinition = "varchar(20)")
    private StudyTag tag;

    @Column(name = "image_url", nullable = true)
    private String imageUrl;

    @Column(name = "password", nullable = true)
    private String password;

    @Column(name = "tier", nullable = true)
    private String tier;

    @Builder
    public Study(
            StudyType type,
            Long goalTime,
            String name,
            String description,
            int memberLimit,
            StudyTag tag,
            String imageUrl,
            String password,
            String tier
    ) {
        validateStudyName(name);
        validateStudyDescription(description);
        validateMemberLimitRange(memberLimit);
        this.type = type;
        this.goalTime = goalTime;
        this.name = name;
        this.description = description;
        this.memberCount = 1;
        this.memberLimit = memberLimit;
        this.tag = tag;
        this.imageUrl = imageUrl;
        this.password = password;
        this.tier = tier;
    }

    public void updateInformation(
            String studyName,
            String studyDescription,
            String studyTag,
            String studyPassword,
            String studyImageUrl
    ) {
        if (studyName != null) {
            validateStudyName(studyName);
            this.name = studyName;
        }
        if (studyDescription != null) {
            validateStudyDescription(studyDescription);
            this.description = studyDescription;
        }
        if (studyTag != null) {
            this.tag = StudyTag.fromDescription(studyTag);
        }
        this.password = studyPassword;
        this.imageUrl = studyImageUrl;
    }

    public void updateMemberLimit(int memberLimit) {
        validateMemberLimitRange(memberLimit);
        validateUpdatableMemberLimit(memberLimit);
        this.memberLimit = memberLimit;
    }

    public void increaseMemberCount() {
        validateAddMember();
        this.memberCount++;
    }

    public void decreaseMemberCount() {
        validateRemoveMember();
        this.memberCount--;
    }

    public void validatePassword(String password) {
        validatePasswordRequired(password);
        validatePasswordMatches(password);
    }

    public boolean isChallengeStudy() {
        return this.type.equals(StudyType.CHALLENGE);
    }

    public String changeImageUrl(String imageUrl) {
        String oldImageUrl = this.imageUrl;
        this.imageUrl = imageUrl;
        return oldImageUrl;
    }

    public boolean isNewlyCreated() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime current = now.minusDays(7);

        return this.getCreatedAt().isAfter(current) && this.getCreatedAt().isBefore(now);
    }

    public boolean hasPassword() {
        return this.password != null;
    }

    public boolean isAchieved(DailyStudySummary dailyStudySummary) {
        return dailyStudySummary.getStudyTime() >= this.goalTime;
    }

    private void validateMemberLimitRange(int memberLimit) {
        if (MAX_MEMBER_LIMIT < memberLimit || memberLimit < MIN_MEMBER_LIMIT) {
            throw new StudyMemberLimitOutOfRangeException();
        }
    }

    private void validateUpdatableMemberLimit(int memberLimit) {
        if (memberLimit < this.memberCount) {
            throw new InvalidStudyMemberLimitException();
        }
    }

    private void validateAddMember() {
        if (this.memberLimit == this.memberCount) {
            throw new StudyMemberCountExceededException();
        }
    }

    private void validatePasswordRequired(String password) {
        if (this.password != null && password == null) {
            throw new StudyPasswordRequiredException();
        }
    }

    private void validatePasswordMatches(String password) {
        if (this.password != null && !password.equals(this.password)) {
            throw new StudyPasswordMismatchException();
        }
    }

    private void validateRemoveMember() {
        if (this.memberCount <= 1) {
            throw new StudyMinimumMemberRequiredException();
        }
    }

    private void validateStudyName(String name) {
        if (BadWords.containsBadWord(name)) {
            throw new StudyNameContainsBadWordException();
        }
    }

    private void validateStudyDescription(String description) {
        if (BadWords.containsBadWord(description)) {
            throw new StudyDescriptionContainsBadWordException();
        }
    }

}
