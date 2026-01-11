package com.togedy.togedy_server_v2.domain.study.application;

import com.togedy.togedy_server_v2.domain.planner.dao.DailyStudySummaryRepository;
import com.togedy.togedy_server_v2.domain.planner.entity.DailyStudySummary;
import com.togedy.togedy_server_v2.domain.study.dao.StudyRepository;
import com.togedy.togedy_server_v2.domain.study.dao.UserStudyRepository;
import com.togedy.togedy_server_v2.domain.study.dto.ActiveMemberDto;
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
import com.togedy.togedy_server_v2.domain.study.exception.StudyLeaderNotFoundException;
import com.togedy.togedy_server_v2.domain.user.dao.UserRepository;
import com.togedy.togedy_server_v2.domain.user.entity.User;
import com.togedy.togedy_server_v2.global.service.S3Service;
import com.togedy.togedy_server_v2.global.util.TimeUtil;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class StudyExternalService {

    private final DailyStudySummaryRepository dailyStudySummaryRepository;
    private final UserStudyRepository userStudyRepository;
    private final StudyRepository studyRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;

    private static final String TIER = "tier";
    private static final int POPULAR_STUDY_SEARCH_SIZE = 3;

    /**
     * 새로운 스터디를 생성하고, 생성 요청 사용자를 해당 스터디의 리더로 등록한다.
     * <p>
     * 스터디 생성 시 다음 작업이 하나의 트랜잭션으로 처리된다.
     * <ul>
     *     <li>스터디 목표 시간을 초 단위로 변환</li>
     *     <li>스터디 이미지 업로드 및 이미지 URL 생성</li>
     *     <li>목표 시간에 따라 스터디 유형 결정</li>
     *     <li>스터디 엔티티 생성 및 저장</li>
     *     <li>생성 요청 사용자를 스터디 리더로 등록</li>
     * </ul>
     * </p>
     *
     * @param request 스터디 생성 요청 DTO
     * @param userId  스터디를 생성한 사용자 ID
     */
    @Transactional
    public void generateStudy(PostStudyRequest request, Long userId) {
        StudyType type = determineStudyTypeByGoalTime(request.getGoalTime());
        Long goalTime = (type == StudyType.CHALLENGE) ? TimeUtil.convertHoursToSeconds(request.getGoalTime()) : null;
        String imageUrl = uploadStudyImage(request.getStudyImage());

        Study study = Study.builder()
                .name(request.getStudyName())
                .description(request.getStudyDescription())
                .memberLimit(request.getStudyMemberLimit())
                .tag(StudyTag.fromDescription(request.getStudyTag()))
                .imageUrl(imageUrl)
                .type(type)
                .goalTime(goalTime)
                .password(request.getStudyPassword())
                .tier(TIER)
                .build();

        Study savedStudy = studyRepository.save(study);

        UserStudy userStudy = UserStudy.builder()
                .userId(userId)
                .studyId(savedStudy.getId())
                .role(StudyRole.LEADER)
                .build();

        userStudyRepository.save(userStudy);
    }

    /**
     * 스터디 이름의 중복 여부를 조회한다.
     *
     * @param studyName 중복 여부를 확인할 스터디 이름
     * @return 스터디 이름 중복 여부를 포함한 응답 DTO
     */
    public GetStudyNameDuplicateResponse findStudyNameDuplicate(String studyName) {
        boolean isDuplicate = studyRepository.existsByName(studyName);

        return GetStudyNameDuplicateResponse.from(isDuplicate);
    }

    /**
     * 사용자의 당일 공부 기록과 참여 중인 스터디 정보를 함께 조회한다.
     * <p>
     * 조회 결과에는 다음 정보가 포함된다.
     * <ul>
     *     <li>오늘 기준 사용자의 총 공부 시간</li>
     *     <li>사용자가 참여 중인 스터디 목록</li>
     *     <li>각 스터디별 멤버 정보 및 오늘의 공부 시간</li>
     * </ul>
     * </p>
     * <p>
     * 모든 학습 시간 관련 정보는 오늘 날짜를 기준으로 집계된다.
     * </p>
     *
     * @param userId 조회 대상 사용자 ID
     * @return 당일 공부 기록 및 참여 스터디 정보를 포함한 응답 DTO
     */
    public GetMyStudyInfoResponse findMyStudyInfo(Long userId) {
        LocalDateTime start = TimeUtil.startOfToday();
        LocalDateTime end = TimeUtil.startOfTomorrow();

        List<Study> studies = studyRepository.findAllByUserIdOrderByCreatedAtAsc(userId);

        Long studyTime = dailyStudySummaryRepository
                .findByUserIdAndCreatedAt(userId, start, end)
                .map(DailyStudySummary::getStudyTime)
                .orElse(0L);

        List<UserStudy> userStudies = findUserStudiesByStudies(studies);

        Map<Long, List<UserStudy>> userStudyMap = userStudies.stream()
                .collect(Collectors.groupingBy(UserStudy::getStudyId));

        List<Long> memberIds = userStudies.stream()
                .map(UserStudy::getUserId)
                .distinct()
                .toList();

        Map<Long, User> memberMap = userRepository.findAllById(memberIds)
                .stream()
                .collect(Collectors.toMap(User::getId, user -> user));

        List<DailyStudySummary> dailyStudySummaries = dailyStudySummaryRepository.findAllByUserIdsAndPeriod(
                memberIds,
                start,
                end
        );

        Map<Long, Long> studyTimeMap = dailyStudySummaries.stream()
                .collect(Collectors.toMap(
                        DailyStudySummary::getUserId,
                        DailyStudySummary::getStudyTime
                ));

        List<StudyDto> studyDtos = studies.stream()
                .map(study -> buildStudyDto(study, userStudyMap, memberMap, studyTimeMap))
                .toList();

        return buildMyStudyInfoResponse(studies, studyTime, studyDtos);
    }

    /**
     * 조건에 따라 스터디를 검색한다.
     * <p>
     * 스터디 이름, 태그, 필터 조건, 참가 가능 여부, 챌린지 여부를 기준으로 스터디 목록을 조회하며, 태그 조건이 존재하는 경우와 없는 경우에 따라 검색 쿼리가 분기된다.
     * </p>
     * <p>
     * 페이지 번호는 1부터 시작하며, 조회 결과는 다음 페이지 존재 여부와 함께 반환된다.
     * </p>
     *
     * @param name      검색할 스터디 이름 (부분 일치)
     * @param tags      검색할 스터디 태그 목록
     * @param filter    검색 정렬 조건 (기본값: 최신순)
     * @param joinable  참가 가능 스터디만 조회할지 여부
     * @param challenge 챌린지 스터디만 조회할지 여부
     * @param page      페이지 번호 (1부터 시작)
     * @param size      페이지당 조회 개수
     * @param userId    검색을 요청한 사용자 ID
     * @return 스터디 검색 결과 및 다음 페이지 존재 여부를 포함한 응답 DTO
     */

    public GetStudySearchResponse findStudySearch(
            String name,
            List<String> tags,
            String filter,
            boolean joinable,
            boolean challenge,
            int page,
            int size,
            Long userId
    ) {
        PageRequest pageRequest = PageRequest.of(Math.max(page - 1, 0), size, Sort.by("name"));

        List<StudyTag> studyTags = List.of();

        if (!CollectionUtils.isEmpty(tags)) {
            studyTags = tags.stream()
                    .map(StudyTag::fromDescription)
                    .toList();
        }

        Slice<Study> studySlice = CollectionUtils.isEmpty(studyTags)
                ? studyRepository.findStudiesWithoutTags(name, filter, joinable, challenge, pageRequest)
                : studyRepository.findStudiesWithTags(name, studyTags, filter, joinable, challenge, pageRequest);

        List<Study> studies = studySlice.getContent();

        List<UserStudy> userStudies = findUserStudiesByStudies(studies);

        Map<Long, List<UserStudy>> userStudyMap = userStudies.stream()
                .collect(Collectors.groupingBy(UserStudy::getStudyId));

        Map<Long, User> userMap = mapUsersById(userStudies);

        List<StudySearchDto> studySearchDtos = studySlice.stream()
                .map(study -> buildStudySearchDto(study, userStudyMap, userMap))
                .toList();

        return GetStudySearchResponse.of(studySlice.hasNext(), studySearchDtos);
    }

    /**
     * 활동 중인 인원이 많은 스터디 중 일부를 조회한다.
     * <p>
     * 현재 활동량이 높은 스터디를 우선 조회한 뒤, 그 중 일부를 무작위로 선택하여 반환한다.
     * </p>
     * <p>
     * 반환되는 스터디 목록의 순서와 구성은 호출 시점마다 달라질 수 있다.
     * </p>
     *
     * @return 인기 스터디 검색 결과 DTO 목록
     */
    public List<StudySearchDto> findPopularStudies() {
        Pageable pageable = PageRequest.of(0, 20);
        List<Study> studies = studyRepository.findMostActiveStudies(pageable);
        Collections.shuffle(studies);

        List<Study> selectedStudies = studies.stream()
                .limit(POPULAR_STUDY_SEARCH_SIZE)
                .toList();

        List<UserStudy> userStudies = findUserStudiesByStudies(selectedStudies);

        Map<Long, List<UserStudy>> userStudyMap = userStudies.stream()
                .collect(Collectors.groupingBy(UserStudy::getStudyId));

        Map<Long, User> userMap = mapUsersById(userStudies);

        return selectedStudies.stream()
                .map(study -> buildStudySearchDto(study, userStudyMap, userMap))
                .toList();
    }

    /**
     * 챌린지 스터디의 목표 공부 시간을 달성한 멤버 수를 집계한다.
     *
     * @param study      챌린지 스터디 엔티티
     * @param members    스터디에 속한 멤버 목록
     * @param summaryMap 사용자별 당일 공부 시간 맵
     * @return 목표 시간을 충족한 멤버 수
     */
    private int countCompletedMembers(
            Study study,
            List<User> members,
            Map<Long, Long> summaryMap
    ) {
        long goalTime = study.getGoalTime();

        int count = 0;
        for (User member : members) {
            long studyTime = summaryMap.getOrDefault(member.getId(), 0L);
            if (studyTime >= goalTime) {
                count++;
            }
        }
        return count;
    }

    /**
     * 챌린지 스터디의 성공률을 퍼센트(%) 단위로 계산한다.
     * <p>
     * 성공률은 전체 스터디 참여 인원 대비 목표를 달성한 인원의 비율에 100을 곱한 값으로 계산되며, 소수점은 버림 처리한다.
     * </p>
     * <p>
     * 스터디 참여 인원이 0명인 경우 0을 반환한다.
     * </p>
     *
     * @param completedMemberCount 챌린지 목표를 달성한 인원 수
     * @param studyMemberCount     스터디 참여 인원 수
     * @return 챌린지 성공률 (퍼센트, 0~100)
     */
    private int calculateCompletionPercentage(int completedMemberCount, int studyMemberCount) {
        if (studyMemberCount == 0) {
            return 0;
        }

        return (int) ((completedMemberCount * 100.0) / studyMemberCount);
    }

    /**
     * 스터디 목표 시간 존재 여부에 따라 스터디 타입을 결정한다.
     * <p>
     * 목표 시간이 설정된 경우 챌린지 스터디로, 설정되지 않은 경우 일반 스터디로 분류한다.
     * </p>
     *
     * @param goalTime 스터디 목표 시간 (시간 단위)
     * @return 결정된 스터디 타입
     */
    private StudyType determineStudyTypeByGoalTime(Integer goalTime) {
        if (goalTime != null) {
            return StudyType.CHALLENGE;
        }
        return StudyType.NORMAL;
    }

    /**
     * 스터디 이미지 파일을 업로드하고 이미지 URL로 변환한다.
     * <p>
     * 이미지 파일이 전달된 경우 S3에 업로드한 뒤 업로드된 이미지의 URL을 반환한다.
     * </p>
     * <p>
     * 이미지 파일이 없는 경우 {@code null}을 반환한다.
     * </p>
     *
     * @param image 스터디 이미지 파일
     * @return 업로드된 이미지 URL, 이미지가 없는 경우 {@code null}
     */
    private String uploadStudyImage(MultipartFile image) {
        if (image != null) {
            return s3Service.uploadFile(image);
        }
        return null;
    }

    /**
     * 여러 스터디에 속한 모든 스터디 유저 정보를 조회한다.
     *
     * @param studies 스터디 목록
     * @return 스터디 ID에 속한 유저-스터디 연관 정보 목록
     */
    private List<UserStudy> findUserStudiesByStudies(List<Study> studies) {
        List<Long> studyIds = studies
                .stream()
                .map(Study::getId)
                .toList();

        return userStudyRepository.findAllByStudyIds(studyIds);
    }

    /**
     * 스터디 유저 정보를 기반으로 사용자 ID 기준의 사용자 맵을 생성한다.
     *
     * @param userStudies 스터디 유저 정보 목록
     * @return 사용자 ID를 키로 하는 사용자 맵
     */
    private Map<Long, User> mapUsersById(List<UserStudy> userStudies) {
        List<Long> userIds = userStudies.stream()
                .map(UserStudy::getUserId)
                .distinct()
                .toList();

        return userRepository.findAllById(userIds)
                .stream()
                .collect(Collectors.toMap(User::getId, user -> user));
    }

    /**
     * 스터디 검색 결과를 검색 화면용 DTO로 변환한다.
     * <p>
     * 변환 과정에서 다음 정보를 함께 계산한다.
     * <ul>
     *     <li>스터디 리더의 프로필 이미지</li>
     *     <li>최근 활동한 사용자 기준 마지막 활동 시간</li>
     *     <li>신규 생성 스터디 여부</li>
     *     <li>챌린지 스터디의 목표 시간</li>
     * </ul>
     * </p>
     *
     * @param study        스터디 엔티티
     * @param userStudyMap 스터디 ID를 키로 하고, 해당 스터디의 {@link UserStudy} 목록을 값으로 갖는 맵
     * @param userMap      사용자 ID를 키로 하고, {@link User} 엔티티를 값으로 갖는 맵
     * @return 스터디 검색 결과 DTO
     */
    private StudySearchDto buildStudySearchDto(
            Study study,
            Map<Long, List<UserStudy>> userStudyMap,
            Map<Long, User> userMap
    ) {
        List<UserStudy> members = userStudyMap.get(study.getId());

        User leader = findLeaderInStudy(userMap, members);
        User lastAcivatedUser = findLastActiveMemberInStudy(userMap, members);

        String lastActivatedAt = lastAcivatedUser != null
                ? TimeUtil.formatTimeAgo(lastAcivatedUser.getLastActivatedAt())
                : null;

        String challengeGoalTime = TimeUtil.formatSecondsToHms(study.getGoalTime());
        boolean isNewlyCreated = study.isNewlyCreated();

        return StudySearchDto.of(
                study,
                leader.getProfileImageUrl(),
                isNewlyCreated,
                lastActivatedAt,
                challengeGoalTime
        );
    }

    /**
     * 스터디 멤버 중 가장 최근에 활동한 사용자를 조회한다.
     * <p>
     * 마지막 활동 시간이 가장 최근인 사용자를 기준으로 하며, 활동 기록이 없는 경우 {@code null}을 반환한다.
     * </p>
     *
     * @param userMap 유저 ID를 키로 하는 사용자 맵
     * @param members 스터디 유저 정보 목록
     * @return 가장 최근에 활동한 사용자, 존재하지 않는 경우 {@code null}
     */
    private User findLastActiveMemberInStudy(Map<Long, User> userMap, List<UserStudy> members) {
        return members.stream()
                .map(userStudy -> userMap.get(userStudy.getUserId()))
                .filter(Objects::nonNull)
                .max(Comparator.comparing(User::getLastActivatedAt,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .orElse(null);
    }

    /**
     * 스터디에 속한 리더 사용자를 조회한다.
     *
     * @param userMap 유저 ID를 키로 하는 사용자 맵
     * @param members 스터디 유저 정보 목록
     * @return 스터디 리더 사용자
     * @throws StudyLeaderNotFoundException 스터디 리더가 존재하지 않는 경우
     */
    private User findLeaderInStudy(Map<Long, User> userMap, List<UserStudy> members) {
        return members.stream()
                .filter(userStudy -> userStudy.getRole() == StudyRole.LEADER)
                .findFirst()
                .map(userStudy -> userMap.get(userStudy.getUserId()))
                .orElseThrow(StudyLeaderNotFoundException::new);
    }

    /**
     * 스터디 정보를 홈 화면용 DTO로 변환한다.
     * <p>
     * 챌린지 스터디인 경우, 목표 달성 인원 수와 성공률을 함께 계산하여 반환한다.
     * </p>
     *
     * @param study        스터디 엔티티
     * @param userStudyMap 스터디 ID를 키로 하는 스터디 유저 정보 맵
     * @param memberMap    사용자 ID를 키로 하는 사용자 맵
     * @param studyTimeMap 사용자별 당일 공부 시간 맵
     * @return 스터디 정보 DTO
     */
    private StudyDto buildStudyDto(
            Study study,
            Map<Long, List<UserStudy>> userStudyMap,
            Map<Long, User> memberMap,
            Map<Long, Long> studyTimeMap
    ) {
        List<UserStudy> memberStudies = userStudyMap.get(study.getId());

        List<User> members = memberStudies.stream()
                .map(userStudy -> memberMap.get(userStudy.getUserId()))
                .toList();

        List<ActiveMemberDto> activeMemberDtos = members.stream()
                .map(ActiveMemberDto::from)
                .toList();

        if (study.isChallengeStudy()) {
            int completedMemberCount = countCompletedMembers(study, members, studyTimeMap);
            int challengeAchievement = calculateCompletionPercentage(completedMemberCount, study.getMemberCount());
            return StudyDto.of(study, challengeAchievement, completedMemberCount, activeMemberDtos);
        }

        return StudyDto.of(study, activeMemberDtos);
    }

    /**
     * 사용자의 당일 공부 기록과 스터디 정보를 홈 화면용 응답 DTO로 생성한다.
     * <p>
     * 챌린지 스터디가 존재하는 경우, 목표 시간이 가장 높은 챌린지 스터디를 기준으로 달성률 정보를 함께 반환한다.
     * </p>
     * <p>
     * 챌린지 스터디가 없는 경우, 스터디 목록만 포함된 기본 응답을 반환한다.
     * </p>
     *
     * @param studies   사용자가 참여 중인 스터디 목록
     * @param studyTime 사용자의 당일 총 공부 시간
     * @param studyDtos 스터디 정보 DTO 목록
     * @return 홈 화면용 사용자 공부 및 스터디 정보 응답 DTO
     */
    private GetMyStudyInfoResponse buildMyStudyInfoResponse(
            List<Study> studies,
            Long studyTime,
            List<StudyDto> studyDtos
    ) {
        return studies.stream()
                .filter(Study::isChallengeStudy)
                .max(Comparator.comparing(Study::getGoalTime))
                .map(challengeStudy -> {
                    long goalTime = challengeStudy.getGoalTime();
                    int achievement = TimeUtil.calculateAchievement(studyTime, goalTime);
                    return GetMyStudyInfoResponse.of(
                            TimeUtil.formatSecondsToHms(goalTime),
                            TimeUtil.formatSecondsToHms(studyTime),
                            achievement,
                            studyDtos
                    );
                })
                .orElse(GetMyStudyInfoResponse.from(studyDtos));
    }
}
