package com.togedy.togedy_server_v2.domain.planner.dao;

import com.togedy.togedy_server_v2.domain.planner.entity.PlannerDailyImage;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlannerDailyImageRepository extends JpaRepository<PlannerDailyImage, Long> {

    Optional<PlannerDailyImage> findByUserIdAndDate(Long userId, LocalDate date);

    Optional<PlannerDailyImage> findTopByUserIdAndDateLessThanEqualOrderByDateDesc(Long userId, LocalDate date);

    List<PlannerDailyImage> findAllByUserId(Long userId);

    void deleteAllByUserId(Long userId);
}
