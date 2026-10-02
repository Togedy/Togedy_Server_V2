package com.togedy.togedy_server_v2.domain.config.dao;

import com.togedy.togedy_server_v2.domain.config.entity.CalendarAnnouncement;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CalendarAnnouncementRepository extends JpaRepository<CalendarAnnouncement, Long> {

    Optional<CalendarAnnouncement> findTopByOrderByCreatedAtDesc();
}
