package com.togedy.togedy_server_v2.domain.schedule.dao;

import com.togedy.togedy_server_v2.domain.schedule.entity.Category;
import com.togedy.togedy_server_v2.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByUserId(Long userId);

    boolean existsByColorAndNameAndUser(String color, String name, User user);
}
