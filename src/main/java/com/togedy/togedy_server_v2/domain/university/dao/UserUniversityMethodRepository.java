package com.togedy.togedy_server_v2.domain.university.dao;

import com.togedy.togedy_server_v2.domain.university.entity.UserUniversityMethod;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserUniversityMethodRepository extends JpaRepository<UserUniversityMethod, Long> {

    @Query(value = """
            SELECT uum
            FROM UserUniversityMethod uum
            WHERE uum.universityAdmissionMethod = :universityAdmissionMethodId
                AND uum.user.id = :userId
            """)
    Optional<UserUniversityMethod> findAddedUserUniversityMethod(
            @Param("universityAdmissionMethodId") Long universityAdmissionMethodId,
            @Param("userId") Long userId
    );

    boolean existsByUniversityAdmissionMethodIdAndUserId(Long universityAdmissionMethodId, Long userId);

    void deleteAllByUserId(Long userId);
}
