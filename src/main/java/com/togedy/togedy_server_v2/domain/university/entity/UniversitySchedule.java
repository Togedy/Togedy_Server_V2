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
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import java.time.LocalDateTime;

@Entity
@Table(name = "university_schedule")
@Immutable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UniversitySchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "university_schedule_id")
    private Long id;

    @Column(name = "admission_stage", nullable = false)
    private String admissionStage;

    @Column(name = "start_date", columnDefinition = "DATETIME(0)", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", columnDefinition = "DATETIME(0)", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "academic_year", columnDefinition = "YEAR", nullable = false, updatable = false)
    private int academicYear;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admission_method_id", nullable = false, updatable = false)
    private AdmissionMethod admissionMethod;

}
