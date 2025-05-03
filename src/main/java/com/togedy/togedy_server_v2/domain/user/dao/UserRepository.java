package com.togedy.togedy_server_v2.domain.user.dao;

import com.togedy.togedy_server_v2.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
