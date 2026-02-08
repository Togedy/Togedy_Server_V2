package com.togedy.togedy_server_v2.domain.planner.dao;

import com.togedy.togedy_server_v2.domain.planner.entity.StudySubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StudySubjectRepository extends JpaRepository<StudySubject, Long> {
    @Query("""
            SELECT ss
            FROM StudySubject ss
            WHERE ss.id = :id
            AND ss.status = 'ACTIVE'
            """)
    Optional<StudySubject> findActiveById(Long id);

    @Query("""
            SELECT ss
            FROM StudySubject ss
            WHERE ss.userId = :userId
            AND ss.status = 'ACTIVE'
            ORDER BY ss.orderIndex ASC
            """)
    List<StudySubject> findAllByUserId(Long userId);

    @Query("""
            SELECT count(ss) > 0
            FROM StudySubject ss
            WHERE ss.name = :name
            AND ss.color = :color
            AND ss.userId = :userId
            AND ss.status = 'ACTIVE'
            """)
    boolean existsByNameAndColorAndUserId(String name, String color, Long userId);

    @Query("""
            SELECT MAX(ss.orderIndex)
            FROM StudySubject ss
            WHERE ss.userId = :userId
            AND ss.status = 'ACTIVE'
            """)
    Long findMaxOrderIndex(Long userId);

    Optional<StudySubject> findByNameAndColorAndUserId(String name, String color, Long userId);
}
