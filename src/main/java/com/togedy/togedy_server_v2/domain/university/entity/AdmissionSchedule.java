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
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "admission_schedule",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"admission_methoid_id", "university_schedule_id"}
        )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdmissionSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admission_schedule_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admission_method_id", nullable = false, updatable = false)
    private AdmissionMethod admissionMethod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "university_schedule_id", nullable = false, updatable = false)
    private UniversitySchedule universitySchedule;
}
