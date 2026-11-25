package com.togedy.togedy_server_v2.domain.badge.dao;

import com.togedy.togedy_server_v2.domain.badge.entity.HolidayKind;
import com.togedy.togedy_server_v2.domain.badge.enums.DateRuleType;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HolidayKindRepository extends JpaRepository<HolidayKind, Long> {

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);

    List<HolidayKind> findAllByDateRuleTypeNot(DateRuleType dateRuleType);

    List<HolidayKind> findAllByDateRuleType(DateRuleType dateRuleType, Sort sort);
}
