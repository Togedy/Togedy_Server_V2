package com.togedy.togedy_server_v2.domain.study.entity;

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

import java.time.LocalTime;

@Entity
@Table(name = "study")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Study extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_id", nullable = false)
    private Long id;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "goal_time", nullable = true)
    private LocalTime goalTime;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = true)
    private String description;

    @Column(name = "participant", nullable = false)
    private int participant;

    @Column(name = "tag", nullable = false)
    private String tag;

    @Column(name = "image_url", nullable = true)
    private String imageUrl;

    @Column(name = "password", nullable = true)
    private String password;

    @Column(name = "invitation_code", nullable = false)
    private String invitationCode;

    @Column(name = "tier", nullable = false)
    private String tier;

    @Column(name = "status", nullable = false)
    private String status;

    @Builder
    public Study(
            String type,
            LocalTime goalTime,
            String name,
            String description,
            int participant,
            String tag,
            String imageUrl,
            String password,
            String invitationCode,
            String tier
    ) {
        this.type = type;
        this.goalTime = goalTime;
        this.name = name;
        this.description = description;
        this.participant = participant;
        this.tag = tag;
        this.imageUrl = imageUrl;
        this.password = password;
        this.invitationCode = invitationCode;
        this.tier = tier;
        this.status = "ACTIVE";
    }
}
