package com.togedy.togedy_server_v2.domain.planner.entity;

import com.togedy.togedy_server_v2.domain.planner.enums.PlanStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@Table(name = "plan")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "study_categody_id", nullable = false)
    private Long studyCategoryId;

    @Column(name = "name", nullable = false, updatable = true)
    private String name;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "status", nullable = false)
    private PlanStatus status;
}
