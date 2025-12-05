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
import com.togedy.togedy_server_v2.domain.user.enums.UserStatus;
import com.togedy.togedy_server_v2.global.service.S3Service;
import com.togedy.togedy_server_v2.global.util.TimeUtil;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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

    /**
     * 스터디를 생성한다.
     *
     * @param request 스터디 생성 DTO
     * @param userId  유저 ID
     */
    @Transactional
    public void generateStudy(PostStudyRequest request, Long userId) {
        Long goalTime = TimeUtil.convertHoursToSeconds(request.getGoalTime());
        String imageUrl = convertImageToUrl(request.getStudyImage());
        StudyType type = detemineStudyType(request.getGoalTime());

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
     * 스터디 이름의 중복 여부를 검사한다.
     *
     * @param studyName 스터디 이름
     * @return
     */
    public GetStudyNameDuplicateResponse findStudyNameDuplicate(String studyName) {
        boolean isDuplicate = studyRepository.existsByName(studyName);

        return GetStudyNameDuplicateResponse.from(isDuplicate);
    }

    public GetMyStudyInfoResponse findMyStudyInfo(Long userId) {
        LocalDate today = LocalDate.now();

        List<Study> studyList = studyRepository.findAllByUserIdOrderByCreatedAtAsc(userId);
        Optional<DailyStudySummary> todaySummaryOpt = dailyStudySummaryRepository.findByUserIdAndCreatedAt(
                userId,
                today.atStartOfDay(),
                today.plusDays(1).atStartOfDay()
        );

        long todayStudyTime = todaySummaryOpt
                .map(DailyStudySummary::getStudyTime)
                .orElse(0L);

        List<StudyDto> studyDtoList = studyList.stream()
                .map(study -> {
                    boolean isChallenge = study.getType() == StudyType.CHALLENGE;
                    List<User> userList = userRepository.findAllByStudyIdAndStatus(study.getId(), UserStatus.STUDYING);
                    List<ActiveMemberDto> activeMemberDtoList = userList.stream()
                            .map(ActiveMemberDto::from)
                            .toList();

                    if (isChallenge) {
                        int completedMemberCount = countCompletedMember(study);
                        int challengeAchievement = calculateCompleteRate(completedMemberCount, study.getMemberCount());
                        return StudyDto.of(study, challengeAchievement, completedMemberCount, activeMemberDtoList);
                    }

                    return StudyDto.of(study, activeMemberDtoList);
                })
                .toList();

        Optional<Study> challengeStudy = studyList.stream()
                .filter(Study::isChallengeStudy)
                .max(Comparator.comparing(Study::getGoalTime));

        if (challengeStudy.isPresent()) {
            long goalTime = challengeStudy.get().getGoalTime();
            int achievement = TimeUtil.calculateAchievement(todayStudyTime, goalTime);
            return GetMyStudyInfoResponse.of(
                    TimeUtil.toTimeFormat(goalTime),
                    TimeUtil.toTimeFormat(todayStudyTime),
                    achievement,
                    studyDtoList
            );
        }

        return GetMyStudyInfoResponse.from(studyDtoList);

    }

    public GetStudySearchResponse findStudySearch(
            String name,
            List<String> tags,
            String filter,
            boolean joinable,
            boolean challenge,
            int page,
            int size,
            Long userId) {
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

        List<UserStudy> userStudies = loadUserStudies(studies);

        Map<Long, List<UserStudy>> userStudyMap = userStudies.stream()
                .collect(Collectors.groupingBy(UserStudy::getStudyId));

        Map<Long, User> userMap = mapUsersById(userStudies);

        List<StudySearchDto> studySearchDtos = studySlice.stream()
                .map(study -> toStudySearchDto(study, userStudyMap, userMap))
                .toList();

        return GetStudySearchResponse.of(studySlice.hasNext(), studySearchDtos);
    }

    public List<StudySearchDto> findPopularStudies() {
        Pageable pageable = PageRequest.of(0, 20);
        List<Study> studies = studyRepository.findMostAcitveStudies(pageable);
        Collections.shuffle(studies);

        List<Study> selectedStudies = studies.stream()
                .limit(3)
                .toList();

        List<UserStudy> userStudies = loadUserStudies(selectedStudies);

        Map<Long, List<UserStudy>> userStudyMap = userStudies.stream()
                .collect(Collectors.groupingBy(UserStudy::getStudyId));

        Map<Long, User> userMap = mapUsersById(userStudies);

        return selectedStudies.stream()
                .map(study -> toStudySearchDto(study, userStudyMap, userMap))
                .toList();
    }

    private int countCompletedMember(Study study) {
        int count = 0;
        LocalDate today = LocalDate.now();

        List<User> memberList = userRepository.findAllByStudyId(study.getId());
        List<Long> memberIdList = memberList.stream()
                .map(User::getId)
                .toList();

        List<DailyStudySummary> dailyStudySummaryList =
                dailyStudySummaryRepository.findAllByUserIdsAndCreatedAt(
                        memberIdList,
                        today.atStartOfDay(),
                        today.plusDays(1).atStartOfDay()
                );

        for (DailyStudySummary dailyStudySummary : dailyStudySummaryList) {
            if (study.getGoalTime() <= dailyStudySummary.getStudyTime()) {
                count++;
            }
        }

        return count;
    }

    private int calculateCompleteRate(int completedMemberCount, int studyMemberCount) {
        if (studyMemberCount == 0) {
            return 0;
        }

        return (int) ((double) completedMemberCount / studyMemberCount);
    }

    private StudyType detemineStudyType(Integer goalTime) {
        if (goalTime != null) {
            return StudyType.CHALLENGE;
        }
        return StudyType.NORMAL;
    }

    private String convertImageToUrl(MultipartFile image) {
        if (image != null) {
            return s3Service.uploadFile(image);
        }
        return null;
    }

    private List<UserStudy> loadUserStudies(List<Study> studies) {
        List<Long> studyIds = studies
                .stream()
                .map(Study::getId)
                .toList();

        return userStudyRepository.findAllByStudyIds(studyIds);
    }

    private Map<Long, User> mapUsersById(List<UserStudy> userStudies) {
        List<Long> userIds = userStudies.stream()
                .map(UserStudy::getUserId)
                .distinct()
                .toList();

        Map<Long, User> userMap = userRepository.findAllById(userIds)
                .stream()
                .collect(Collectors.toMap(User::getId, user -> user));
        return userMap;
    }

    private StudySearchDto toStudySearchDto(
            Study study,
            Map<Long, List<UserStudy>> userStudyMap,
            Map<Long, User> userMap
    ) {
        List<UserStudy> members = userStudyMap.get(study.getId());

        User leader = findLeaderInStudy(userMap, members);
        User lastAcivatedUser = findLastActivatedUserInStudy(userMap, members);

        String lastActivatedAt = lastAcivatedUser != null
                ? TimeUtil.formatTimeAgo(lastAcivatedUser.getLastActivatedAt())
                : null;

        String challengeGoalTime = TimeUtil.toTimeFormat(study.getGoalTime());
        boolean isNewlyCreated = study.validateNewlyCreated();

        return StudySearchDto.of(
                study,
                leader.getProfileImageUrl(),
                isNewlyCreated,
                lastActivatedAt,
                challengeGoalTime
        );
    }

    private User findLastActivatedUserInStudy(Map<Long, User> userMap, List<UserStudy> members) {
        return members.stream()
                .map(userStudy -> userMap.get(userStudy.getUserId()))
                .filter(Objects::nonNull)
                .max(Comparator.comparing(User::getLastActivatedAt,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .orElse(null);
    }

    private User findLeaderInStudy(Map<Long, User> userMap, List<UserStudy> members) {
        return members.stream()
                .filter(userStudy -> userStudy.getRole() == StudyRole.LEADER)
                .findFirst()
                .map(userStudy -> userMap.get(userStudy.getUserId()))
                .orElseThrow(StudyLeaderNotFoundException::new);
    }
}
