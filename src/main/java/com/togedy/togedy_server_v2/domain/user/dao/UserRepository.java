package com.togedy.togedy_server_v2.domain.user.dao;

import com.togedy.togedy_server_v2.domain.study.dto.StudyMemberRoleDto;
import com.togedy.togedy_server_v2.domain.study.enums.StudyRole;
import com.togedy.togedy_server_v2.domain.user.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Boolean existsByNickname(String nickname);

    Boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    @Query("""
                SELECT u
                FROM User u
                JOIN UserStudy us on us.userId = u.id
                WHERE us.studyId = :studyId
                    AND us.role = :role
            """)
    Optional<User> findByStudyIdAndRole(Long studyId, StudyRole role);

    @Query("""
                SELECT u
                FROM User u
                JOIN UserStudy us on us.studyId = :studyId
                WHERE us.userId = u.id
            """)
    List<User> findAllByStudyId(Long studyId);

    @Query("""
                SELECT new com.togedy.togedy_server_v2.domain.study.dto.StudyMemberRoleDto (
                    u as user,
                    us.role as role
                )
                FROM User u
                JOIN UserStudy us ON u.id = us.userId
                WHERE us.studyId = :studyId
                ORDER BY us.createdAt ASC
            """)
    List<StudyMemberRoleDto> findAllByStudyIdOrderByCreatedAtAsc(Long studyId);
}
