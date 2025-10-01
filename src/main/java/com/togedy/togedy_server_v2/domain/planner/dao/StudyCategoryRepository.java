package com.togedy.togedy_server_v2.domain.planner.dao;

import com.togedy.togedy_server_v2.domain.planner.entity.StudyCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StudyCategoryRepository extends JpaRepository<StudyCategory, Long> {

    @Query("""
            SELECT sc
            FROM StudyCategory sc
            WHERE sc.userId = :userId
            """)
    List<StudyCategory> findAllByUserId(Long userId);
}
