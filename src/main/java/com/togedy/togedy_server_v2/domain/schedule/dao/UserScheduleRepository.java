package com.togedy.togedy_server_v2.domain.schedule.dao;

import com.togedy.togedy_server_v2.domain.schedule.entity.UserSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface UserScheduleRepository extends JpaRepository<UserSchedule, Long> {

    @Query("""
       SELECT us
       FROM UserSchedule us
       WHERE us.user.id = :userId
       AND YEAR(us.startDate)  = :year
       AND MONTH(us.startDate) = :month
    """)
    List<UserSchedule> findByUserIdAndYearAndMonth(
            @Param("userId") Long userId,
            @Param("year") int year,
            @Param("month") int month
    );

    @Query("""
       SELECT us
       FROM UserSchedule us
       WHERE us.user.id = :userId
       AND DATE(us.startDate) = :date
    """)
    List<UserSchedule> findByUserIdAndDate(
            @Param("userId") Long userId,
            @Param("date") LocalDate date
    );

}
