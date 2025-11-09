package com.togedy.togedy_server_v2.domain.study.application;

import com.togedy.togedy_server_v2.domain.planner.dao.DailyStudySummaryRepository;
import com.togedy.togedy_server_v2.domain.planner.entity.DailyStudySummary;
import com.togedy.togedy_server_v2.domain.study.dao.StudyRepository;
import com.togedy.togedy_server_v2.domain.study.dao.UserStudyRepository;
import com.togedy.togedy_server_v2.domain.study.dto.*;
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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudyExternalService {

    private final DailyStudySummaryRepository dailyStudySummaryRepository;
    private final UserStudyRepository userStudyRepository;
    private final StudyRepository studyRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;

    /**
     * 스터디를 생성한다.
     *
     * @param request   스터디 생성 DTO
     * @param userId    유저 ID
     */
    @Transactional
    public void generateStudy(PostStudyRequest request, Long userId) {

        String imageUrl = null;
        StudyType type = StudyType.NORMAL;
        String tier = null;
        Long goalTime = null;

        if (request.getStudyImage() != null) {
            imageUrl = s3Service.uploadFile(request.getStudyImage());
        }

        if (request.getGoalTime() != null) {
            type = StudyType.CHALLENGE;
            tier = "tier";
            goalTime = request.getGoalTime() * 3600L;
        }

        Study study = Study.builder()
                .name(request.getStudyName())
                .description(request.getStudyDescription())
                .memberLimit(request.getStudyMemberLimit())
                .tag(StudyTag.fromDescription(request.getStudyTag()))
                .imageUrl(imageUrl)
                .type(type)
                .goalTime(goalTime)
                .password(request.getStudyPassword())
                .tier(tier)
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
     * @param studyName     스터디 이름
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
                .filter(study -> study.getType() == StudyType.CHALLENGE)
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
            Long userId)
    {
        PageRequest pageRequest = PageRequest.of(Math.max(page - 1, 0), size, Sort.by("name"));
        List<StudyTag> studyTags = null;
        if (tags != null && !tags.isEmpty()) {
            studyTags = tags.stream()
                    .map(StudyTag::fromDescription)
                    .collect(Collectors.toList());
        }

        Slice<Study> studyList;
        if (studyTags == null || studyTags.isEmpty()) {
            studyList = studyRepository.findStudiesWithoutTags(name, filter, joinable, challenge, pageRequest);
        } else {
            studyList = studyRepository.findStudiesWithTags(name, studyTags, filter, joinable, challenge, pageRequest);
        }

        List<StudySearchDto> studySearchDtos = studyList.stream()
                .map(study -> {
                    List<User> userList = userRepository.findAllByStudyId(study.getId());
                    Optional<User> user = userList.stream().max(Comparator.comparing(User::getLastActivatedAt));
                    String lastActivatedAt = null;
                    if (user.isPresent()) {
                        lastActivatedAt = TimeUtil.formatTimeAgo(user.get().getLastActivatedAt());
                    }

                    User leader = userRepository.findByStudyIdAndRole(study.getId(), StudyRole.LEADER)
                            .orElseThrow(StudyLeaderNotFoundException::new);

                    String studyLeaderImageUrl = leader.getProfileImageUrl();
                    String challengeGoalTime = TimeUtil.toTimeFormat(study.getGoalTime());
                    boolean isNewlyCreated = validateNewlyCreated(study.getCreatedAt());
                    boolean hasPassword = study.getPassword() != null;
                    return StudySearchDto.of(study, studyLeaderImageUrl, isNewlyCreated, lastActivatedAt, challengeGoalTime, hasPassword);
                })
                .toList();

        return GetStudySearchResponse.of(studyList.hasNext(), studySearchDtos);
    }

    public List<StudySearchDto> findPopularStudies() {
        Pageable pageable = PageRequest.of(0, 20);
        List<Study> studies = studyRepository.findMostAcitveStudies(pageable);
        Collections.shuffle(studies);
        return studies.stream()
                .limit(3)
                .map(study -> {
                    List<User> userList = userRepository.findAllByStudyId(study.getId());
                    Optional<User> user = userList.stream().max(Comparator.comparing(User::getLastActivatedAt));
                    String lastActivatedAt = null;
                    if (user.isPresent()) {
                        lastActivatedAt = TimeUtil.formatTimeAgo(user.get().getLastActivatedAt());
                    }

                    User leader = userRepository.findByStudyIdAndRole(study.getId(), StudyRole.LEADER)
                            .orElseThrow(StudyLeaderNotFoundException::new);

                    String studyLeaderImageUrl = leader.getProfileImageUrl();
                    String challengeGoalTime = TimeUtil.toTimeFormat(study.getGoalTime());
                    boolean isNewlyCreated = validateNewlyCreated(study.getCreatedAt());
                    boolean hasPassword = study.getPassword() != null;
                    return StudySearchDto.of(study, studyLeaderImageUrl, isNewlyCreated, lastActivatedAt, challengeGoalTime, hasPassword);
                })
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

    private boolean validateNewlyCreated(LocalDateTime createdAt) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime current = now.minusDays(7);

        return createdAt.isAfter(current) && createdAt.isBefore(now);
    }
}
