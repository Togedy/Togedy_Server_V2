package com.togedy.togedy_server_v2.domain.study.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.togedy.togedy_server_v2.domain.planner.entity.DailyStudySummary;
import com.togedy.togedy_server_v2.domain.study.dto.GetMyStudyInfoResponse;
import com.togedy.togedy_server_v2.domain.study.dto.GetStudyNameDuplicateResponse;
import com.togedy.togedy_server_v2.domain.study.dto.GetStudySearchResponse;
import com.togedy.togedy_server_v2.domain.study.dto.PostStudyRequest;
import com.togedy.togedy_server_v2.domain.study.dto.StudyDto;
import com.togedy.togedy_server_v2.domain.study.dto.StudySearchDto;
import com.togedy.togedy_server_v2.domain.study.entity.Study;
import com.togedy.togedy_server_v2.domain.study.entity.UserStudy;
import com.togedy.togedy_server_v2.domain.study.enums.StudyRole;
import com.togedy.togedy_server_v2.domain.study.enums.StudyTag;
import com.togedy.togedy_server_v2.domain.study.enums.StudyType;
import com.togedy.togedy_server_v2.domain.user.entity.User;
import com.togedy.togedy_server_v2.global.fixtures.DailyStudySummaryFixture;
import com.togedy.togedy_server_v2.global.fixtures.StudyFixture;
import com.togedy.togedy_server_v2.global.fixtures.UserFixture;
import com.togedy.togedy_server_v2.global.fixtures.UserStudyFixture;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

public class StudyExternalServiceTest extends AbstractStudyServiceTest {

    @InjectMocks
    private StudyExternalService studyExternalService;

    @Test
    public void 스터디_생성_시_목표_시간이_존재하는_경우_챌린지_스터디로_타입을_설정한다() {
        //given
        Long userId = 1L;
        MultipartFile image = mock(MultipartFile.class);

        PostStudyRequest request = new PostStudyRequest(
                3,
                "챌린지 스터디",
                "",
                30,
                StudyTag.SCHOOL.getDescription(),
                image, ""
        );

        given(studyRepository.save(any())).willAnswer(invocationOnMock -> {
            Study study = invocationOnMock.getArgument(0);
            ReflectionTestUtils.setField(study, "id", 1L);
            return study;
        });

        // when
        studyExternalService.generateStudy(request, userId);

        // then
        verify(studyRepository).save(
                argThat(study -> study.getType() == StudyType.CHALLENGE && study.getGoalTime() == 3 * 3600L));
    }

    @Test
    public void 스터디_생성_시_목표_시간이_존재하지_않는_경우_일반_스터디로_타입을_설정한다() {
        //given
        Long userId = 1L;
        MultipartFile image = mock(MultipartFile.class);

        PostStudyRequest request = new PostStudyRequest(
                null,
                "챌린지 스터디",
                "",
                30,
                StudyTag.SCHOOL.getDescription(),
                image,
                ""
        );

        given(studyRepository.save(any())).willAnswer(invocationOnMock -> {
            Study study = invocationOnMock.getArgument(0);
            ReflectionTestUtils.setField(study, "id", 1L);
            return study;
        });

        // when
        studyExternalService.generateStudy(request, userId);

        // then
        verify(studyRepository).save(
                argThat(study -> study.getType() == StudyType.NORMAL && study.getGoalTime() == null));
    }

    @Test
    public void 스터디를_생성한_유저는_리더로_설정된다() {
        //given
        Long userId = 1L;
        MultipartFile image = mock(MultipartFile.class);

        PostStudyRequest request = new PostStudyRequest(
                3,
                "챌린지 스터디",
                "",
                30,
                StudyTag.SCHOOL.getDescription(),
                image, ""
        );

        given(studyRepository.save(any())).willAnswer(invocationOnMock -> {
            Study study = invocationOnMock.getArgument(0);
            ReflectionTestUtils.setField(study, "id", 1L);
            return study;
        });

        // when
        studyExternalService.generateStudy(request, userId);

        // then
        verify(userStudyRepository).save(
                argThat(userStudy -> userStudy.getUserId().equals(userId) && userStudy.getRole() == StudyRole.LEADER));
    }

    @Test
    public void 스터디_생성_시_이미지_파일이_존재하는_경우_URL로_변환하여_저장한다() {
        //given
        Long userId = 1L;
        MultipartFile image = mock(MultipartFile.class);

        PostStudyRequest request = new PostStudyRequest(
                3,
                "챌린지 스터디",
                "",
                30,
                StudyTag.SCHOOL.getDescription(),
                image, ""
        );

        given(studyRepository.save(any())).willAnswer(invocationOnMock -> {
            Study study = invocationOnMock.getArgument(0);
            ReflectionTestUtils.setField(study, "id", 1L);
            ReflectionTestUtils.setField(study, "imageUrl", "imageUrl");
            return study;
        });

        // when
        studyExternalService.generateStudy(request, userId);

        // then
        verify(studyRepository).save(
                argThat(study -> study.getImageUrl().equals("imageUrl")));
    }

    @Test
    public void 중복되는_이름의_스터디의_존재_여부를_조회한다() {
        //given
        Study study = StudyFixture.createChallengeStudy();
        given(studyRepository.existsByName(study.getName()))
                .willReturn(true);

        // when
        GetStudyNameDuplicateResponse response = studyExternalService.findStudyNameDuplicate(study.getName());

        // then
        assertThat(response.getIsDuplicate()).isTrue();
    }

    @Test
    public void 본인_스터디_조회_시_오늘의_공부_기록이_존재하지_않는_경우_공부_시간은_00시_00분_00초를_반환한다() {
        //given
        Long userId = 1L;
        LocalDate today = LocalDate.now();

        given(studyRepository.findAllByUserIdOrderByCreatedAtAsc(userId))
                .willReturn(List.of());

        given(dailyStudySummaryRepository.findByUserIdAndDate(userId, today))
                .willReturn(Optional.empty());

        // when
        GetMyStudyInfoResponse response = studyExternalService.findMyStudyInfo(userId);

        // then
        assertThat(response.getStudyTime()).isEqualTo("00:00:00");
        assertThat(response.getStudyList()).isEmpty();
    }

    @Test
    public void 본인_스터디_조회_시_오늘의_공부_기록이_존재하는_경우_hh_mm_ss_형식으로_반환한다() {
        //given
        Long userId = 1L;
        LocalDate today = LocalDate.now();

        given(studyRepository.findAllByUserIdOrderByCreatedAtAsc(userId))
                .willReturn(List.of());

        given(dailyStudySummaryRepository.findByUserIdAndDate(1L, today))
                .willReturn(Optional.of(DailyStudySummaryFixture.createDailyStudySummaryWithStudyTime(3 * 3600L)));

        // when
        GetMyStudyInfoResponse response = studyExternalService.findMyStudyInfo(userId);

        // then
        assertThat(response.getStudyTime()).isEqualTo("03:00:00");
    }

    @Test
    public void 본인_스터디_조회_시_입장한_스터디가_없는_경우_빈_리스트를_반환한다() {
        //given
        Long userId = 1L;
        LocalDate today = LocalDate.now();

        given(studyRepository.findAllByUserIdOrderByCreatedAtAsc(userId))
                .willReturn(List.of());

        // when
        GetMyStudyInfoResponse response = studyExternalService.findMyStudyInfo(userId);

        // then
        assertThat(response.getStudyList())
                .isEqualTo(List.of());
    }

    @Test
    public void 본인_스터디_조회_시_일반_스터디에만_가입한_경우_챌린지_관련_응답을_null로_반환한다() {
        // given
        Long userId = 1L;
        Long studyId = 1L;
        LocalDate today = LocalDate.now();

        Study study = StudyFixture.createNormalStudy();
        ReflectionTestUtils.setField(study, "id", studyId);
        ReflectionTestUtils.setField(study, "memberCount", 1);

        given(studyRepository.findAllByUserIdOrderByCreatedAtAsc(userId))
                .willReturn(List.of(study));

        given(dailyStudySummaryRepository.findByUserIdAndDate(userId, today))
                .willReturn(Optional.empty());

        UserStudy userStudy = UserStudyFixture.createLeaderUserStudy(userId, studyId);

        given(userStudyRepository.findAllByStudyIds(List.of(studyId)))
                .willReturn(List.of(userStudy));

        User user = UserFixture.createUser();
        ReflectionTestUtils.setField(user, "id", userId);

        given(userRepository.findAllById(List.of(userId)))
                .willReturn(List.of(user));

        given(dailyStudySummaryRepository.findAllByUserIdsAndDate(List.of(userId), today))
                .willReturn(List.of());

        // when
        GetMyStudyInfoResponse response = studyExternalService.findMyStudyInfo(userId);

        // then
        assertThat(response.getStudyList()).hasSize(1);

        StudyDto studyDto = response.getStudyList().get(0);

        assertThat(studyDto.getStudyType()).isEqualTo(StudyType.NORMAL);
        assertThat(studyDto.getChallengeAchievement()).isNull();
        assertThat(studyDto.getChallengeGoalTime()).isNull();
        assertThat(studyDto.getCompletedMemberCount()).isNull();
    }

    @Test
    public void 본인_스터디_조회_시_챌린지_스터디에_가입한_경우_챌린지_관련_응답을_반환한다() {
        // given
        Long userId = 1L;
        Long studyId = 1L;
        LocalDate today = LocalDate.now();

        Study study = StudyFixture.createChallengeStudy();
        ReflectionTestUtils.setField(study, "id", studyId);
        ReflectionTestUtils.setField(study, "memberCount", 1);

        given(studyRepository.findAllByUserIdOrderByCreatedAtAsc(userId))
                .willReturn(List.of(study));

        given(dailyStudySummaryRepository.findByUserIdAndDate(userId, today))
                .willReturn(Optional.empty());

        UserStudy userStudy = UserStudyFixture.createLeaderUserStudy(userId, studyId);

        given(userStudyRepository.findAllByStudyIds(List.of(studyId)))
                .willReturn(List.of(userStudy));

        User user = UserFixture.createUser();
        ReflectionTestUtils.setField(user, "id", userId);

        given(userRepository.findAllById(List.of(userId)))
                .willReturn(List.of(user));

        given(dailyStudySummaryRepository.findAllByUserIdsAndDate(List.of(userId), today))
                .willReturn(List.of());

        // when
        GetMyStudyInfoResponse response = studyExternalService.findMyStudyInfo(userId);

        // then
        assertThat(response.getStudyList()).hasSize(1);

        StudyDto studyDto = response.getStudyList().get(0);

        assertThat(studyDto.getStudyType()).isEqualTo(StudyType.CHALLENGE);
        assertThat(studyDto.getChallengeAchievement()).isNotNull();
        assertThat(studyDto.getChallengeGoalTime()).isNotNull();
        assertThat(studyDto.getCompletedMemberCount()).isNotNull();
    }

    @Test
    public void 본인_스터디_조회_시_챌린지_스터디에_가입한_경우_챌린지_달성도를_계산한다() {
        // given
        Long userId = 1L;
        Long studyId = 1L;
        Long dailyStudySummaryId = 1L;
        LocalDate today = LocalDate.now();

        Study study = StudyFixture.createChallengeStudy();
        ReflectionTestUtils.setField(study, "id", studyId);
        ReflectionTestUtils.setField(study, "memberCount", 1);

        given(studyRepository.findAllByUserIdOrderByCreatedAtAsc(userId))
                .willReturn(List.of(study));

        DailyStudySummary dailyStudySummary = DailyStudySummaryFixture.createDailyStudySummaryWithStudyTime(3 * 3600L);
        ReflectionTestUtils.setField(dailyStudySummary, "id", dailyStudySummaryId);

        given(dailyStudySummaryRepository.findByUserIdAndDate(userId, today))
                .willReturn(Optional.of(dailyStudySummary));

        UserStudy userStudy = UserStudyFixture.createLeaderUserStudy(userId, studyId);

        given(userStudyRepository.findAllByStudyIds(List.of(studyId)))
                .willReturn(List.of(userStudy));

        User user = UserFixture.createUser();
        ReflectionTestUtils.setField(user, "id", userId);

        given(userRepository.findAllById(List.of(userId)))
                .willReturn(List.of(user));

        given(dailyStudySummaryRepository.findAllByUserIdsAndDate(List.of(userId), today))
                .willReturn(List.of(dailyStudySummary));

        // when
        GetMyStudyInfoResponse response = studyExternalService.findMyStudyInfo(userId);

        // then
        assertThat(response.getStudyList().get(0).getChallengeAchievement()).isEqualTo(100);
    }


    @Test
    public void 본인_스터디_조회_시_챌린지_스터디에_가입한_경우_각_스터디의_챌린지_성공_인원을_집계한다() {
        // given
        Long userId = 1L;
        Long studyId = 1L;
        Long dailyStudySummaryId = 1L;
        LocalDate today = LocalDate.now();

        Study study = StudyFixture.createChallengeStudy();
        ReflectionTestUtils.setField(study, "id", studyId);
        ReflectionTestUtils.setField(study, "memberCount", 1);

        given(studyRepository.findAllByUserIdOrderByCreatedAtAsc(userId))
                .willReturn(List.of(study));

        DailyStudySummary dailyStudySummary = DailyStudySummaryFixture.createDailyStudySummaryWithStudyTime(3 * 3600L);
        ReflectionTestUtils.setField(dailyStudySummary, "id", dailyStudySummaryId);

        given(dailyStudySummaryRepository.findByUserIdAndDate(userId, today))
                .willReturn(Optional.of(dailyStudySummary));

        UserStudy userStudy = UserStudyFixture.createLeaderUserStudy(userId, studyId);

        given(userStudyRepository.findAllByStudyIds(List.of(studyId)))
                .willReturn(List.of(userStudy));

        User user = UserFixture.createUser();
        ReflectionTestUtils.setField(user, "id", userId);

        given(userRepository.findAllById(List.of(userId)))
                .willReturn(List.of(user));

        given(dailyStudySummaryRepository.findAllByUserIdsAndDate(List.of(userId), today))
                .willReturn(List.of(dailyStudySummary));

        // when
        GetMyStudyInfoResponse response = studyExternalService.findMyStudyInfo(userId);

        // then
        assertThat(response.getStudyList().get(0).getCompletedMemberCount()).isEqualTo(1);
    }

    @Test
    public void 스터디_검색_시_태그를_입력하지_않은_경우_태그는_필터링하지_않는다() {
        //given
        Slice<Study> emptySlice = new SliceImpl<>(List.of(), PageRequest.of(0, 20), false);

        given(studyRepository.findStudiesWithoutTags(any(), any(), anyBoolean(), anyBoolean(), any(PageRequest.class)))
                .willReturn(emptySlice);

        // when
        GetStudySearchResponse response = studyExternalService.findStudySearch(
                null,
                List.of(),
                null,
                false,
                false,
                1,
                20
        );

        // then
        verify(studyRepository, never())
                .findStudiesWithTags(any(), anyList(), any(), anyBoolean(), anyBoolean(), any(PageRequest.class));

        verify(studyRepository, times(1))
                .findStudiesWithoutTags(any(), any(), anyBoolean(), anyBoolean(), any(PageRequest.class));
    }

    @Test
    public void 스터디_검색_시_태그를_입력한_경우_태그를_필터링한다() {
        //given
        Study study = StudyFixture.createChallengeStudyWithTag(StudyTag.SCHOOL);
        ReflectionTestUtils.setField(study, "id", 1L);
        ReflectionTestUtils.setField(study, "createdAt", LocalDateTime.now());

        UserStudy userStudy = UserStudyFixture.createLeaderUserStudy(1L, 1L);

        User user = UserFixture.createLeader();
        ReflectionTestUtils.setField(user, "id", 1L);

        Slice<Study> slice = new SliceImpl<>(List.of(study), PageRequest.of(0, 20), false);
        List<StudyTag> tags = List.of(StudyTag.SCHOOL);

        given(userStudyRepository.findAllByStudyIds(List.of(1L)))
                .willReturn(List.of(userStudy));

        given(userRepository.findAllById(List.of(1L)))
                .willReturn(List.of(user));

        given(studyRepository.findStudiesWithTags(
                any(),
                anyList(),
                any(),
                anyBoolean(),
                anyBoolean(),
                any(PageRequest.class))
        ).willReturn(slice);

        // when
        GetStudySearchResponse response = studyExternalService.findStudySearch(
                null,
                List.of(StudyTag.SCHOOL.getDescription()),
                null,
                false,
                false,
                1,
                20
        );

        // then
        verify(studyRepository, never())
                .findStudiesWithoutTags(any(), any(), anyBoolean(), anyBoolean(), any(PageRequest.class));

        verify(studyRepository, times(1))
                .findStudiesWithTags(any(), anyList(), any(), anyBoolean(), anyBoolean(), any(PageRequest.class));

        assertThat(response.getStudyList()).hasSize(1);
        assertThat(response.getStudyList().get(0).getStudyTag()).isEqualTo(StudyTag.SCHOOL.getDescription());
    }

    @Test
    public void 스터디_검색_시_검색_결과가_존재하지_않는_경우_빈_Slice를_반환한다() {
        //given
        Slice<Study> emptySlice = new SliceImpl<>(List.of(), PageRequest.of(0, 20), false);
        given(studyRepository.findStudiesWithoutTags(any(), any(), anyBoolean(), anyBoolean(), any(PageRequest.class)))
                .willReturn(emptySlice);

        // when
        GetStudySearchResponse response = studyExternalService.findStudySearch(
                null,
                List.of(),
                null,
                false,
                false,
                1,
                20
        );

        // then
        assertThat(response.getStudyList()).isEmpty();
        assertThat(response.getHasNext()).isFalse();
    }

    @Test
    public void 인기_스터디_검색_시_검색_결과가_존재하지_않는_경우_빈_List를_반환한다() {
        //given
        given(studyRepository.findMostActiveStudies(any(PageRequest.class)))
                .willReturn(List.of());

        // when
        List<StudySearchDto> response = studyExternalService.findPopularStudies();

        // then
        assertThat(response).hasSize(0);
    }

}
