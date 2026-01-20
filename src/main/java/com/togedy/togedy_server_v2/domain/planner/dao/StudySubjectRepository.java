package com.togedy.togedy_server_v2.domain.planner.dao;

import com.togedy.togedy_server_v2.domain.planner.entity.StudySubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StudySubjectRepository extends JpaRepository<StudySubject, Long> {
    @Query("""
            SELECT sc
            FROM StudySubject sc
            WHERE sc.id = :id
            AND sc.status = 'ACTIVE'
            """)
    Optional<StudySubject> findActiveById(Long id);

    @Query("""
            SELECT sc
            FROM StudySubject sc
            WHERE sc.userId = :userId
            AND sc.status = 'ACTIVE'
            ORDER BY sc.orderIndex ASC
            """)
    List<StudySubject> findAllByUserId(Long userId);

    @Query("""
            SELECT count(sc) > 0
            FROM StudySubject sc
            WHERE sc.name = :name
            AND sc.color = :color
            AND sc.userId = :userId
            AND sc.status = 'ACTIVE'
            """)
    boolean existsByNameAndColorAndUserId(String name, String color, Long userId);

    @Query("""
            SELECT MAX(sc.orderIndex)
            FROM StudySubject sc
            WHERE sc.userId = :userId
            AND sc.status = 'ACTIVE'
            """)
    Long findMaxOrderIndex(Long userId);

    Optional<StudySubject> findByNameAndColorAndUserId(String name, String color, Long userId);
}
