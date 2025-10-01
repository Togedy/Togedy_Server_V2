package com.togedy.togedy_server_v2.domain.planner.dao;

import com.togedy.togedy_server_v2.domain.planner.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface PlanRepository extends JpaRepository<Plan, Long> {

    List<Plan> findByStudyCategoryIdAndCreatedAtBetween(
            Long studyCategoryId,
            LocalDateTime startOfDay,
            LocalDateTime endOfDay
    );
}
