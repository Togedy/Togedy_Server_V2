package com.togedy.togedy_server_v2.domain.badge.dao;

import com.togedy.togedy_server_v2.domain.badge.entity.Holiday;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HolidayRepository extends JpaRepository<Holiday, Long> {

    @Query("""
            SELECT COUNT(h) > 0
            FROM Holiday h
            WHERE h.startDate <= :endDate
              AND h.endDate >= :startDate
              AND (:excludedHolidayId IS NULL OR h.id <> :excludedHolidayId)
            """)
    boolean existsOverlapping(@Param("startDate") LocalDate startDate,
                              @Param("endDate") LocalDate endDate,
                              @Param("excludedHolidayId") Long excludedHolidayId);

    List<Holiday> findAllByEndDate(LocalDate endDate);
}
