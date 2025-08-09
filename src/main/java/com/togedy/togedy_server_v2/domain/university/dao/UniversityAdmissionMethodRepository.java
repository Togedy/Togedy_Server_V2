package com.togedy.togedy_server_v2.domain.university.dao;

import com.togedy.togedy_server_v2.domain.university.entity.University;
import com.togedy.togedy_server_v2.domain.university.entity.UniversityAdmissionMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UniversityAdmissionMethodRepository extends JpaRepository<UniversityAdmissionMethod, Long> {

    @Query("""
        SELECT uam
        FROM UniversityAdmissionMethod uam
            JOIN FETCH uam.userUniversityMethodList uum
        WHERE uum.user.id = :userId
            AND uam.university = :university
            AND uam.academicYear = :academicYear
    """)
    List<UniversityAdmissionMethod> findAllByUniversityAndUserIdAndAcademicYear(
            @Param("university") University university,
            @Param("userId") Long userId,
            @Param("academicYear") int academicYear
    );

    @Query("""
        SELECT uam
        FROM UniversityAdmissionMethod uam
            JOIN FETCH uam.userUniversityMethodList uum
        WHERE uum.user.id = :userId
            AND uam.university.id IN :universityIds
            AND uam.academicYear = :academicYear
    """)
    List<UniversityAdmissionMethod> findAllByUniversityIdsAndUserIdAndAcademicYear(
            @Param("universityIds") List<Long> universityIds,
            @Param("userId") Long userId,
            @Param("academicYear") int academicYear
    );

    @Query("""
        SELECT uam
        FROM UniversityAdmissionMethod uam
            JOIN FETCH uam.universityAdmissionScheduleList uasl
            JOIN FETCH uasl.universitySchedule us
        WHERE uam.university = :university
            AND uam.academicYear = :academicYear
    """)
    List<UniversityAdmissionMethod> findAllByUniversityAndAcademicYear(
            @Param("university") University university,
            @Param("academicYear") int academicYear
    );


    @Query("""
        SELECT u.id, COUNT(uam)
        FROM UniversityAdmissionMethod uam
            JOIN uam.university u
        WHERE u.id IN :ids
            AND uam.academicYear = :academicYear
        GROUP BY u.id
    """)
    List<Object[]> findCountByUniversityIdsAnAndAcademicYear(
            @Param("ids") List<Long> universityIdList,
            @Param("academicYear") int academicYear
    );
}
