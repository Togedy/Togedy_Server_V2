package com.togedy.togedy_server_v2.domain.university.dao;

import com.togedy.togedy_server_v2.domain.university.entity.UserUniversityMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserUniversityMethodRepository extends JpaRepository<UserUniversityMethod, Long> {

    @Query("""
        SELECT uum
        FROM UserUniversityMethod uum
            JOIN FETCH uum.universityAdmissionMethod uam
            JOIN FETCH uam.universityAdmissionScheduleList uasl
            JOIN FETCH uasl.universitySchedule us
            JOIN FETCH uam.university u
        WHERE uum.user.id = :userId
            AND us.startDate <= :endOfMonth
            AND COALESCE(us.endDate, us.startDate) >= :startOfMonth 
    """)
    List<UserUniversityMethod> findByUserIdAndYearAndMonth(
            @Param("userId") Long userId,
            @Param("startOfMonth") LocalDate startOfMonth,
            @Param("endOfMonth") LocalDate endOfMonth
    );

    @Query("""
        SELECT uum
        FROM UserUniversityMethod uum
            JOIN FETCH uum.universityAdmissionMethod uam
            JOIN FETCH uam.universityAdmissionScheduleList uasl
            JOIN FETCH uasl.universitySchedule us
            JOIN FETCH uam.university u
        WHERE uum.user.id = :userId
            AND :date BETWEEN us.startDate AND COALESCE(us.endDate, us.startDate)
    """)
    List<UserUniversityMethod> findByUserIdAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    Optional<UserUniversityMethod> findByUniversityAdmissionMethodIdAndUserId(Long universityAdmissionMethodId, Long userId);

    boolean existsByUniversityAdmissionMethodIdAndUserId(Long universityAdmissionMethodId, Long userId);
}
