package com.togedy.togedy_server_v2.domain.support.dao;

import com.togedy.togedy_server_v2.domain.support.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
}
