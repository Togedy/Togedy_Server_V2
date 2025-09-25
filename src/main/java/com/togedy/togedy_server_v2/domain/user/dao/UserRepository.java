package com.togedy.togedy_server_v2.domain.user.dao;

import com.togedy.togedy_server_v2.domain.study.enums.StudyRole;
import com.togedy.togedy_server_v2.domain.user.entity.User;
import com.togedy.togedy_server_v2.domain.user.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


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
            SELECT u, us.role
            FROM User u
            JOIN UserStudy us ON u.id = us.id
            WHERE us.studyId = :studyId
            ORDER BY us.createdAt ASC
            """)
    List<Object[]> findAllByStudyIdOrderByCreatedAtAsc(Long studyId);

    @Query("""
                SELECT u
                FROM User u
                JOIN UserStudy us on us.studyId = :studyId
                WHERE us.userId = u.id
                    AND u.status = :status
            """)
    List<User> findAllByStudyIdAndStatus(Long studyId, UserStatus status);
}
