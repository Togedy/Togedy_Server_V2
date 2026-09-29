package com.togedy.togedy_server_v2.domain.planner.dao;

import com.togedy.togedy_server_v2.global.fixtures.StudyTimeFixture;
import com.togedy.togedy_server_v2.global.support.AbstractRepositoryTest;
import java.util.List;
import java.util.Set;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class StudyTimeRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    private StudyTimeRepository studyTimeRepository;

    @Test
    public void 종료되지_않은_타이머가_존재하는_유저_ID만_조회한다() {
        // given
        studyTimeRepository.save(StudyTimeFixture.createRunningStudyTime(1L));
        studyTimeRepository.save(StudyTimeFixture.createEndedStudyTime(2L));

        // when
        Set<Long> result = studyTimeRepository.findStudyingUserIds(List.of(1L, 2L));

        // then
        Assertions.assertThat(result).containsExactly(1L);
    }

    @Test
    public void 조회_대상이_아닌_유저는_공부_중이어도_조회되지_않는다() {
        // given
        studyTimeRepository.save(StudyTimeFixture.createRunningStudyTime(1L));
        studyTimeRepository.save(StudyTimeFixture.createRunningStudyTime(3L));

        // when
        Set<Long> result = studyTimeRepository.findStudyingUserIds(List.of(1L));

        // then
        Assertions.assertThat(result).containsExactly(1L);
    }

    @Test
    public void 빈_유저_ID_목록으로_조회하는_경우_빈_Set을_반환한다() {
        // given
        studyTimeRepository.save(StudyTimeFixture.createRunningStudyTime(1L));

        // when
        Set<Long> result = studyTimeRepository.findStudyingUserIds(List.of());

        // then
        Assertions.assertThat(result).isEmpty();
    }
}
