package com.togedy.togedy_server_v2.domain.university.dao;

import com.togedy.togedy_server_v2.domain.university.entity.UserUniversityMethod;
import com.togedy.togedy_server_v2.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface UserUniversityMethodRepository extends JpaRepository<UserUniversityMethod, Long> {

    List<UserUniversityMethod> findByUserAndUniversityAdmissionMethodIdIn(User user, List<Long> id);

    @Query("""
        SELECT uum
        FROM UserUniversityMethod uum
        JOIN FETCH uum.universityAdmissionMethod uam
        JOIN FETCH uam.universityAdmissionScheduleList uasl
        JOIN FETCH uasl.universitySchedule us
        WHERE uum.user.id = :userId
        AND YEAR(us.startDate) = :year
        AND MONTH(us.startDate) = :month
    """)
    List<UserUniversityMethod> findByUserIdAndYearAndMonth(
            @Param("userId") Long userId,
            @Param("year") int year,
            @Param("month") int month
    );

    @Query("""
        SELECT uum
        FROM UserUniversityMethod uum
        JOIN FETCH uum.universityAdmissionMethod uam
        JOIN FETCH uam.universityAdmissionScheduleList uasl
        JOIN FETCH uasl.universitySchedule us
        WHERE uum.id = :userId
        AND :date BETWEEN us.startDate AND us.endDate
    """)
    List<UserUniversityMethod> findByUserIdAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);
}
