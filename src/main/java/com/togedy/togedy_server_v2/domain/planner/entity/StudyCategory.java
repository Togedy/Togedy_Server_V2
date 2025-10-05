package com.togedy.togedy_server_v2.domain.planner.entity;

import com.togedy.togedy_server_v2.global.enums.BaseStatus;
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
@Table(name = "study_category")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_category_id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "user_id", nullable = false, updatable = false)
    private Long userId;

    @Column(name = "name", nullable = false, updatable = true)
    private String name;

    @Column(name = "color", nullable = false, updatable = true)
    private String color;

    @Column(name = "status", nullable = false, updatable = true)
    private String status;

    @Builder
    public StudyCategory(Long userId, String name, String color) {
        this.userId = userId;
        this.name = name;
        this.color = color;
        this.status = BaseStatus.ACTIVE.name();
    }
}
