package com.togedy.togedy_server_v2.domain.study.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.togedy.togedy_server_v2.domain.planner.entity.DailyStudySummary;
import com.togedy.togedy_server_v2.domain.study.dto.GetStudyMemberProfileResponse;
import com.togedy.togedy_server_v2.domain.study.dto.GetStudyMemberStudyTimeResponse;
import com.togedy.togedy_server_v2.domain.study.entity.UserStudy;
import com.togedy.togedy_server_v2.domain.study.exception.StudyAccessDeniedException;
import com.togedy.togedy_server_v2.domain.study.exception.UserStudyNotFoundException;
import com.togedy.togedy_server_v2.domain.user.entity.User;
import com.togedy.togedy_server_v2.domain.user.exception.UserNotFoundException;
import com.togedy.togedy_server_v2.global.fixtures.DailyStudySummaryFixture;
import com.togedy.togedy_server_v2.global.fixtures.UserFixture;
import com.togedy.togedy_server_v2.global.fixtures.UserStudyFixture;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.test.util.ReflectionTestUtils;

public class StudyMemberServiceTest extends AbstractStudyServiceTest {

    @InjectMocks
    StudyMemberService studyMemberService;

    @Test
    public void 스터디_멤버의_프로필을_조회한다() {
        //given
        Long studyId = 1L;
        Long userId = 1L;
        Long memberId = 2L;
        Long totalStudyTime = 3 * 3600L;

        User member = UserFixture.createMember();
        UserStudy memberUserStudy = UserStudyFixture.createMemberUserStudy(memberId, studyId);
        ReflectionTestUtils.setField(memberUserStudy, "createdAt", LocalDateTime.now());

        given(userStudyRepository.existsByStudyIdAndUserId(any(), any()))
                .willReturn(true);

        given(userRepository.findById(any()))
                .willReturn(Optional.of(member));

        given(dailyStudySummaryRepository.findTotalStudyTimeByUserId(any()))
                .willReturn(Optional.of(totalStudyTime));

        given(userStudyRepository.findByStudyIdAndUserId(any(), any()))
                .willReturn(Optional.of(memberUserStudy));

        // when
        GetStudyMemberProfileResponse response = studyMemberService.findStudyMemberProfile(studyId, memberId, userId);

        // then
        assertThat(response.getUserName()).isEqualTo(member.getNickname());
        assertThat(response.getTotalStudyTime()).isEqualTo("03:00:00");
        assertThat(response.getElapsedDays()).isEqualTo(0);
    }

    @Test
    public void 스터디_멤버의_프로필을_조회_시_공부_기록이_존재하지_않는_경우_00시_00분_00초를_반환한다() {
        //given
        Long studyId = 1L;
        Long userId = 1L;
        Long memberId = 2L;

        User member = UserFixture.createMember();
        UserStudy memberUserStudy = UserStudyFixture.createMemberUserStudy(memberId, studyId);
        ReflectionTestUtils.setField(memberUserStudy, "createdAt", LocalDateTime.now());

        given(userStudyRepository.existsByStudyIdAndUserId(any(), any()))
                .willReturn(true);

        given(userRepository.findById(any()))
                .willReturn(Optional.of(member));

        given(dailyStudySummaryRepository.findTotalStudyTimeByUserId(any()))
                .willReturn(Optional.empty());

        given(userStudyRepository.findByStudyIdAndUserId(any(), any()))
                .willReturn(Optional.of(memberUserStudy));

        // when
        GetStudyMemberProfileResponse response = studyMemberService.findStudyMemberProfile(studyId, memberId, userId);

        // then
        assertThat(response.getTotalStudyTime()).isEqualTo("00:00:00");
    }

    @Test
    public void 스터디_멤버_프로필_조회_시_해당_유저가_스터디에_가입하지_않은_경우_예외가_발생한다() {
        //given
        Long studyId = 1L;
        Long userId = 1L;
        Long memberId = 2L;

        given(userStudyRepository.existsByStudyIdAndUserId(any(), any()))
                .willReturn(false);

        // when & then
        assertThatThrownBy(() ->
                studyMemberService.findStudyMemberProfile(studyId, memberId, userId)
        ).isInstanceOf(StudyAccessDeniedException.class);
    }

    @Test
    public void 스터디_멤버_프로필_조회_시_멤버가_존재하지_않는_경우_예외가_발생한다() {
        //given
        Long studyId = 1L;
        Long userId = 1L;
        Long memberId = 2L;

        given(userStudyRepository.existsByStudyIdAndUserId(any(), any()))
                .willReturn(true);

        given(userRepository.findById(any()))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                studyMemberService.findStudyMemberProfile(studyId, memberId, userId)
        ).isInstanceOf(UserNotFoundException.class);
    }

    @Test
    public void 스터디_멤버_프로필_조회_시_멤버가_스터디에_가입하지_않은_경우_예외가_발생한다() {
        //given
        Long studyId = 1L;
        Long userId = 1L;
        Long memberId = 2L;

        given(userStudyRepository.existsByStudyIdAndUserId(any(), any()))
                .willReturn(true);

        given(userRepository.findById(any()))
                .willReturn(Optional.of(UserFixture.createMember()));

        given(userStudyRepository.findByStudyIdAndUserId(any(), any()))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                studyMemberService.findStudyMemberProfile(studyId, memberId, userId)
        ).isInstanceOf(UserStudyNotFoundException.class);
    }

    @Test
    public void 스터디_멤버의_최근_6개월의_공부_시간을_월별로_집계하여_조회한다() {
        //given
        Long studyId = 1L;
        Long userId = 1L;
        Long memberId = 2L;

        LocalDate date = LocalDate.now().plusDays(1).atStartOfDay().toLocalDate();
        DailyStudySummary dailyStudySummary = DailyStudySummaryFixture.createDailyStudySummaryWithDate(date);

        given(userStudyRepository.existsByStudyIdAndUserId(any(), any()))
                .willReturn(true);

        given(dailyStudySummaryRepository.findAllByUserIdAndPeriod(any(), any(), any()))
                .willReturn(List.of(dailyStudySummary));

        // when
        GetStudyMemberStudyTimeResponse response =
                studyMemberService.findStudyMemberStudyTime(studyId, memberId, userId);

        // then
        assertThat(response.getStudyTimeCount()).isEqualTo(1);
        assertThat(response.getMonthlyStudyTimeList().get(5).getYear()).isEqualTo(date.getYear());
        assertThat(response.getMonthlyStudyTimeList().get(5).getMonth()).isEqualTo(date.getMonth().getValue());
    }

    @Test
    public void 스터디_멤버의_월별_공부_시간_조회시_공부_시간별_학습_레벨을_계산한다() {
        // given
        Long studyId = 1L;
        Long userId = 1L;
        Long memberId = 2L;

        LocalDate date = LocalDate.now().plusDays(1).atStartOfDay().toLocalDate();
        DailyStudySummary dailyStudySummary = DailyStudySummaryFixture.createDailyStudySummaryWithDate(date);

        given(userStudyRepository.existsByStudyIdAndUserId(any(), any()))
                .willReturn(true);

        given(dailyStudySummaryRepository.findAllByUserIdAndPeriod(any(), any(), any()))
                .willReturn(List.of(dailyStudySummary));

        // when
        GetStudyMemberStudyTimeResponse response =
                studyMemberService.findStudyMemberStudyTime(studyId, memberId, userId);

        // then
        int level = response.getMonthlyStudyTimeList()
                .get(5)
                .getStudyTimeList()
                .get(date.getDayOfMonth() - 1);

        assertThat(level).isEqualTo(3);
    }

    @Test
    public void 스터디_멤버의_월별_공부_시간_조회_시_유저가_스터디에_가입하지_않은_경우_예외가_발생한다() {
        // given
        Long studyId = 1L;
        Long userId = 1L;
        Long memberId = 2L;

        given(userStudyRepository.existsByStudyIdAndUserId(any(), any()))
                .willReturn(false);

        // when & then
        assertThatThrownBy(() ->
                studyMemberService.findStudyMemberStudyTime(studyId, memberId, userId)
        ).isInstanceOf(StudyAccessDeniedException.class);
    }
}
