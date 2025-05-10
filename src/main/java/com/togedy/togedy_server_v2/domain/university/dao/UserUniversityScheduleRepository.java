package com.togedy.togedy_server_v2.domain.university.dao;

import com.togedy.togedy_server_v2.domain.university.entity.UserUniversitySchedule;
import com.togedy.togedy_server_v2.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserUniversityScheduleRepository extends JpaRepository<UserUniversitySchedule, Long> {

    List<UserUniversitySchedule> findByUserAndUniversityScheduleIdIn(User user, List<Long> scheduleIds);
}
