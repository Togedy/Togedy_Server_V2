package com.togedy.togedy_server_v2.domain.university.dao;

import com.togedy.togedy_server_v2.domain.university.entity.UserUniversityMethod;
import com.togedy.togedy_server_v2.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserUniversityMethodRepository extends JpaRepository<UserUniversityMethod, Long> {

    List<UserUniversityMethod> findByUserAndUniversityAdmissionMethodIdIn(User user, List<Long> id);
}
