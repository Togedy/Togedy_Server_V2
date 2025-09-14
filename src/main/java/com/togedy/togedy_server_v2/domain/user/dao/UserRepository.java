package com.togedy.togedy_server_v2.domain.user.dao;

import com.togedy.togedy_server_v2.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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
    Optional<User> findByStudyIdAndRole(Long studyId, String role);
}
