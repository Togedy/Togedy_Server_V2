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
    @Column(name = "study_id")
    private Long id;

    @Column(name = "type")
    private String type;

    @Column(name = "goal_time")
    private LocalTime goalTime;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "personnel")
    private int personnel;

    @Column(name = "tag")
    private String tag;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "password")
    private int password;

    @Column(name = "invitation_code")
    private String invitationCode;

    @Column(name = "tier")
    private String tier;

    @Column(name = "status")
    private String status;

    @Builder
    public Study(
            String type,
            LocalTime goalTime,
            String name,
            String description,
            int personnel,
            String tag,
            String imageUrl,
            int password,
            String invitationCode,
            String tier
    ) {
        this.type = type;
        this.goalTime = goalTime;
        this.name = name;
        this.description = description;
        this.personnel = personnel;
        this.tag = tag;
        this.imageUrl = imageUrl;
        this.password = password;
        this.invitationCode = invitationCode;
        this.tier = tier;
        this.status = "ACTIVE";
    }
}
