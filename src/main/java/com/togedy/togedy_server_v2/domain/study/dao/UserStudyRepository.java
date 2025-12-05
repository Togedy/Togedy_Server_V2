package com.togedy.togedy_server_v2.domain.study.dao;

import com.togedy.togedy_server_v2.domain.study.dto.GetStudyMemberManagementResponse;
import com.togedy.togedy_server_v2.domain.study.entity.UserStudy;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

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

    @Query("""
            SELECT new com.togedy.togedy_server_v2.domain.study.dto.GetStudyMemberManagementResponse (
                u.id,
                u.nickname,
                us.role
            )
            FROM UserStudy us
            JOIN User u ON us.userId = u.id
            WHERE us.studyId = :studyId
            ORDER BY u.nickname ASC
            """)
    List<GetStudyMemberManagementResponse> findStudyMembersByStudyId(Long studyId);

    @Query("""
                SELECT us
                FROM UserStudy us
                WHERE us.studyId IN :studyIds
            """)
    List<UserStudy> findAllByStudyIds(List<Long> studyIds);
}
