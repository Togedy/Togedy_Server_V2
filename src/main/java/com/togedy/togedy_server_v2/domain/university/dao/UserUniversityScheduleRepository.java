package com.togedy.togedy_server_v2.domain.university.dao;

import com.togedy.togedy_server_v2.domain.university.entity.UserUniversitySchedule;
import com.togedy.togedy_server_v2.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface UserUniversityScheduleRepository extends JpaRepository<UserUniversitySchedule, Long> {

    List<UserUniversitySchedule> findByUserAndUniversityScheduleIdIn(User user, List<Long> scheduleIds);

    @Query("""
      SELECT uus
      FROM UserUniversitySchedule uus
      JOIN FETCH uus.universitySchedule us
      WHERE uus.user.id = :userId
      AND YEAR(us.startDate) = :year
      AND MONTH(us.startDate) = :month
    """)
    List<UserUniversitySchedule> findByUserIdAndYearAndMonth(
            @Param("userId") Long userId,
            @Param("year")   int  year,
            @Param("month")  int  month
    );

    @Query("""
      SELECT uus
      FROM UserUniversitySchedule uus
      JOIN FETCH uus.universitySchedule us
      WHERE uus.user.id = :userId
      AND DATE(us.startDate) = :date
    """)
    List<UserUniversitySchedule> findByUserIdAndDate(
            @Param("userId") Long userId,
            @Param("date") LocalDate date
    );

    @Query("""
        SELECT uus.universitySchedule.id
        FROM UserUniversitySchedule uus
        WHERE uus.user.id = :userId
        """)
    List<Long> findAddedUniversityScheduleIds(@Param("userId") Long userId);
}
