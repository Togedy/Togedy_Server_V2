package com.togedy.togedy_server_v2.domain.study.dao;

import com.togedy.togedy_server_v2.domain.study.entity.UserStudy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserStudyRepository extends JpaRepository<UserStudy, Long> {
    Optional<UserStudy> findByStudyIdAndUserId(Long studyId, Long userId);

    List<UserStudy> findAllByStudyId(Long studyId);

    void deleteByStudyIdAndUserId(Long studyId, Long memberId);

}
