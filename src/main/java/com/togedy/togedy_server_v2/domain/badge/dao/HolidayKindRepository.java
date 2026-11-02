package com.togedy.togedy_server_v2.domain.badge.dao;

import com.togedy.togedy_server_v2.domain.badge.entity.HolidayKind;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HolidayKindRepository extends JpaRepository<HolidayKind, Long> {

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);
}
