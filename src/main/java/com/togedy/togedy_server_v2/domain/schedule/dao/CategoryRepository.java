package com.togedy.togedy_server_v2.domain.schedule.dao;

import com.togedy.togedy_server_v2.domain.schedule.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT c FROM Category c where c.user.id = :userId AND c.status = 'ACTIVE'")
    List<Category> findAllByUserId(Long userId);

    @Query("""
            SELECT (count(c) > 0 )
                FROM Category c
            WHERE c.name = :name 
                AND c.color = :color 
                AND c.user.id = :userId
                AND c.status = 'ACTIVE'
            """)
    boolean existsByNameAndColorAndUserId(String name, String color, Long userId);
}
