package com.togedy.togedy_server_v2.domain.university.dao;

import com.togedy.togedy_server_v2.domain.university.entity.University;
import com.togedy.togedy_server_v2.domain.university.entity.UniversityAdmissionMethod;
import com.togedy.togedy_server_v2.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UniversityAdmissionMethodRepository extends JpaRepository<UniversityAdmissionMethod, Long> {

    int countByUniversity(University university);

    @Query("""
        SELECT uam
        FROM UniversityAdmissionMethod uam
            JOIN FETCH uam.userUniversityMethodList uum
        WHERE uum.user = :user
            AND uam.university = :university
    """)
    List<UniversityAdmissionMethod> findAllByUniversityAndUser(University university, User user);

    @Query("""
        SELECT uam
        FROM UniversityAdmissionMethod uam
            JOIN FETCH uam.universityAdmissionScheduleList uasl
            JOIN FETCH uasl.universitySchedule us
        WHERE uam.university = :university
    """)
    List<UniversityAdmissionMethod> findAllByUniversity(University university);
}
