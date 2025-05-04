package com.togedy.togedy_server_v2.domain.schedule.dao;

import com.togedy.togedy_server_v2.domain.schedule.entity.UserSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserScheduleRepository extends JpaRepository<UserSchedule, Long> {
}
