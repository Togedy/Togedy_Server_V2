package com.togedy.togedy_server_v2.domain.study.dao;

import com.togedy.togedy_server_v2.domain.study.entity.Study;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudyRepository extends JpaRepository<Study, Long> {
    boolean existsByInvitationCode(String invitationCode);

    boolean existsByName(String studyName);

    Optional<Study> findByInvitationCode(String invitationCode);
}
