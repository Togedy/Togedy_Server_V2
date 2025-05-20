package com.togedy.togedy_server_v2.domain.university.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "admission_method",
        uniqueConstraints = @UniqueConstraint(
        columnNames = {"university_id",
                "admission_method_name"}
        )
)
@Immutable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdmissionMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admission_method_id")
    private Long id;

    @Column(name = "admission_method_name", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "university_id", nullable = false, updatable = false)
    private University university;

    @OneToMany(mappedBy = "admissionMethod", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<AdmissionSchedule> admissionScheduleList = new ArrayList<>();
}
