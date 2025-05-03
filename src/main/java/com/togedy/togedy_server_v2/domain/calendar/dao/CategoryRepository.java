package com.togedy.togedy_server_v2.domain.calendar.dao;

import com.togedy.togedy_server_v2.domain.calendar.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
