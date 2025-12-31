package com.togedy.togedy_server_v2.domain.study.dao;

import com.togedy.togedy_server_v2.domain.global.fixtures.StudyFixture;
import com.togedy.togedy_server_v2.domain.global.fixtures.UserFixture;
import com.togedy.togedy_server_v2.domain.global.fixtures.UserStudyFixture;
import com.togedy.togedy_server_v2.domain.global.support.AbstractRepositoryTest;
import com.togedy.togedy_server_v2.domain.study.dto.GetStudyMemberManagementResponse;
import com.togedy.togedy_server_v2.domain.study.entity.Study;
import com.togedy.togedy_server_v2.domain.study.entity.UserStudy;
import com.togedy.togedy_server_v2.domain.user.dao.UserRepository;
import com.togedy.togedy_server_v2.domain.user.entity.User;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class UserStudyRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    private UserStudyRepository userStudyRepository;

    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void 스터디_ID로_스터디_멤버_정보를_조회한다() {
        //given
        Study study = studyRepository.save(StudyFixture.createNormalStudy());
        User user = userRepository.save(UserFixture.createUser());
        UserStudy userStudy = userStudyRepository.save(
                UserStudyFixture.createLeaderUserStudy(user.getId(), study.getId())
        );

        // when
        List<GetStudyMemberManagementResponse> result = userStudyRepository.findStudyMembersByStudyId(study.getId());

        // then
        Assertions.assertThat(result).hasSize(1);
        Assertions.assertThat(result.get(0).getUserId()).isEqualTo(user.getId());
    }

    @Test
    public void 스터디_멤버를_닉네임_오름차순으로_조회한다() {
        //given
        Study study = studyRepository.save(StudyFixture.createNormalStudy());
        User userA = userRepository.save(UserFixture.createUserWithName("가가가"));
        User userB = userRepository.save(UserFixture.createUserWithName("나나나"));

        userStudyRepository.save(UserStudyFixture.createLeaderUserStudy(userB.getId(), study.getId()));
        userStudyRepository.save(UserStudyFixture.createLeaderUserStudy(userA.getId(), study.getId()));

        // when
        List<GetStudyMemberManagementResponse> result = userStudyRepository.findStudyMembersByStudyId(study.getId());

        // then
        Assertions.assertThat(result).hasSize(2)
                .extracting(GetStudyMemberManagementResponse::getUserName)
                .containsExactly(userA.getNickname(), userB.getNickname());
    }

    @Test
    public void 스터디_ID로_모든_스터디_멤버를_삭제한다() {
        // given
        Study study = studyRepository.save(StudyFixture.createNormalStudy());

        userStudyRepository.save(UserStudyFixture.createMemberUserStudy(1L, study.getId()));
        userStudyRepository.save(UserStudyFixture.createMemberUserStudy(2L, study.getId()));

        // when
        userStudyRepository.deleteAllByStudyId(study.getId());

        // then
        Assertions.assertThat(userStudyRepository.findAll()).isEmpty();
    }

}
