package com.togedy.togedy_server_v2.domain.badge.dao;

import com.togedy.togedy_server_v2.domain.badge.entity.Holiday;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HolidayRepository extends JpaRepository<Holiday, Long> {

    boolean existsByHolidayKindId(Long holidayKindId);

    boolean existsByHolidayKindIdAndStartDate(Long holidayKindId, LocalDate startDate);

    boolean existsByHolidayKindIdAndStartDateAndIdNot(Long holidayKindId, LocalDate startDate, Long id);

    @EntityGraph(attributePaths = "holidayKind")
    List<Holiday> findAllByEndDateAndHolidayKindBadgeEnabledTrue(LocalDate endDate);

    @EntityGraph(attributePaths = "holidayKind")
    List<Holiday> findAllByStartDateIn(Collection<LocalDate> startDates);
}
