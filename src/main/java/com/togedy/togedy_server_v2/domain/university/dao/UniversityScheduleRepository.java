package com.togedy.togedy_server_v2.domain.university.dao;

import com.togedy.togedy_server_v2.domain.university.entity.UniversitySchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UniversityScheduleRepository extends JpaRepository<UniversitySchedule, Long> {

    Optional<UniversitySchedule> findById(Long universityScheduleId);
}
