package com.togedy.togedy_server_v2.domain.schedule.dao;

import com.togedy.togedy_server_v2.domain.schedule.entity.UserSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserScheduleRepository extends JpaRepository<UserSchedule, Long> {

    @Query("""
       SELECT us
       FROM UserSchedule us
       WHERE us.user.id = :userId
       AND :year BETWEEN YEAR(us.startDate) AND YEAR(us.endDate)
       AND :month BETWEEN YEAR(us.startDate) AND YEAR(us.endDate)
    """)
    List<UserSchedule> findByUserIdAndYearAndMonth(
            @Param("userId") Long userId,
            @Param("year") int year,
            @Param("month") int month
    );

    @Query("""
        SELECT us
        FROM UserSchedule us
        WHERE us.user.id    = :userId
        AND :date BETWEEN us.startDate AND us.endDate
    """)
    List<UserSchedule> findByUserIdAndDate(
            @Param("userId") Long userId,
            @Param("date") LocalDate date
    );

    @Query("""
        SELECT us
        FROM  UserSchedule us
        WHERE us.user.id = :userId
        AND us.dDay = true
    """)
    Optional<UserSchedule> findByUserIdAndDDayTrue(Long userId);

}
