package com.togedy.togedy_server_v2.domain.university.dao;

import com.togedy.togedy_server_v2.domain.university.entity.UniversityAdmissionSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AdmissionScheduleRepository extends JpaRepository<UniversityAdmissionSchedule, Long> {

    @Query("""
      SELECT DISTINCT asch
        FROM UniversityAdmissionSchedule asch
          JOIN FETCH asch.universityAdmissionMethod uam
          JOIN FETCH uam.university u
          JOIN FETCH asch.universitySchedule us
       WHERE u.id = :universityId
         AND us.academicYear = :year
    """)
    List<UniversityAdmissionSchedule> findByUniversityAndYear(
            @Param("universityId") Long universityId,
            @Param("year")         int  academicYear
    );
}
