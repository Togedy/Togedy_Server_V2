package com.togedy.togedy_server_v2.global.support;

import com.togedy.togedy_server_v2.domain.study.entity.Study;
import com.togedy.togedy_server_v2.domain.study.entity.UserStudy;
import com.togedy.togedy_server_v2.domain.study.enums.StudyRole;
import com.togedy.togedy_server_v2.domain.user.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class FixtureSupport {

    @PersistenceContext
    private EntityManager entityManager;

    public User persistUser(User user) {
        entityManager.persist(user);
        return user;
    }

    public Study persistStudy(Study study) {
        entityManager.persist(study);
        return study;
    }

    public UserStudy persistUserStudy(Study study, User user, StudyRole studyRole) {
        UserStudy userStudy = UserStudy.builder()
                .userId(user.getId())
                .studyId(study.getId())
                .role(studyRole)
                .build();

        entityManager.persist(userStudy);
        return userStudy;
    }

}
