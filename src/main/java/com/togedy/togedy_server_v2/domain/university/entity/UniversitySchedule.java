package com.togedy.togedy_server_v2.domain.university.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "university_schedule")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UniversitySchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "university_schedule_id")
    private Long id;

    @Column(name = "admission_stage")
    private String admissionStage;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admission_method_id")
    private AdmissionMethod admissionMethod;

    @Builder
    private UniversitySchedule(String admissionStage,
                               LocalDateTime startDate,
                               LocalDateTime endDate) {
        this.admissionStage = admissionStage;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
