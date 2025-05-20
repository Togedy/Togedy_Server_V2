package com.togedy.togedy_server_v2.domain.university.dao;

import com.togedy.togedy_server_v2.domain.university.entity.AdmissionSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AdmissionScheduleRepository extends JpaRepository<AdmissionSchedule, Long> {

    @Query("""
    SELECT DISTINCT asch
    FROM AdmissionSchedule asch
      JOIN FETCH asch.admissionMethod am
      JOIN FETCH am.university u
      JOIN FETCH asch.universitySchedule us
      LEFT JOIN us.userUniversityScheduleList uus
        WITH uus.user.id = :userId
    WHERE u.name LIKE CONCAT('%', :name, '%')
      AND us.academicYear = :year
      AND (:admissionType IS NULL OR u.admissionType = :admissionType)
    """)
    List<AdmissionSchedule> findAllWithUserFlag(
            @Param("userId") Long userId,
            @Param("name") String name,
            @Param("year") int year,
            @Param("admissionType") String admissionType
    );
}
