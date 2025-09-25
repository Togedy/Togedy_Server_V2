package com.togedy.togedy_server_v2.domain.study.dao;

import com.togedy.togedy_server_v2.domain.study.entity.Study;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StudyRepository extends JpaRepository<Study, Long> {

    boolean existsByName(String studyName);

    @Query("""
            SELECT s
            FROM Study s
            JOIN UserStudy us ON us.userId = :userId
            ORDER BY us.createdAt ASC
            """)
    List<Study> findAllByUserIdOrderByCreatedAtAsc(Long userId);
}
