package com.togedy.togedy_server_v2.domain.university.dao;

import com.togedy.togedy_server_v2.domain.schedule.entity.UserSchedule;
import com.togedy.togedy_server_v2.domain.university.entity.UserUniversitySchedule;
import com.togedy.togedy_server_v2.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
    List<UserUniversitySchedule> findByUserAndYearAndMonth(
            @Param("userId") Long userId,
            @Param("year")   int  year,
            @Param("month")  int  month
    );

    @Query("""
      SELECT uus
      FROM UserUniversitySchedule uus
      JOIN FETCH uus.universitySchedule us
      WHERE uus.user.id = :userId
      AND YEAR(us.startDate) = :year
      AND MONTH(us.startDate) = :month
      AND DATE(us.startDate) = :date
    """)
    List<UserUniversitySchedule> findByUserIdAndYearAndMonthAndDate(
            @Param("userId") Long userId,
            @Param("year") int year,
            @Param("month") int month,
            @Param("date") int date
    );
}
