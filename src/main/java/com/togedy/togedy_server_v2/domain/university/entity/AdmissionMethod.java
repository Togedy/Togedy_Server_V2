package com.togedy.togedy_server_v2.domain.university.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "admission_method")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdmissionMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admission_method_id")
    private Long id;

    @Column(name = "admission_method_name")
    private String name;

    @Builder
    private AdmissionMethod(String name) {
        this.name = name;
    }
}
