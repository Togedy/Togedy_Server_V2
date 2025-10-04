package com.togedy.togedy_server_v2.domain.study.entity;

import com.togedy.togedy_server_v2.domain.study.dto.PatchStudyInfoRequest;
import com.togedy.togedy_server_v2.domain.study.dto.PatchStudyMemberLimitRequest;
import com.togedy.togedy_server_v2.domain.study.enums.StudyTag;
import com.togedy.togedy_server_v2.domain.study.enums.StudyType;
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
@Table(name = "study")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Study extends BaseEntity {

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

    @Column(name = "tier", nullable = false)
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

    public void updateInfo(PatchStudyInfoRequest request, String studyImageUrl) {
        if (request.getStudyName() != null) {
            this.name = request.getStudyName();
        }
        if (request.getStudyDescription() != null) {
            this.description = request.getStudyDescription();
        }
        if (request.getStudyTag() != null) {
            this.tag = StudyTag.fromDescription(request.getStudyTag());
        }
        if (request.getStudyPassword() != null) {
            this.password = request.getStudyPassword();
        }
        if (studyImageUrl != null) {
            this.imageUrl = studyImageUrl;
        }
    }

    public void updateMemberLimit(PatchStudyMemberLimitRequest request) {
        this.memberLimit = request.getStudyMemberLimit();
    }

    public void increaseMemberCount() {
        this.memberCount++;
    }

    public void decreaseMemberCount() {
        this.memberCount--;
    }
}
