package com.togedy.togedy_server_v2.domain.user.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.togedy.togedy_server_v2.domain.chat.dao.ChatMessageRepository;
import com.togedy.togedy_server_v2.domain.planner.dao.DailyStudySummaryRepository;
import com.togedy.togedy_server_v2.domain.planner.dao.PlannerDailyImageRepository;
import com.togedy.togedy_server_v2.domain.planner.dao.StudySubjectRepository;
import com.togedy.togedy_server_v2.domain.planner.dao.StudyTaskRepository;
import com.togedy.togedy_server_v2.domain.planner.dao.StudyTimeRepository;
import com.togedy.togedy_server_v2.domain.schedule.dao.CategoryRepository;
import com.togedy.togedy_server_v2.domain.schedule.dao.UserScheduleRepository;
import com.togedy.togedy_server_v2.domain.study.dao.StudyRepository;
import com.togedy.togedy_server_v2.domain.study.dao.UserStudyRepository;
import com.togedy.togedy_server_v2.domain.university.dao.UserUniversityMethodRepository;
import com.togedy.togedy_server_v2.domain.user.dao.AuthProviderRepository;
import com.togedy.togedy_server_v2.domain.user.dao.RefreshTokenRepository;
import com.togedy.togedy_server_v2.domain.user.dao.UserRepository;
import com.togedy.togedy_server_v2.domain.user.dto.GetMyStatusResponse;
import com.togedy.togedy_server_v2.domain.user.entity.User;
import com.togedy.togedy_server_v2.global.infrastructure.kakao.KakaoApiClient;
import com.togedy.togedy_server_v2.global.service.S3Service;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.support.TransactionTemplate;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private S3Service s3Service;

    @Mock
    private KakaoApiClient kakaoApiClient;

    @Mock
    private UserRepository userRepository;

    @Mock
    private StudyRepository studyRepository;

    @Mock
    private UserStudyRepository userStudyRepository;

    @Mock
    private AuthProviderRepository authProviderRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private DailyStudySummaryRepository dailyStudySummaryRepository;

    @Mock
    private UserScheduleRepository userScheduleRepository;

    @Mock
    private UserUniversityMethodRepository userUniversityMethodRepository;

    @Mock
    private StudyTimeRepository studyTimeRepository;

    @Mock
    private PlannerDailyImageRepository plannerDailyImageRepository;

    @Mock
    private StudySubjectRepository studySubjectRepository;

    @Mock
    private StudyTaskRepository studyTaskRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Mock
    private TransactionTemplate transactionTemplate;

    @InjectMocks
    private UserService userService;

    @Test
    void 내_상태를_조회하면_온보딩_완료_여부를_반환한다() {
        Long userId = 1L;
        User user = User.createTemp("temp@test.com");
        ReflectionTestUtils.setField(user, "id", userId);
        ReflectionTestUtils.setField(user, "profileCompleted", false);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        GetMyStatusResponse response = userService.findMyStatus(userId);

        assertThat(response.getProfileCompleted()).isFalse();
    }
}
