package com.togedy.togedy_server_v2.domain.study.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.togedy.togedy_server_v2.domain.study.enums.StudyRole;
import com.togedy.togedy_server_v2.domain.study.exception.StudyLeaderCannotRemoveSelfException;
import com.togedy.togedy_server_v2.domain.study.exception.StudyLeaderRequiredException;
import com.togedy.togedy_server_v2.domain.study.exception.StudyMemberRequiredException;
import com.togedy.togedy_server_v2.global.fixtures.UserStudyFixture;
import org.junit.jupiter.api.Test;

public class UserStudyTest {

    @Test
    public void 스터디_리더인_경우_리더_검증에_통과한다() {
        //given
        UserStudy leader = UserStudyFixture.createLeaderUserStudy(1L, 1L);

        // when & then
        assertThatCode(leader::validateStudyLeader)
                .doesNotThrowAnyException();
    }

    @Test
    public void 스터디_멤버인_경우_멤버_검증에_통과한다() {
        //given
        UserStudy member = UserStudyFixture.createMemberUserStudy(1L, 1L);

        // when & then
        assertThatCode(member::validateStudyMember)
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
        assertThat(leader.getRole()).isEqualTo(StudyRole.MEMBER);
        assertThat(member.getRole()).isEqualTo(StudyRole.LEADER);
    }

    @Test
    public void 스터디_리더가_아닌_경우_리더_검증_시_예외가_발생한다() {
        //given
        UserStudy member = UserStudyFixture.createMemberUserStudy(1L, 1L);

        // when & then
        assertThatThrownBy(member::validateStudyLeader)
                .isInstanceOf(StudyLeaderRequiredException.class);
    }

    @Test
    public void 스터디_리더인_경우_멤버_검증_시_예외가_발생한다() {
        //given
        UserStudy leader = UserStudyFixture.createLeaderUserStudy(1L, 1L);

        // when & then
        assertThatThrownBy(leader::validateStudyMember)
                .isInstanceOf(StudyMemberRequiredException.class);
    }

    @Test
    public void 스터디_리더가_아닌_경우_리더_위임_시_예외가_발생한다() {
        //given
        UserStudy leader = UserStudyFixture.createLeaderUserStudy(1L, 1L);
        UserStudy member = UserStudyFixture.createMemberUserStudy(2L, 1L);

        // when
        assertThatThrownBy(() -> member.delegateLeader(leader))
                .isInstanceOf(StudyLeaderRequiredException.class);
    }

    @Test
    public void 스터디_리더는_멤버를_추방할_수_있다() {
        //given
        UserStudy leader = UserStudyFixture.createLeaderUserStudy(1L, 1L);
        UserStudy member = UserStudyFixture.createMemberUserStudy(2L, 1L);

        // when
        leader.validateRemovable(2L);

        // then
        assertThatCode(() ->
                leader.validateRemovable(2L))
                .doesNotThrowAnyException();
    }

    @Test
    public void 스터디_리더를_추방하는_경우_예외가_발생한다() {
        //given
        UserStudy leader = UserStudyFixture.createLeaderUserStudy(1L, 1L);

        // when & then
        assertThatThrownBy(() ->
                leader.validateRemovable(1L)
        ).isInstanceOf(StudyLeaderCannotRemoveSelfException.class);
    }
}
