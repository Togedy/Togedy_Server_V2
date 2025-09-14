package com.togedy.togedy_server_v2.domain.study.dao;

import com.togedy.togedy_server_v2.domain.study.entity.UserStudy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserStudyRepository extends JpaRepository<UserStudy, Long> {
    Optional<UserStudy> findByStudyIdAndUserId(Long studyId, Long userId);

    void deleteByStudyIdAndUserId(Long studyId, Long memberId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        DELETE FROM UserStudy us
        WHERE us.studyId = :studyId
    """)
    void deleteAllByStudyId(Long studyId);

    boolean existsByStudyIdAndUserId(Long studyId, Long userId);
}
