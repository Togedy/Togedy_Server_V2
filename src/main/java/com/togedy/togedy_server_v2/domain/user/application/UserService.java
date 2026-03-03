package com.togedy.togedy_server_v2.domain.user.application;

import com.togedy.togedy_server_v2.domain.planner.dao.DailyStudySummaryRepository;
import com.togedy.togedy_server_v2.domain.planner.entity.DailyStudySummary;
import com.togedy.togedy_server_v2.domain.study.dao.StudyRepository;
import com.togedy.togedy_server_v2.domain.study.dao.UserStudyRepository;
import com.togedy.togedy_server_v2.domain.study.entity.Study;
import com.togedy.togedy_server_v2.domain.study.entity.UserStudy;
import com.togedy.togedy_server_v2.domain.user.dao.AuthProviderRepository;
import com.togedy.togedy_server_v2.domain.user.dao.UserRepository;
import com.togedy.togedy_server_v2.domain.user.dto.CreateUserRequest;
import com.togedy.togedy_server_v2.domain.user.dto.GetMyPageResponse;
import com.togedy.togedy_server_v2.domain.user.dto.GetMySettingsResponse;
import com.togedy.togedy_server_v2.domain.user.dto.MyPageStudyDto;
import com.togedy.togedy_server_v2.domain.user.dto.PatchMarketingConsentedSettingRequest;
import com.togedy.togedy_server_v2.domain.user.dto.PatchNicknameRequest;
import com.togedy.togedy_server_v2.domain.user.dto.PatchProfileImageRequest;
import com.togedy.togedy_server_v2.domain.user.dto.PatchPushNotificationSettingRequest;
import com.togedy.togedy_server_v2.domain.user.entity.AuthProvider;
import com.togedy.togedy_server_v2.domain.user.entity.User;
import com.togedy.togedy_server_v2.domain.user.event.UserProfileImageRemovedEvent;
import com.togedy.togedy_server_v2.domain.user.exception.user.DuplicateEmailException;
import com.togedy.togedy_server_v2.domain.user.exception.user.DuplicateNicknameException;
import com.togedy.togedy_server_v2.domain.user.exception.user.UserNotFoundException;
import com.togedy.togedy_server_v2.global.enums.ImageCategory;
import com.togedy.togedy_server_v2.global.service.S3Service;
import com.togedy.togedy_server_v2.global.util.TimeUtil;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final S3Service s3Service;
    private final UserRepository userRepository;
    private final StudyRepository studyRepository;
    private final UserStudyRepository userStudyRepository;
    private final AuthProviderRepository authProviderRepository;
    private final DailyStudySummaryRepository dailyStudySummaryRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    private final static int MY_PAGE_STUDY_COUNT = 2;

    @Transactional
    public Long generateUser(CreateUserRequest request) {
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new DuplicateNicknameException();
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException();
        }

        User user = User.create(request.getNickname(), request.getEmail());
        userRepository.save(user);

        authProviderRepository.save(AuthProvider.local(user));

        return user.getId();
    }

    @Transactional(readOnly = true)
    public User loadUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }

    /**
     * 마이페이지 정보를 조회한다.
     * <p>
     * 사용자 기본 정보, 누적 공부 시간, 최근 참여 스터디 목록을 함께 반환한다. 누적 공부 시간은 전체 기간 기준으로 계산되며, 시간 형식(HH:mm:ss)으로 변환되어 제공된다.
     * </p>
     *
     * @param userId 조회 대상 사용자 ID
     * @return 마이페이지 조회 응답 DTO
     * @throws UserNotFoundException 사용자가 존재하지 않는 경우
     */
    public GetMyPageResponse findMyPage(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Long totalStudyTime = dailyStudySummaryRepository.findTotalStudyTimeByUserId(userId).orElse(0L);
        List<MyPageStudyDto> studyDtos = findMyPageStudyDtos(userId);
        return GetMyPageResponse.from(user, TimeUtil.formatSecondsToHms(totalStudyTime), studyDtos);
    }

    /**
     * 사용자 설정 정보를 조회한다.
     * <p>
     * 푸시 알림 수신 여부, 마케팅 수신 동의 여부 등 계정 설정과 관련된 정보를 반환한다.
     * </p>
     *
     * @param userId 조회 대상 사용자 ID
     * @return 사용자 설정 조회 응답 DTO
     * @throws UserNotFoundException 사용자가 존재하지 않는 경우
     */
    public GetMySettingsResponse findMySettings(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        return GetMySettingsResponse.from(user);
    }

    /**
     * 푸시 알림 수신 설정을 변경한다.
     * <p>
     * 요청된 값에 따라 사용자의 푸시 알림 수신 여부를 수정한다. 트랜잭션 내에서 도메인 객체의 상태를 변경하며, 별도의 반환 값은 없다.
     * </p>
     *
     * @param request 푸시 알림 설정 변경 요청 DTO
     * @param userId  설정을 변경할 사용자 ID
     * @throws UserNotFoundException 사용자가 존재하지 않는 경우
     */
    @Transactional
    public void modifyPushNotificationSetting(PatchPushNotificationSettingRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        user.changePushNotificationEnabled(request.getPushNotificationEnabled());
    }

    /**
     * 마케팅 수신 동의 설정을 변경한다.
     * <p>
     * 요청된 값에 따라 사용자의 마케팅 정보 수신 동의 여부를 수정한다. 트랜잭션 내에서 도메인 객체의 상태를 변경하며, 별도의 반환 값은 없다.
     * </p>
     *
     * @param request 마케팅 수신 동의 설정 변경 요청 DTO
     * @param userId  설정을 변경할 사용자 ID
     * @throws UserNotFoundException 사용자가 존재하지 않는 경우
     */
    @Transactional
    public void modifyMarketingConsentedSetting(PatchMarketingConsentedSettingRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        user.changeMarketingConsented(request.getMarketingConsented());
    }

    /**
     * 사용자의 닉네임을 변경한다.
     * <p>
     * 요청된 닉네임으로 사용자 정보를 수정한다. 닉네임 유효성 검증은 도메인 객체 내부에서 수행된다.
     * </p>
     *
     * @param request 닉네임 변경 요청 DTO
     * @param userId  닉네임을 변경할 사용자 ID
     * @throws UserNotFoundException 사용자가 존재하지 않는 경우
     */
    @Transactional
    public void modifyNickname(PatchNicknameRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        user.changeNickname(request.getNickname());
    }

    /**
     * 사용자의 프로필 이미지를 변경한다.
     * <p>
     * 요청 값에 따라 프로필 이미지를 새로 업로드하거나 제거한다. 기존 이미지가 존재하는 경우 이벤트를 발행하여 비동기적으로 삭제 처리한다.
     * </p>
     *
     * @param request 프로필 이미지 변경 요청 DTO
     * @param userId  프로필 이미지를 변경할 사용자 ID
     * @throws UserNotFoundException 사용자가 존재하지 않는 경우
     */
    @Transactional
    public void modifyProfileImage(PatchProfileImageRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        replaceUserProfileImage(request, user);
    }

    /**
     * 단일 스터디 정보를 마이페이지용 DTO로 변환한다.
     * <p>
     * 챌린지 스터디인 경우, 오늘 기준 목표 달성 인원 수를 계산하여 챌린지 성공 여부와 함께 반환한다. 일반 스터디인 경우 기본 정보만 반환한다.
     * </p>
     *
     * @param study           변환 대상 스터디
     * @param userStudyMap    studyId 기준 스터디 참여자 매핑 정보
     * @param studySummaryMap userId 기준 오늘 공부 시간 매핑 정보
     * @return 마이페이지 스터디 DTO
     */
    private MyPageStudyDto buildMyPageStudyDto(
            Study study,
            Map<Long, List<UserStudy>> userStudyMap,
            Map<Long, Long> studySummaryMap
    ) {
        if (study.isChallengeStudy()) {
            List<UserStudy> userStudies = userStudyMap.getOrDefault(study.getId(), List.of());

            int completedMemberCount = calculateCompletedMember(study, studySummaryMap, userStudies);

            return MyPageStudyDto.from(
                    study,
                    study.isChallengeSuccess(completedMemberCount),
                    completedMemberCount
            );
        }

        return MyPageStudyDto.from(study);
    }

    /**
     * 챌린지 스터디에서 목표를 달성한 멤버 수를 계산한다.
     * <p>
     * 각 참여자의 오늘 공부 시간을 조회하여, 스터디 목표 시간 이상 달성한 인원 수를 반환한다.
     * </p>
     *
     * @param study           대상 스터디
     * @param studySummaryMap userId 기준 오늘 공부 시간 매핑 정보
     * @param userStudies     해당 스터디 참여자 목록
     * @return 목표 달성 멤버 수
     */
    private int calculateCompletedMember(Study study, Map<Long, Long> studySummaryMap, List<UserStudy> userStudies) {
        int completedMemberCount = 0;

        for (UserStudy userStudy : userStudies) {
            Long studyTime = studySummaryMap.getOrDefault(userStudy.getUserId(), 0L);

            if (study.isAchieved(studyTime)) {
                completedMemberCount++;
            }
        }

        return completedMemberCount;
    }

    /**
     * 프로필 이미지 교체 로직을 수행한다.
     * <p>
     * 요청 값에 따라 새 이미지 URL을 결정하고, 기존 이미지가 존재하는 경우 삭제 이벤트를 발행한다.
     * </p>
     *
     * @param request 프로필 이미지 변경 요청 DTO
     * @param user    대상 사용자
     */
    private void replaceUserProfileImage(PatchProfileImageRequest request, User user) {
        String newImageUrl = resolveNewProfileImageUrl(request);
        String oldImageUrl = user.changeProfileImageUrl(newImageUrl);
        publishImageRemovedEvent(oldImageUrl);
    }

    /**
     * 요청 값에 따라 새 프로필 이미지 URL을 결정한다.
     * <p>
     * 이미지 제거 요청인 경우 {@code null}을 반환하며, 그렇지 않은 경우 S3에 이미지를 업로드하고 URL을 반환한다.
     * </p>
     *
     * @param request 프로필 이미지 변경 요청 DTO
     * @return 새 프로필 이미지 URL, 제거 요청인 경우 {@code null}
     */
    private String resolveNewProfileImageUrl(PatchProfileImageRequest request) {
        if (Boolean.TRUE.equals(request.getRemoveUserProfileImage())) {
            return null;
        }

        return s3Service.uploadFile(request.getUserProfileImage(), ImageCategory.PROFILE);
    }

    /**
     * 기존 프로필 이미지 삭제 이벤트를 발행한다.
     * <p>
     * 이미지 URL이 존재하는 경우에만 이벤트를 발행하며, 실제 파일 삭제는 이벤트 리스너에서 처리된다.
     * </p>
     *
     * @param imageUrl 삭제 대상 이미지 URL
     */
    private void publishImageRemovedEvent(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return;
        }

        applicationEventPublisher.publishEvent(new UserProfileImageRemovedEvent(imageUrl));
    }


    /**
     * 마이페이지에 표시할 최근 스터디 목록을 조회하고 DTO로 변환한다.
     * <p>
     * 최근 참여 스터디를 조회한 뒤, 스터디 참여 정보 및 오늘 공부 시간 정보를 결합하여 마이페이지용 스터디 DTO 목록을 생성한다.
     * </p>
     *
     * @param userId 조회 대상 사용자 ID
     * @return 마이페이지 스터디 DTO 목록
     */
    private List<MyPageStudyDto> findMyPageStudyDtos(Long userId) {
        Pageable pageable = PageRequest.of(0, MY_PAGE_STUDY_COUNT);
        List<Study> studies = studyRepository.findRecentStudiesByUserId(userId, pageable);
        Map<Long, List<UserStudy>> userStudyMap = groupUserStudiesByStudyId(studies);
        Map<Long, Long> studySummaryMap = findTodayStudyTimeByUserIds(studies, userStudyMap);

        return studies.stream()
                .map(study -> buildMyPageStudyDto(study, userStudyMap, studySummaryMap))
                .toList();
    }

    /**
     * 스터디 ID를 기준으로 스터디 참여자 목록을 그룹화한다.
     * <p>
     * 주어진 스터디 목록에 속한 모든 참여 정보를 조회한 뒤, studyId를 키로 하여 {@code Map<Long, List<UserStudy>>} 형태로 반환한다.
     * </p>
     *
     * @param studies 스터디 목록
     * @return studyId 기준 참여자 매핑 정보
     */
    private Map<Long, List<UserStudy>> groupUserStudiesByStudyId(List<Study> studies) {
        List<Long> studyIds = studies.stream()
                .map(Study::getId)
                .toList();

        List<UserStudy> userStudies = userStudyRepository.findAllByStudyIds(studyIds);

        return userStudies.stream()
                .collect(Collectors.groupingBy(UserStudy::getStudyId));
    }

    /**
     * 챌린지 스터디 참여자들의 오늘 공부 시간을 조회한다.
     * <p>
     * 챌린지 스터디에 참여 중인 사용자 ID를 추출한 뒤, 오늘 날짜 기준 공부 시간을 조회하여 userId → studyTime 형태의 매핑으로 반환한다.
     * </p>
     *
     * @param studies      스터디 목록
     * @param userStudyMap studyId 기준 참여자 매핑 정보
     * @return userId 기준 오늘 공부 시간 매핑 정보
     */
    private Map<Long, Long> findTodayStudyTimeByUserIds(List<Study> studies, Map<Long, List<UserStudy>> userStudyMap) {
        List<Long> challengeUserIds = findChallengeUserIds(studies, userStudyMap);

        return dailyStudySummaryRepository
                .findAllByUserIdsAndDate(challengeUserIds, LocalDate.now())
                .stream()
                .collect(Collectors.toMap(
                        DailyStudySummary::getUserId,
                        DailyStudySummary::getStudyTime
                ));
    }

    /**
     * 챌린지 스터디에 참여 중인 사용자 ID 목록을 추출한다.
     * <p>
     * 스터디 목록 중 챌린지 유형만 필터링한 뒤, 참여자 정보를 기반으로 중복 제거된 사용자 ID 목록을 반환한다.
     * </p>
     *
     * @param studies      스터디 목록
     * @param userStudyMap studyId 기준 참여자 매핑 정보
     * @return 챌린지 스터디 참여 사용자 ID 목록
     */
    private List<Long> findChallengeUserIds(List<Study> studies, Map<Long, List<UserStudy>> userStudyMap) {
        return studies.stream()
                .filter(Study::isChallengeStudy)
                .flatMap(study -> userStudyMap.getOrDefault(study.getId(), List.of()).stream())
                .map(UserStudy::getUserId)
                .distinct()
                .toList();
    }
}
