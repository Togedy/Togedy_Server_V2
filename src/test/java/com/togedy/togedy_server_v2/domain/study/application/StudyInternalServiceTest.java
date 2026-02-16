package com.togedy.togedy_server_v2.domain.study.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.togedy.togedy_server_v2.domain.planner.entity.DailyStudySummary;
import com.togedy.togedy_server_v2.domain.study.dto.DailyStudyTimeDto;
import com.togedy.togedy_server_v2.domain.study.dto.GetStudyAttendanceResponse;
import com.togedy.togedy_server_v2.domain.study.dto.GetStudyMemberManagementResponse;
import com.togedy.togedy_server_v2.domain.study.dto.GetStudyMemberResponse;
import com.togedy.togedy_server_v2.domain.study.dto.GetStudyResponse;
import com.togedy.togedy_server_v2.domain.study.dto.PatchStudyInformationRequest;
import com.togedy.togedy_server_v2.domain.study.dto.PatchStudyMemberLimitRequest;
import com.togedy.togedy_server_v2.domain.study.dto.PostStudyMemberRequest;
import com.togedy.togedy_server_v2.domain.study.dto.StudyMemberRoleDto;
import com.togedy.togedy_server_v2.domain.study.entity.Study;
import com.togedy.togedy_server_v2.domain.study.entity.UserStudy;
import com.togedy.togedy_server_v2.domain.study.enums.StudyRole;
import com.togedy.togedy_server_v2.domain.study.enums.StudyTag;
import com.togedy.togedy_server_v2.domain.study.event.StudyImageRemovedEvent;
import com.togedy.togedy_server_v2.domain.study.exception.StudyLeaderRequiredException;
import com.togedy.togedy_server_v2.domain.study.exception.StudyMemberRequiredException;
import com.togedy.togedy_server_v2.domain.study.exception.StudyNotFoundException;
import com.togedy.togedy_server_v2.domain.study.exception.UserStudyNotFoundException;
import com.togedy.togedy_server_v2.domain.user.entity.User;
import com.togedy.togedy_server_v2.global.enums.ImageCategory;
import com.togedy.togedy_server_v2.global.fixtures.DailyStudySummaryFixture;
import com.togedy.togedy_server_v2.global.fixtures.StudyFixture;
import com.togedy.togedy_server_v2.global.fixtures.UserFixture;
import com.togedy.togedy_server_v2.global.fixtures.UserStudyFixture;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

public class StudyInternalServiceTest extends AbstractStudyServiceTest {

    @InjectMocks
    StudyInternalService studyInternalService;

    @Mock
    ApplicationEventPublisher applicationEventPublisher;

    @Test
    public void 스터디_조회_시_스터디에_참여한_사용자는_joined값으로_true를_반환한다() {
        //given
        Study study = StudyFixture.createChallengeStudy();
        ReflectionTestUtils.setField(study, "id", 1L);

        User user = UserFixture.createUser();
        ReflectionTestUtils.setField(user, "id", 1L);

        given(studyRepository.findById(any()))
                .willReturn(Optional.of(study));

        given(userRepository.findByStudyIdAndRole(any(), any()))
                .willReturn(Optional.of(user));

        given(userStudyRepository.existsByStudyIdAndUserId(any(), any()))
                .willReturn(true);

        // when
        GetStudyResponse response = studyInternalService.findStudyInfo(1L, 1L);

        // then
        assertThat(response.getIsJoined()).isTrue();
    }

    @Test
    public void 스터디_조회_시_스터디에_참여하지_않은_사용자는_joined값으로_false를_반환한다() {
        //given
        Study study = StudyFixture.createChallengeStudy();
        ReflectionTestUtils.setField(study, "id", 1L);

        User user = UserFixture.createUser();
        ReflectionTestUtils.setField(user, "id", 1L);

        given(studyRepository.findById(any()))
                .willReturn(Optional.of(study));

        given(userRepository.findByStudyIdAndRole(any(), any()))
                .willReturn(Optional.of(user));

        given(userStudyRepository.existsByStudyIdAndUserId(any(), any()))
                .willReturn(false);

        // when
        GetStudyResponse response = studyInternalService.findStudyInfo(1L, 1L);

        // then
        assertThat(response.getIsJoined()).isFalse();
    }

    @Test
    public void 스터디_조회_시_리더가_아닌_경우_비밀번호로_null을_반환한다() {
        //given
        Long userId = 1L;
        Study study = StudyFixture.createNormalStudyWithPassword();
        ReflectionTestUtils.setField(study, "id", 1L);

        User leader = UserFixture.createUser();
        ReflectionTestUtils.setField(leader, "id", 2L);

        given(studyRepository.findById(any()))
                .willReturn(Optional.of(study));

        given(userRepository.findByStudyIdAndRole(any(), any()))
                .willReturn(Optional.of(leader));

        given(userStudyRepository.existsByStudyIdAndUserId(any(), any()))
                .willReturn(true);

        // when
        GetStudyResponse response = studyInternalService.findStudyInfo(1L, userId);

        // then
        assertThat(response.getStudyPassword()).isNull();
    }

    @Test
    public void 스터디_조회_시_리더인_경우_비밀번호를_반환한다() {
        //given
        Long userId = 1L;
        Study study = StudyFixture.createNormalStudyWithPassword();
        ReflectionTestUtils.setField(study, "id", 1L);

        User leader = UserFixture.createUser();
        ReflectionTestUtils.setField(leader, "id", 1L);

        given(studyRepository.findById(any()))
                .willReturn(Optional.of(study));

        given(userRepository.findByStudyIdAndRole(any(), any()))
                .willReturn(Optional.of(leader));

        given(userStudyRepository.existsByStudyIdAndUserId(any(), any()))
                .willReturn(true);

        // when
        GetStudyResponse response = studyInternalService.findStudyInfo(1L, 1L);

        // then
        assertThat(response.getStudyPassword()).isNotNull();
        assertThat(response.getStudyPassword()).isEqualTo("1234");
    }

    @Test
    public void 스터디_조회_시_챌린지_스터디인_경우_챌린지_성공_인원을_반환한다() {
        //given
        Study study = StudyFixture.createChallengeStudy();
        ReflectionTestUtils.setField(study, "id", 1L);

        User leader = UserFixture.createUser();
        ReflectionTestUtils.setField(leader, "id", 1L);

        given(studyRepository.findById(any()))
                .willReturn(Optional.of(study));

        given(userRepository.findByStudyIdAndRole(any(), any()))
                .willReturn(Optional.of(leader));

        given(userStudyRepository.existsByStudyIdAndUserId(any(), any()))
                .willReturn(true);

        given(dailyStudySummaryRepository.findAllByUserIdsAndDate(any(), any()))
                .willReturn(List.of());

        // when
        GetStudyResponse response = studyInternalService.findStudyInfo(1L, 1L);

        // then
        assertThat(response.getCompletedMemberCount()).isNotNull();
    }

    @Test
    public void 스터디_조회_시_비밀번호가_존재하는_경우_hasPassword값으로_true를_반환한다() {
        //given
        Study study = StudyFixture.createNormalStudyWithPassword();
        ReflectionTestUtils.setField(study, "id", 1L);

        User leader = UserFixture.createUser();
        ReflectionTestUtils.setField(leader, "id", 1L);

        given(studyRepository.findById(any()))
                .willReturn(Optional.of(study));

        given(userRepository.findByStudyIdAndRole(any(), any()))
                .willReturn(Optional.of(leader));

        given(userStudyRepository.existsByStudyIdAndUserId(any(), any()))
                .willReturn(true);

        // when
        GetStudyResponse response = studyInternalService.findStudyInfo(1L, 1L);

        // then
        assertThat(response.getHasPassword()).isTrue();
    }

    @Test
    public void 스터디_조회_시_비밀번호가_존재하지_않는_경우_hasPassword값으로_false를_반환한다() {
        //given
        Study study = StudyFixture.createNormalStudy();
        ReflectionTestUtils.setField(study, "id", 1L);

        User leader = UserFixture.createUser();
        ReflectionTestUtils.setField(leader, "id", 1L);

        given(studyRepository.findById(any()))
                .willReturn(Optional.of(study));

        given(userRepository.findByStudyIdAndRole(any(), any()))
                .willReturn(Optional.of(leader));

        given(userStudyRepository.existsByStudyIdAndUserId(any(), any()))
                .willReturn(true);

        // when
        GetStudyResponse response = studyInternalService.findStudyInfo(1L, 1L);

        // then
        assertThat(response.getHasPassword()).isFalse();
    }

    @Test
    public void 스터디_리더는_스터디를_삭제할_수_있다() {
        //given
        Long userId = 1L;
        Long studyId = 1L;

        UserStudy userStudy = UserStudyFixture.createLeaderUserStudy(userId, studyId);
        given(userStudyRepository.findByStudyIdAndUserId(any(), any()))
                .willReturn(Optional.of(userStudy));

        Study study = StudyFixture.createNormalStudy();
        ReflectionTestUtils.setField(study, "id", 1L);
        ReflectionTestUtils.setField(study, "imageUrl", "test");
        given(studyRepository.findById(any()))
                .willReturn(Optional.of(study));

        // when
        assertThatNoException()
                .isThrownBy(() -> studyInternalService.removeStudy(studyId, userId));

        // then
        verify(applicationEventPublisher).publishEvent(any(StudyImageRemovedEvent.class));
        verify(userStudyRepository).deleteAllByStudyId(studyId);
        verify(studyRepository).delete(study);
    }

    @Test
    public void 스터디_삭제_시_멤버인_아닌_경우_예외가_발생한다() {
        //given
        Long userId = 1L;
        Long studyId = 1L;

        UserStudy userStudy = UserStudyFixture.createMemberUserStudy(userId, studyId);
        given(userStudyRepository.findByStudyIdAndUserId(any(), any()))
                .willReturn(Optional.of(userStudy));

        // when & then
        assertThatThrownBy(() -> studyInternalService.removeStudy(studyId, userId))
                .isInstanceOf(StudyLeaderRequiredException.class);
    }

    @Test
    public void 스터디_삭제_시_스터디에_가입한_유저가_아닌_경우_예외가_발생한다() {
        //given
        Long userId = 1L;
        Long studyId = 1L;

        given(userStudyRepository.findByStudyIdAndUserId(any(), any()))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> studyInternalService.removeStudy(studyId, userId))
                .isInstanceOf(UserStudyNotFoundException.class);
    }

    @Test
    public void 스터디_삭제_시_스터디가_존재하지_않는_경우_예외가_발생한다() {
        //given
        Long userId = 1L;
        Long studyId = 1L;

        UserStudy userStudy = UserStudyFixture.createLeaderUserStudy(userId, studyId);
        given(userStudyRepository.findByStudyIdAndUserId(any(), any()))
                .willReturn(Optional.of(userStudy));

        given(studyRepository.findById(any()))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> studyInternalService.removeStudy(studyId, userId))
                .isInstanceOf(StudyNotFoundException.class);
    }

    @Test
    public void 스터디_정보_변경_시_스터디에_가입한_유저가_아닌_경우_예외가_발생한다() {
        //given
        Long userId = 1L;
        Long studyId = 1L;

        given(userStudyRepository.findByStudyIdAndUserId(any(), any()))
                .willReturn(Optional.empty());

        MultipartFile imageFile = mock(MultipartFile.class);

        PatchStudyInformationRequest request = new PatchStudyInformationRequest(
                "변경",
                "변경",
                StudyTag.FREE.getDescription(),
                imageFile,
                "1111",
                false
        );

        // when & then
        assertThatThrownBy(() -> studyInternalService.modifyStudyInformation(request, studyId, userId))
                .isInstanceOf(UserStudyNotFoundException.class);
    }

    @Test
    public void 스터디_정보_변경_시_멤버인_경우_예외가_발생한다() {
        //given
        Long userId = 1L;
        Long studyId = 1L;

        UserStudy userStudy = UserStudyFixture.createMemberUserStudy(userId, studyId);
        given(userStudyRepository.findByStudyIdAndUserId(any(), any()))
                .willReturn(Optional.of(userStudy));

        MultipartFile imageFile = mock(MultipartFile.class);

        PatchStudyInformationRequest request = new PatchStudyInformationRequest(
                "변경",
                "변경",
                StudyTag.FREE.getDescription(),
                imageFile,
                "1111",
                false
        );

        // when & then
        assertThatThrownBy(() -> studyInternalService.modifyStudyInformation(request, studyId, userId))
                .isInstanceOf(StudyLeaderRequiredException.class);
    }

    @Test
    public void 스터디_정보_변경_시_스터디가_존재하지_않는_경우_예외가_발생한다() {
        //given
        Long userId = 1L;
        Long studyId = 1L;

        UserStudy userStudy = UserStudyFixture.createLeaderUserStudy(userId, studyId);
        given(userStudyRepository.findByStudyIdAndUserId(any(), any()))
                .willReturn(Optional.of(userStudy));

        given(studyRepository.findById(any()))
                .willReturn(Optional.empty());

        MultipartFile imageFile = mock(MultipartFile.class);

        PatchStudyInformationRequest request = new PatchStudyInformationRequest(
                "변경",
                "변경",
                StudyTag.FREE.getDescription(),
                imageFile,
                "1111",
                false
        );

        // when & then
        assertThatThrownBy(() -> studyInternalService.modifyStudyInformation(request, studyId, userId))
                .isInstanceOf(StudyNotFoundException.class);
    }

    @Test
    public void 스터디_리더는_스터디_정보를_변경할_수_있다() {
        //given
        Long userId = 1L;
        Long studyId = 1L;

        UserStudy userStudy = UserStudyFixture.createLeaderUserStudy(userId, studyId);
        given(userStudyRepository.findByStudyIdAndUserId(any(), any()))
                .willReturn(Optional.of(userStudy));

        Study study = StudyFixture.createNormalStudy();
        ReflectionTestUtils.setField(study, "id", 1L);
        given(studyRepository.findById(any()))
                .willReturn(Optional.of(study));

        MultipartFile imageFile = mock(MultipartFile.class);

        PatchStudyInformationRequest request = new PatchStudyInformationRequest(
                "변경된 이름",
                "변경된 설명",
                StudyTag.FREE.getDescription(),
                imageFile,
                "1111",
                false
        );

        given(s3Service.uploadFile(any(), any(ImageCategory.class)))
                .willReturn("변경된 이미지 URL");

        // when
        assertThatNoException()
                .isThrownBy(() ->
                        studyInternalService.modifyStudyInformation(request, studyId, userId)
                );

        // then
        verify(studyRepository).save(study);

        assertThat(study.getName()).isEqualTo("변경된 이름");
        assertThat(study.getDescription()).isEqualTo("변경된 설명");
        assertThat(study.getTag()).isEqualTo(StudyTag.FREE);
        assertThat(study.getPassword()).isEqualTo("1111");
        assertThat(study.getImageUrl()).isEqualTo("변경된 이미지 URL");
    }

    @Test
    public void 스터디_리더는_스터디_최대_인원을_변경할_수_있다() {
        //given
        Long userId = 1L;
        Long studyId = 1L;

        Study study = StudyFixture.createNormalStudy();
        int changeMemberLimit = 20;
        UserStudy leaderUserStudy = UserStudyFixture.createLeaderUserStudy(1L, 1L);

        given(studyRepository.findById(any()))
                .willReturn(Optional.of(study));

        given(userStudyRepository.findByStudyIdAndUserId(any(), any()))
                .willReturn(Optional.of(leaderUserStudy));

        PatchStudyMemberLimitRequest request = new PatchStudyMemberLimitRequest(changeMemberLimit);

        // when
        studyInternalService.modifyStudyMemberLimit(request, studyId, userId);

        // then
        assertThat(study.getMemberLimit()).isEqualTo(changeMemberLimit);
    }

    @Test
    public void 스터디_최대_인원_변경_시_멤버인_경우_예외가_발생한다() {
        //given
        Long userId = 1L;
        Long studyId = 1L;

        UserStudy memberUserStudy = UserStudyFixture.createMemberUserStudy(1L, 1L);

        given(userStudyRepository.findByStudyIdAndUserId(any(), any()))
                .willReturn(Optional.of(memberUserStudy));

        PatchStudyMemberLimitRequest request = new PatchStudyMemberLimitRequest(1);

        // when & then
        assertThatThrownBy(() ->
                studyInternalService.modifyStudyMemberLimit(request, studyId, userId)
        ).isInstanceOf(StudyLeaderRequiredException.class);
    }

    @Test
    public void 스터디_최대_인원_변경_시_스터디가_존재하지_않는_경우_예외가_발생한다() {
        //given
        Long userId = 1L;
        Long studyId = 1L;

        UserStudy memberUserStudy = UserStudyFixture.createLeaderUserStudy(1L, 1L);

        given(userStudyRepository.findByStudyIdAndUserId(any(), any()))
                .willReturn(Optional.of(memberUserStudy));

        given(studyRepository.findById(any()))
                .willReturn(Optional.empty());

        PatchStudyMemberLimitRequest request = new PatchStudyMemberLimitRequest(1);

        // when & then
        assertThatThrownBy(() ->
                studyInternalService.modifyStudyMemberLimit(request, studyId, userId)
        ).isInstanceOf(StudyNotFoundException.class);
    }

    @Test
    public void 스터디에_멤버를_등록한다() {
        //given
        Long userId = 1L;
        Long studyId = 1L;

        Study study = StudyFixture.createNormalStudy();
        ReflectionTestUtils.setField(study, "id", studyId);

        PostStudyMemberRequest request = new PostStudyMemberRequest(null);

        given(studyRepository.findById(any()))
                .willReturn(Optional.of(study));

        // when
        studyInternalService.registerStudyMember(request, studyId, userId);

        // then
        verify(studyRepository).save(study);
        verify(userStudyRepository).save(
                argThat(userStudy ->
                        userStudy.getStudyId().equals(studyId) &&
                                userStudy.getUserId().equals(userId) &&
                                userStudy.getRole().equals(StudyRole.MEMBER)
                )
        );
    }

    @Test
    public void 스터디_멤버_등록_시_스터디가_존재하지_않는_경우_예외가_발생한다() {
        //given
        Long userId = 1L;
        Long studyId = 1L;

        PostStudyMemberRequest request = new PostStudyMemberRequest(null);

        given(studyRepository.findById(any()))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                studyInternalService.registerStudyMember(request, studyId, userId)
        ).isInstanceOf(StudyNotFoundException.class);
    }

    @Test
    public void 스터디에서_멤버가_퇴장한다() {
        //given
        Long userId = 1L;
        Long studyId = 1L;
        int memberCount = 10;

        UserStudy memberUserStudy = UserStudyFixture.createMemberUserStudy(userId, studyId);
        Study study = StudyFixture.createNormalStudy();
        ReflectionTestUtils.setField(study, "memberCount", memberCount);

        given(userStudyRepository.findByStudyIdAndUserId(any(), any()))
                .willReturn(Optional.of(memberUserStudy));

        given(studyRepository.findById(any()))
                .willReturn(Optional.of(study));

        // when
        studyInternalService.removeMyStudyMembership(studyId, userId);

        // then
        verify(studyRepository).save(
                argThat(updateStudy ->
                        updateStudy.getMemberCount() == memberCount - 1
                )
        );

        verify(userStudyRepository).delete(memberUserStudy);
    }

    @Test
    public void 스터디_퇴장_시_스터디에_가입하지_않은_경우_예외가_발생한다() {
        //given
        Long userId = 1L;
        Long studyId = 1L;
        int memberCount = 10;

        UserStudy memberUserStudy = UserStudyFixture.createMemberUserStudy(userId, studyId);

        given(userStudyRepository.findByStudyIdAndUserId(any(), any()))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                studyInternalService.removeMyStudyMembership(studyId, userId)
        ).isInstanceOf(UserStudyNotFoundException.class);
    }

    @Test
    public void 스터디_퇴장_시_스터디가_존재하지_않는_경우_예외가_발생한다() {
        //given
        Long userId = 1L;
        Long studyId = 1L;
        int memberCount = 10;

        UserStudy leaderUserStudy = UserStudyFixture.createLeaderUserStudy(userId, studyId);

        given(userStudyRepository.findByStudyIdAndUserId(any(), any()))
                .willReturn(Optional.of(leaderUserStudy));

        given(studyRepository.findById(any()))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                studyInternalService.removeMyStudyMembership(studyId, userId)
        ).isInstanceOf(StudyNotFoundException.class);
    }

    @Test
    public void 스터디_퇴장_시_리더인_경우_예외가_발생한다() {
        //given
        Long userId = 1L;
        Long studyId = 1L;
        int memberCount = 10;

        UserStudy leaderUserStudy = UserStudyFixture.createLeaderUserStudy(userId, studyId);
        Study study = StudyFixture.createNormalStudy();
        ReflectionTestUtils.setField(study, "memberCount", memberCount);

        given(userStudyRepository.findByStudyIdAndUserId(any(), any()))
                .willReturn(Optional.of(leaderUserStudy));

        given(studyRepository.findById(any()))
                .willReturn(Optional.of(study));

        // when & then
        assertThatThrownBy(() ->
                studyInternalService.removeMyStudyMembership(studyId, userId)
        ).isInstanceOf(StudyMemberRequiredException.class);
    }

    @Test
    public void 리더는_스터디에서_멤버를_추방할_수_있다() {
        //given
        Long leaderId = 1L;
        Long studyId = 1L;
        Long memberId = 2L;

        Study study = StudyFixture.createNormalStudy();
        int memberCount = 10;
        ReflectionTestUtils.setField(study, "memberCount", memberCount);

        UserStudy leaderUserStudy = UserStudyFixture.createLeaderUserStudy(leaderId, studyId);

        given(studyRepository.findById(any()))
                .willReturn(Optional.of(study));

        given(userStudyRepository.findByStudyIdAndUserId(studyId, leaderId))
                .willReturn(Optional.of(leaderUserStudy));

        // when
        studyInternalService.removeStudyMember(studyId, memberId, leaderId);

        // then
        verify(studyRepository).save(
                argThat(updateStudy ->
                        updateStudy.getMemberCount() == memberCount - 1
                )
        );

        verify(userStudyRepository).deleteByStudyIdAndUserId(studyId, memberId);
    }

    @Test
    public void 스터디에서_멤버_추방_시_스터디가_존재하지_않는_경우_예외가_발생한다() {
        //given
        Long leaderId = 1L;
        Long studyId = 1L;
        Long memberId = 1L;

        given(studyRepository.findById(any()))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                studyInternalService.removeStudyMember(studyId, memberId, leaderId)
        ).isInstanceOf(StudyNotFoundException.class);
    }

    @Test
    public void 스터디에서_멤버_추방_시_스터디에_가입하지_않은_경우_예외가_발생한다() {
        //given
        Long leaderId = 1L;
        Long studyId = 1L;
        Long memberId = 1L;

        Study study = StudyFixture.createNormalStudy();
        int memberCount = 10;
        ReflectionTestUtils.setField(study, "memberCount", memberCount);

        given(studyRepository.findById(any()))
                .willReturn(Optional.of(study));

        given(userStudyRepository.findByStudyIdAndUserId(studyId, leaderId))
                .willReturn(Optional.empty());

        // when
        assertThatThrownBy(() ->
                studyInternalService.removeStudyMember(studyId, memberId, leaderId)
        ).isInstanceOf(UserStudyNotFoundException.class);
    }

    @Test
    public void 스터디에서_멤버_추방_시_리더가_아닌_경우_예외가_발생한다() {
        //given
        Long leaderId = 1L;
        Long studyId = 1L;
        Long memberId = 1L;

        Study study = StudyFixture.createNormalStudy();
        int memberCount = 10;
        ReflectionTestUtils.setField(study, "memberCount", memberCount);

        UserStudy memberUserStudy = UserStudyFixture.createMemberUserStudy(leaderId, studyId);

        given(studyRepository.findById(any()))
                .willReturn(Optional.of(study));

        given(userStudyRepository.findByStudyIdAndUserId(studyId, leaderId))
                .willReturn(Optional.of(memberUserStudy));

        // when
        assertThatThrownBy(() ->
                studyInternalService.removeStudyMember(studyId, memberId, leaderId)
        ).isInstanceOf(StudyLeaderRequiredException.class);
    }

    @Test
    public void 스터디_리더_권한을_다른_멤버에게_양도한다() {
        //given
        Long studyId = 1L;
        Long leaderId = 1L;
        Long memberId = 2L;

        UserStudy leaderUserStudy = UserStudyFixture.createLeaderUserStudy(leaderId, studyId);
        UserStudy memberUserStudy = UserStudyFixture.createMemberUserStudy(memberId, studyId);

        given(userStudyRepository.findByStudyIdAndUserId(studyId, leaderId))
                .willReturn(Optional.of(leaderUserStudy));

        given(userStudyRepository.findByStudyIdAndUserId(studyId, memberId))
                .willReturn(Optional.of(memberUserStudy));

        // when
        studyInternalService.modifyStudyLeader(studyId, memberId, leaderId);

        // then
        assertThat(leaderUserStudy.getRole()).isEqualTo(StudyRole.MEMBER);
        assertThat(memberUserStudy.getRole()).isEqualTo(StudyRole.LEADER);
    }

    @Test
    public void 스터디_리더_권한_양도_시_리더의_스터디_정보가_존재하지_않는_경우_예외가_발생한다() {
        //given
        Long studyId = 1L;
        Long leaderId = 1L;
        Long memberId = 2L;

        given(userStudyRepository.findByStudyIdAndUserId(studyId, leaderId))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                studyInternalService.modifyStudyLeader(studyId, memberId, leaderId)
        ).isInstanceOf(UserStudyNotFoundException.class);
    }

    @Test
    public void 스터디_리더_권한_양도_시_멤버의_스터디_정보가_존재하지_않는_경우_예외가_발생한다() {
        //given
        Long studyId = 1L;
        Long leaderId = 1L;
        Long memberId = 2L;

        UserStudy leaderUserStudy = UserStudyFixture.createLeaderUserStudy(leaderId, studyId);

        given(userStudyRepository.findByStudyIdAndUserId(studyId, leaderId))
                .willReturn(Optional.of(leaderUserStudy));

        given(userStudyRepository.findByStudyIdAndUserId(studyId, memberId))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                studyInternalService.modifyStudyLeader(studyId, memberId, leaderId)
        ).isInstanceOf(UserStudyNotFoundException.class);
    }

    @Test
    public void 스터디의_멤버_목록을_조회한다() {
        //given
        Long userId = 1L;
        Long studyId = 1L;
        Long memberId = 2L;

        User user = UserFixture.createLeader();
        ReflectionTestUtils.setField(user, "id", userId);

        User member = UserFixture.createMember();
        ReflectionTestUtils.setField(member, "id", memberId);

        given(userRepository.findAllByStudyIdOrderByCreatedAtAsc(any()))
                .willReturn(
                        List.of(new StudyMemberRoleDto(user, StudyRole.MEMBER),
                                new StudyMemberRoleDto(member, StudyRole.MEMBER))
                );

        // when
        List<GetStudyMemberResponse> response = studyInternalService.findStudyMember(studyId, userId);

        // then
        assertThat(response).hasSize(2);
        assertThat(response)
                .extracting(GetStudyMemberResponse::getUserId)
                .containsExactlyInAnyOrder(userId, memberId);
    }

    @Test
    public void 스터디의_멤버_목록_조회_시_본인이_스터디에_가입한_경우_가장_먼저_반환한다() {
        //given
        Long userId = 1L;
        Long studyId = 1L;
        Long memberId = 2L;

        User user = UserFixture.createLeader();
        ReflectionTestUtils.setField(user, "id", userId);
        User member = UserFixture.createMember();
        ReflectionTestUtils.setField(member, "id", memberId);

        given(userRepository.findAllByStudyIdOrderByCreatedAtAsc(any()))
                .willReturn(
                        List.of(new StudyMemberRoleDto(user, StudyRole.MEMBER),
                                new StudyMemberRoleDto(member, StudyRole.MEMBER))
                );

        // when
        List<GetStudyMemberResponse> response = studyInternalService.findStudyMember(studyId, userId);

        // then
        assertThat(response.get(0).getUserId()).isEqualTo(userId);
    }

    @Test
    public void 스터디_멤버_관리_목록을_조회한다() {
        //given
        Long userId = 1L;
        Long studyId = 1L;
        Long memberId = 2L;

        User user = UserFixture.createLeader();
        ReflectionTestUtils.setField(user, "id", userId);
        User member = UserFixture.createMember();
        ReflectionTestUtils.setField(member, "id", memberId);

        given(userStudyRepository.existsByStudyIdAndUserId(studyId, userId))
                .willReturn(true);

        given(userStudyRepository.findStudyMembersByStudyId(any()))
                .willReturn(List.of(
                                new GetStudyMemberManagementResponse(
                                        user.getId(),
                                        user.getNickname(),
                                        user.getProfileImageUrl(),
                                        StudyRole.MEMBER
                                ),
                                new GetStudyMemberManagementResponse(
                                        member.getId(),
                                        member.getNickname(),
                                        member.getProfileImageUrl(),
                                        StudyRole.MEMBER
                                )
                        )
                );

        // when
        List<GetStudyMemberManagementResponse> response =
                studyInternalService.findStudyMemberManagement(studyId, userId);

        // then
        assertThat(response).hasSize(2);
        assertThat(response).extracting(GetStudyMemberManagementResponse::getUserId)
                .containsExactlyInAnyOrder(userId, memberId);
    }

    @Test
    public void 스터디_멤버_관리_목록을_조회_시_본인이_스터디에_가입한_경우_가장_먼저_반환한다() {
        //given
        Long userId = 1L;
        Long studyId = 1L;
        Long memberId = 2L;

        User user = UserFixture.createLeader();
        ReflectionTestUtils.setField(user, "id", userId);
        User member = UserFixture.createMember();
        ReflectionTestUtils.setField(member, "id", memberId);

        given(userStudyRepository.existsByStudyIdAndUserId(studyId, userId))
                .willReturn(true);

        given(userStudyRepository.findStudyMembersByStudyId(any()))
                .willReturn(List.of(
                                new GetStudyMemberManagementResponse(
                                        user.getId(),
                                        user.getNickname(),
                                        user.getProfileImageUrl(),
                                        StudyRole.MEMBER
                                ),
                                new GetStudyMemberManagementResponse(
                                        member.getId(),
                                        member.getNickname(),
                                        member.getProfileImageUrl(),
                                        StudyRole.MEMBER
                                )
                        )
                );

        // when
        List<GetStudyMemberManagementResponse> response =
                studyInternalService.findStudyMemberManagement(studyId, userId);

        // then
        assertThat(response.get(0).getUserId()).isEqualTo(userId);
    }

    @Test
    public void 스터디_멤버의_출석_여부_및_기간별_학습_시간을_조회한다() {
        //given
        Long userId = 1L;
        Long studyId = 1L;

        LocalDate startDate = LocalDate.of(2026, 1, 1);
        LocalDate endDate = LocalDate.of(2026, 1, 3);
        LocalDate date = startDate.plusDays(1);

        User user = UserFixture.createMember();
        ReflectionTestUtils.setField(user, "id", userId);

        DailyStudySummary dailyStudySummary = DailyStudySummaryFixture.createDailyStudySummaryWithDate(date);

        given(userRepository.findAllByStudyId(any()))
                .willReturn(List.of(user));

        given(dailyStudySummaryRepository.findDailyStudyTimeByUserIdsAndPeriod(any(), any(), any()))
                .willReturn(List.of(new DailyStudyTimeDto(userId, date, dailyStudySummary.getStudyTime())));

        // when
        List<GetStudyAttendanceResponse> responses = studyInternalService.findStudyAttendance(
                startDate,
                endDate,
                userId
        );

        // then
        assertThat(responses).hasSize(1);

        GetStudyAttendanceResponse response = responses.get(0);

        assertThat(response.getUserId()).isEqualTo(user.getId());
        assertThat(response.getStudyTimeList())
                .containsExactly(null, "03:00:00", null);
    }

    @Test
    public void 스터디_멤버의_출석_여부_및_기간별_학습_시간을_조회_시_총_학습_시간이_많은_멤버부터_반환한다() {
        //given
        Long userId = 1L;
        Long studyId = 1L;
        Long memberId = 2L;
        Long userStudyTime = 5 * 3600L;
        Long memberStudyTime = 3 * 3600L;

        LocalDate startDate = LocalDate.of(2026, 1, 1);
        LocalDate endDate = LocalDate.of(2026, 1, 3);
        LocalDate date = startDate.plusDays(1);

        User user = UserFixture.createMember();
        ReflectionTestUtils.setField(user, "id", userId);

        User member = UserFixture.createMember();
        ReflectionTestUtils.setField(member, "id", memberId);

        given(userRepository.findAllByStudyId(any()))
                .willReturn(List.of(user, member));

        given(dailyStudySummaryRepository.findDailyStudyTimeByUserIdsAndPeriod(any(), any(), any()))
                .willReturn(List.of(
                        new DailyStudyTimeDto(userId, date, userStudyTime),
                        new DailyStudyTimeDto(memberId, date, memberStudyTime))
                );

        // when
        List<GetStudyAttendanceResponse> responses = studyInternalService.findStudyAttendance(
                startDate,
                endDate,
                studyId
        );

        // then
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getUserId()).isEqualTo(userId);
    }
}
