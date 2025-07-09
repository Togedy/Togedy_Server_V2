package com.togedy.togedy_server_v2.domain.university.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

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

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "academic_year", columnDefinition = "YEAR", nullable = false, updatable = false)
    private int academicYear;

    @OneToMany(mappedBy = "universitySchedule", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<UniversityAdmissionSchedule> universityAdmissionScheduleList = new ArrayList<>();

    @OneToMany(mappedBy = "universitySchedule", fetch = FetchType.LAZY)
    private List<UserUniversitySchedule> userUniversityScheduleList = new ArrayList<>();

}
