package com.togedy.togedy_server_v2.domain.study.dao;

import com.togedy.togedy_server_v2.domain.global.fixtures.StudyFixture;
import com.togedy.togedy_server_v2.domain.global.fixtures.UserFixture;
import com.togedy.togedy_server_v2.domain.global.fixtures.UserStudyFixture;
import com.togedy.togedy_server_v2.domain.global.support.AbstractRepositoryTest;
import com.togedy.togedy_server_v2.domain.study.entity.Study;
import com.togedy.togedy_server_v2.domain.study.enums.StudyTag;
import com.togedy.togedy_server_v2.domain.user.dao.UserRepository;
import com.togedy.togedy_server_v2.domain.user.entity.User;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public class StudyRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private UserStudyRepository userStudyRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void 스터디_이름_중복_여부를_확인한다() {
        //given
        Study study = StudyFixture.createNormalStudy();
        studyRepository.save(study);

        // when
        boolean exists = studyRepository.existsByName(study.getName());

        // then
        Assertions.assertThat(exists).isTrue();
    }

    @Test
    public void 유저가_참여한_스터디를_참여순으로_조회한다() {
        //given
        Study study1 = studyRepository.save(StudyFixture.createNormalStudyWithName("스터디1"));
        userStudyRepository.save(UserStudyFixture.createLeaderUserStudy(1L, study1.getId()));

        Study study2 = studyRepository.save(StudyFixture.createNormalStudyWithName("스터디2"));
        userStudyRepository.save(UserStudyFixture.createMemberUserStudy(1L, study2.getId()));

        // when
        List<Study> result = studyRepository.findAllByUserIdOrderByCreatedAtAsc(1L);

        // then
        Assertions.assertThat(result)
                .extracting(Study::getName)
                .containsExactly(study1.getName(), study2.getName());
    }

    @Test
    public void 태그가_일치하는_스터디를_조회한다() {
        //given
        Study schoolNormalStudy = studyRepository.save(StudyFixture.createNormalStudyWithTag(StudyTag.SCHOOL));
        Study freeNormalStudy = studyRepository.save(StudyFixture.createNormalStudyWithTag(StudyTag.FREE));
        Study schoolChallengeStudy = studyRepository.save(StudyFixture.createChallengeStudyWithTag(StudyTag.SCHOOL));

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Slice<Study> result = studyRepository.findStudiesWithTags(
                null,
                List.of(StudyTag.SCHOOL),
                "latest",
                false,
                false,
                pageable
        );

        // then
        Assertions.assertThat(result.getContent())
                .hasSize(2)
                .extracting(Study::getTag)
                .containsOnly(StudyTag.SCHOOL);
    }

    @Test
    public void 태그가_일치하는_참여_가능한_스터디만_조회한다() {
        //given
        Study schoolNormalStudy = studyRepository.save(StudyFixture.createNormalStudyWithTag(StudyTag.SCHOOL));
        Study freeNormalStudy = studyRepository.save(StudyFixture.createNormalStudyWithTag(StudyTag.FREE));
        Study fullStudy = StudyFixture.createNormalStudyWithTag(StudyTag.SCHOOL);

        for (int memberCount = 1; memberCount < fullStudy.getMemberLimit(); memberCount++) {
            fullStudy.increaseMemberCount();
        }

        studyRepository.save(fullStudy);

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Slice<Study> result = studyRepository.findStudiesWithTags(
                null,
                List.of(StudyTag.SCHOOL),
                "latest",
                true,
                false,
                pageable
        );

        // then
        Assertions.assertThat(result.getContent())
                .hasSize(1)
                .extracting(Study::getId)
                .containsOnly(schoolNormalStudy.getId());
    }

    @Test
    public void 태그가_일치하는_챌린지_스터디만_조회한다() {
        //given
        Study schoolChallengeStudy = studyRepository.save(StudyFixture.createChallengeStudyWithTag(StudyTag.SCHOOL));
        Study freeNormalStudy = studyRepository.save(StudyFixture.createNormalStudyWithTag(StudyTag.FREE));
        Study schoolNormalStudy = studyRepository.save(StudyFixture.createNormalStudyWithTag(StudyTag.SCHOOL));

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Slice<Study> result = studyRepository.findStudiesWithTags(
                null,
                List.of(StudyTag.SCHOOL),
                "latest",
                false,
                true,
                pageable
        );

        // then
        Assertions.assertThat(result.getContent())
                .hasSize(1)
                .extracting(Study::getId)
                .containsOnly(schoolChallengeStudy.getId());
    }

    @Test
    public void 태그가_일치하는_이름에_입력값이_포함된_스터디만_조회한다() {
        //given
        Study schoolStudy = studyRepository.save(StudyFixture.createNormalStudyWithName("내신 스터디"));
        Study freeStudy = studyRepository.save(StudyFixture.createNormalStudyWithName("취업 스터디"));

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Slice<Study> result = studyRepository.findStudiesWithTags(
                "내신",
                List.of(StudyTag.SCHOOL),
                "latest",
                false,
                false,
                pageable
        );

        // then
        Assertions.assertThat(result.getContent())
                .hasSize(1)
                .extracting(Study::getId)
                .containsOnly(schoolStudy.getId());
    }

    @Test
    public void 태그_조건_없이_스터디를_조회한다() {
        //given
        Study study = studyRepository.save(StudyFixture.createNormalStudy());
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Slice<Study> result = studyRepository.findStudiesWithoutTags(
                null,
                "latest",
                false,
                false,
                pageable
        );

        // then
        Assertions.assertThat(result).hasSize(1);
    }

    @Test
    public void 태그_조건_없이_참여_가능한_스터디만_조회한다() {
        //given
        Study study = studyRepository.save(StudyFixture.createNormalStudy());
        Study fullStudy = StudyFixture.createNormalStudy();

        for (int memberCount = 1; memberCount < fullStudy.getMemberLimit(); memberCount++) {
            fullStudy.increaseMemberCount();
        }

        studyRepository.save(fullStudy);

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Slice<Study> result = studyRepository.findStudiesWithoutTags(
                null,
                "latest",
                true,
                false,
                pageable
        );

        // then
        Assertions.assertThat(result.getContent())
                .hasSize(1)
                .extracting(Study::getId)
                .containsOnly(study.getId());
    }

    @Test
    public void 태그_조건_없이_챌린지_스터디만_조회한다() {
        //given
        Study challengeStudy = studyRepository.save(StudyFixture.createChallengeStudy());
        Study normalStudy = studyRepository.save(StudyFixture.createNormalStudy());

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Slice<Study> result = studyRepository.findStudiesWithoutTags(
                null,
                "latest",
                false,
                true,
                pageable
        );

        // then
        Assertions.assertThat(result.getContent())
                .hasSize(1)
                .extracting(Study::getId)
                .containsOnly(challengeStudy.getId());
    }

    @Test
    public void 태그_조건_없이_이름에_입력값이_포함된_스터디만_조회한다() {
        //given
        Study schoolStudy = studyRepository.save(StudyFixture.createNormalStudyWithName("내신 스터디"));
        Study freeStudy = studyRepository.save(StudyFixture.createNormalStudyWithName("취업 스터디"));

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Slice<Study> result = studyRepository.findStudiesWithoutTags(
                "내신",
                "latest",
                false,
                false,
                pageable
        );

        // then
        Assertions.assertThat(result.getContent())
                .hasSize(1)
                .extracting(Study::getId)
                .containsOnly(schoolStudy.getId());
    }

    @Test
    public void 가장_많은_멤버가_공부_중인_스터디를_조회한다() {
        //given
        Study study1 = studyRepository.save(StudyFixture.createNormalStudy());
        Study study2 = studyRepository.save(StudyFixture.createNormalStudy());

        User studyingUser = userRepository.save(UserFixture.createStudyingUser());
        User activeUser = userRepository.save(UserFixture.createUser());

        userStudyRepository.save(UserStudyFixture.createLeaderUserStudy(studyingUser.getId(), study1.getId()));
        userStudyRepository.save(UserStudyFixture.createMemberUserStudy(activeUser.getId(), study2.getId()));

        Pageable pageable = PageRequest.of(0, 10);

        // when
        List<Study> result = studyRepository.findMostActiveStudies(pageable);

        // then
        Assertions.assertThat(result)
                .hasSize(1)
                .extracting(Study::getId)
                .containsExactly(study1.getId());
    }

}
