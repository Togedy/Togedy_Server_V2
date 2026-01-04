package com.togedy.togedy_server_v2.domain.planner.dao;

import com.togedy.togedy_server_v2.domain.planner.entity.StudyCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StudyCategoryRepository extends JpaRepository<StudyCategory, Long> {
    @Query("""
            SELECT sc
            FROM StudyCategory sc
            WHERE sc.id = :id
            AND sc.status = 'ACTIVE'
            """)
    Optional<StudyCategory> findActiveById(Long id);

    @Query("""
            SELECT sc
            FROM StudyCategory sc
            WHERE sc.userId = :userId
            AND sc.status = 'ACTIVE'
            ORDER BY sc.orderIndex ASC
            """)
    List<StudyCategory> findAllByUserId(Long userId);

    @Query("""
            SELECT CASE WHEN COUNT(sc) > 0 THEN true ELSE false END
            FROM StudyCategory sc
            WHERE sc.name = :name
            AND sc.color = :color
            AND sc.userId = :userId
            AND sc.status = 'ACTIVE'
            """)
    boolean existsByNameAndColorAndUserId(String name, String color, Long userId);

    @Query("""
            SELECT MAX(sc.orderIndex)
            FROM StudyCategory sc
            WHERE sc.userId = :userId
            AND sc.status = 'ACTIVE'
            """)
    Long findMaxOrderIndex(Long userId);
}
