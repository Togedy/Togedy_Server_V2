package com.togedy.togedy_server_v2.domain.university.dao;

import com.togedy.togedy_server_v2.domain.university.entity.University;
import com.togedy.togedy_server_v2.domain.university.entity.UniversityAdmissionMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UniversityAdmissionMethodRepository extends JpaRepository<UniversityAdmissionMethod, Long> {

    int countByUniversity(University university);

    @Query("""
        SELECT uam
        FROM UniversityAdmissionMethod uam
            JOIN FETCH uam.userUniversityMethodList uum
        WHERE uum.user.id = :userId
            AND uam.university = :university
    """)
    List<UniversityAdmissionMethod> findAllByUniversityAndUserId(University university, Long userId);

    @Query("""
        SELECT uam
        FROM UniversityAdmissionMethod uam
            JOIN FETCH uam.userUniversityMethodList uum
        WHERE uum.user.id = :userId
            AND uam.university.id IN :universityIds
    """)
    List<UniversityAdmissionMethod> findAllByUniversityIdsAndUserId(
            @Param("universityIds") List<Long> universityIds,
            @Param("userId") Long userId
    );

    @Query("""
        SELECT uam
        FROM UniversityAdmissionMethod uam
            JOIN FETCH uam.universityAdmissionScheduleList uasl
            JOIN FETCH uasl.universitySchedule us
        WHERE uam.university = :university
    """)
    List<UniversityAdmissionMethod> findAllByUniversity(University university);


    @Query("""
        SELECT u.id, COUNT(uam)
        FROM UniversityAdmissionMethod uam
            JOIN uam.university u
        WHERE u.id IN :ids
        GROUP BY u.id
    """)
    List<Object[]> findCountByUniversityIds(@Param("ids") List<Long> universityIdList);
}
