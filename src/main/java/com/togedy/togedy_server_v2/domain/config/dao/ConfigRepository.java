package com.togedy.togedy_server_v2.domain.config.dao;

import com.togedy.togedy_server_v2.domain.config.entity.AppConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfigRepository extends JpaRepository<AppConfig, String> {
}
