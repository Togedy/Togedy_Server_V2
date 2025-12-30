package com.togedy.togedy_server_v2.domain.study.entity;

import com.togedy.togedy_server_v2.domain.global.fixtures.UserStudyFixture;
import com.togedy.togedy_server_v2.domain.study.enums.StudyRole;
import com.togedy.togedy_server_v2.domain.study.exception.StudyLeaderRequiredException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class UserStudyTest {

    @Test
    public void 스터디_리더인_경우_리더_검증에_통과한다() {
        //given
        UserStudy leader = UserStudyFixture.createLeaderUserStudy(1L, 1L);

        // when & then
        Assertions.assertThatCode(leader::validateStudyLeader)
                .doesNotThrowAnyException();
    }

    @Test
    public void 스터디_멤버인_경우_멤버_검증에_통과한다() {
        //given
        UserStudy member = UserStudyFixture.createLeaderUserStudy(1L, 1L);

        // when & then
        Assertions.assertThatCode(member::validateStudyMember)
                .doesNotThrowAnyException();
    }

    @Test
    public void 스터디_리더인_경우_리더를_위임할_수_있다() {
        //given
        UserStudy leader = UserStudyFixture.createLeaderUserStudy(1L, 1L);
        UserStudy member = UserStudyFixture.createMemberUserStudy(2L, 1L);

        // when
        leader.delegateLeader(member);

        // then
        Assertions.assertThat(leader.getRole()).isEqualTo(StudyRole.MEMBER);
        Assertions.assertThat(member.getRole()).isEqualTo(StudyRole.LEADER);
    }

    @Test
    public void 스터디_리더가_아닌_경우_리더_검증_시_예외가_발생한다() {
        //given
        UserStudy member = UserStudyFixture.createMemberUserStudy(1L, 1L);

        // when & then
        Assertions.assertThatThrownBy(member::validateStudyLeader)
                .isInstanceOf(StudyLeaderRequiredException.class);
    }

    @Test
    public void 스터디_멤버가_아닌_경우_멤버_검증_시_예외가_발생한다() {
        //given
        UserStudy leader = UserStudyFixture.createLeaderUserStudy(1L, 1L);

        // when & then
        Assertions.assertThatThrownBy(leader::validateStudyMember)
                .isInstanceOf(StudyLeaderRequiredException.class);
    }

    @Test
    public void 스터디_리더가_아닌_경우_리더_위임_시_예외가_발생한다() {
        //given
        UserStudy leader = UserStudyFixture.createLeaderUserStudy(1L, 1L);
        UserStudy member = UserStudyFixture.createMemberUserStudy(2L, 1L);

        // when
        Assertions.assertThatThrownBy(() -> member.delegateLeader(leader))
                .isInstanceOf(StudyLeaderRequiredException.class);
    }
}
