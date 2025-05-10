package com.togedy.togedy_server_v2.domain.university.dao;

import com.togedy.togedy_server_v2.domain.university.entity.UserUniversitySchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserUniversityScheduleRepository extends JpaRepository<UserUniversitySchedule, Long> {
}
