package com.togedy.togedy_server_v2.domain.university.dao;

import com.togedy.togedy_server_v2.domain.university.entity.UniversitySchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UniversityScheduleRepository extends JpaRepository<UniversitySchedule, Long> {

    @Query("""
        SELECT us
        FROM UniversitySchedule us
        JOIN FETCH us.admissionMethod am
        JOIN FETCH am.university u
        WHERE u.name LIKE CONCAT('%', :namePart, '%')
        AND us.academicYear = :year
        ORDER BY u.id, u.admissionType, am.name, us.startDate
    """)
    List<UniversitySchedule> findByUniversityNameLikeAndYear(
            @Param("namePart") String namePart,
            @Param("year")      int    year
    );

    Optional<UniversitySchedule> findById(Long universityScheduleId);

    List<UniversitySchedule> findAllById(List<Long> universityScheduleIdList);
}
