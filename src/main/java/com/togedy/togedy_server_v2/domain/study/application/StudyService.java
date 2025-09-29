package com.togedy.togedy_server_v2.domain.study.application;

import com.togedy.togedy_server_v2.domain.planner.dao.DailyStudySummaryRepository;
import com.togedy.togedy_server_v2.domain.planner.entity.DailyStudySummary;
import com.togedy.togedy_server_v2.domain.study.dao.StudyRepository;
import com.togedy.togedy_server_v2.domain.study.dao.UserStudyRepository;
import com.togedy.togedy_server_v2.domain.study.dto.ActiveMemberDto;
import com.togedy.togedy_server_v2.domain.study.dto.GetMyStudyInfoResponse;
import com.togedy.togedy_server_v2.domain.study.dto.GetStudyMemberResponse;
import com.togedy.togedy_server_v2.domain.study.dto.GetStudyNameDuplicateResponse;
import com.togedy.togedy_server_v2.domain.study.dto.GetStudyResponse;
import com.togedy.togedy_server_v2.domain.study.dto.GetStudySearchResponse;
import com.togedy.togedy_server_v2.domain.study.dto.PatchStudyInfoRequest;
import com.togedy.togedy_server_v2.domain.study.dto.PatchStudyMemberLimitRequest;
import com.togedy.togedy_server_v2.domain.study.dto.PostStudyMemberRequest;
import com.togedy.togedy_server_v2.domain.study.dto.PostStudyRequest;
import com.togedy.togedy_server_v2.domain.study.dto.StudyDto;
import com.togedy.togedy_server_v2.domain.study.dto.StudySearchDto;
import com.togedy.togedy_server_v2.domain.study.entity.Study;
import com.togedy.togedy_server_v2.domain.study.entity.UserStudy;
import com.togedy.togedy_server_v2.domain.study.enums.StudyRole;
import com.togedy.togedy_server_v2.domain.study.enums.StudyTag;
import com.togedy.togedy_server_v2.domain.study.enums.StudyType;
import com.togedy.togedy_server_v2.domain.study.exception.StudyAccessDeniedException;
import com.togedy.togedy_server_v2.domain.study.exception.StudyLeaderNotFoundException;
import com.togedy.togedy_server_v2.domain.study.exception.StudyLeaderRequiredException;
import com.togedy.togedy_server_v2.domain.study.exception.StudyMemberLimitExceededException;
import com.togedy.togedy_server_v2.domain.study.exception.StudyMemberLimitIncreaseRequiredException;
import com.togedy.togedy_server_v2.domain.study.exception.StudyMemberRequiredException;
import com.togedy.togedy_server_v2.domain.study.exception.StudyNotFoundException;
import com.togedy.togedy_server_v2.domain.study.exception.StudyPasswordMismatchException;
import com.togedy.togedy_server_v2.domain.study.exception.StudyPasswordRequiredException;
import com.togedy.togedy_server_v2.domain.study.exception.UserStudyNotFoundException;
import com.togedy.togedy_server_v2.domain.user.dao.UserRepository;
import com.togedy.togedy_server_v2.domain.user.entity.User;
import com.togedy.togedy_server_v2.domain.user.enums.UserStatus;
import com.togedy.togedy_server_v2.global.service.S3Service;
import com.togedy.togedy_server_v2.global.util.DateTimeUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudyService {

    private final StudyRepository studyRepository;
    private final UserStudyRepository userStudyRepository;
    private final UserRepository userRepository;
    private final DailyStudySummaryRepository dailyStudySummaryRepository;
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
        Long goalTime = null;

        if (request.getStudyImage() != null) {
            imageUrl = s3Service.uploadFile(request.getStudyImage());
        }

        if (request.getGoalTime() != null) {
            type = StudyType.CHALLENGE;
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
                .tier("tier")
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
     * 스터디 정보를 조회한다.
     * 해당 스터디에 존재하는 유저만 수행할 수 있다.
     * 조회를 요청한 유저가 해당 스터디의 리더인 경우 비밀번호를 함께 반환한다.
     *
     * @param studyId   스터디 ID
     * @param userId    유저 ID
     * @return          해당 스터디 정보 DTO
     */
    public GetStudyResponse findStudyInfo(Long studyId, Long userId) {
        Integer count = null;
        String studyPassword = null;
        Study study = studyRepository.findById(studyId)
                .orElseThrow(StudyNotFoundException::new);

        User leader = userRepository.findByStudyIdAndRole(study.getId(), StudyRole.LEADER)
                .orElseThrow(StudyLeaderNotFoundException::new);

        if (study.getType().equals(StudyType.CHALLENGE)) {
            count = countCompletedMember(study);
        }

        boolean isStudyLeader = leader.getId().equals(userId);
        if (isStudyLeader) {
            studyPassword = study.getPassword();
        }
        return GetStudyResponse.of(isStudyLeader, study, leader, count, studyPassword);
    }

    /**
     * 스터디를 제거한다.
     * 해당 스터디의 리더만 수행할 수 있다.
     *
     * @param studyId   스터디 ID
     * @param leaderId  리더 ID
     */
    @Transactional
    public void removeStudy(Long studyId, Long leaderId) {
        UserStudy userStudy = userStudyRepository.findByStudyIdAndUserId(studyId, leaderId)
                .orElseThrow(UserStudyNotFoundException::new);

        validateStudyLeader(userStudy);

        Study study = studyRepository.findById(studyId)
                .orElseThrow(StudyNotFoundException::new);

        if (study.getImageUrl() != null) {
            s3Service.deleteFile(study.getImageUrl());
        }

        userStudyRepository.deleteAllByStudyId(studyId);
        studyRepository.delete(study);
    }

    /**
     * 스터디 정보를 변경한다.
     * 해당 스터디의 리더만 수행할 수 있다.
     *
     * @param request   스터디 정보 변경 DTO
     * @param studyId   스터디 ID
     * @param leaderId  리더 ID
     */
    @Transactional
    public void modifyStudyInfo(PatchStudyInfoRequest request, Long studyId, Long leaderId) {
        UserStudy userStudy = userStudyRepository.findByStudyIdAndUserId(studyId, leaderId)
                .orElseThrow(UserStudyNotFoundException::new);

        validateStudyLeader(userStudy);

        Study study = studyRepository.findById(studyId)
                .orElseThrow(StudyNotFoundException::new);

        String studyImageUrl = null;

        if (request.getStudyImage() != null) {
            studyImageUrl = s3Service.uploadFile(request.getStudyImage());
            s3Service.deleteFile(study.getImageUrl());
        }

        study.updateInfo(request, studyImageUrl);
        studyRepository.save(study);
    }

    /**
     * 스터디 최대 인원을 변경한다.
     * 기존에 설정한 최대 인원보다 더 많은 인원으로만 설정할 수 있다.
     * 해당 스터디의 리더만 수행할 수 있다.
     *
     * @param request   스터디 멤버 변경 DTO
     * @param studyId   스터디 ID
     * @param leaderId  리더 ID
     */
    @Transactional
    public void modifyStudyMemberLimit(PatchStudyMemberLimitRequest request, Long studyId, Long leaderId) {
        UserStudy userStudy = userStudyRepository.findByStudyIdAndUserId(studyId, leaderId)
                .orElseThrow(UserStudyNotFoundException::new);

        validateStudyLeader(userStudy);

        Study study = studyRepository.findById(studyId)
                .orElseThrow(StudyNotFoundException::new);

        if (request.getStudyMemberLimit() < study.getMemberLimit()) {
            throw new StudyMemberLimitIncreaseRequiredException();
        }

        study.updateMemberLimit(request);
        studyRepository.save(study);
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

    /**
     * 스터디에 멤버를 추가한다.
     * 스터디 인원이 최대인 경우 추가할 수 없다.
     *
     * @param request   스터디 입장 DTO
     * @param studyId   스터디 ID
     * @param userId    유저 ID
     */
    @Transactional
    public void registerStudyMember(PostStudyMemberRequest request, Long studyId, Long userId) {
        Study study = studyRepository.findById(studyId)
                .orElseThrow(StudyNotFoundException::new);

        if (study.getMemberCount() == study.getMemberLimit()) {
            throw new StudyMemberLimitExceededException();
        }

        if (study.getPassword() != null && request.getStudyPassword() == null) {
            throw new StudyPasswordRequiredException();
        }
        if (study.getPassword() != null && !study.getPassword().equals(request.getStudyPassword())) {
            throw new StudyPasswordMismatchException();
        }

        UserStudy userStudy = UserStudy.builder()
                .userId(userId)
                .studyId(studyId)
                .role(StudyRole.MEMBER)
                .build();

        userStudyRepository.save(userStudy);

        study.increaseMemberCount();
        studyRepository.save(study);
    }

    /**
     * 스터디에서 퇴장한다.
     * 해당 스터디의 리더는 수행할 수 없다.
     *
     * @param studyId   스터디 ID
     * @param memberId  멤버 ID
     */
    @Transactional
    public void removeMyStudyMembership(Long studyId, Long memberId) {
        UserStudy userStudy = userStudyRepository.findByStudyIdAndUserId(studyId, memberId)
                .orElseThrow(UserStudyNotFoundException::new);

        Study study = studyRepository.findById(studyId)
                .orElseThrow(StudyNotFoundException::new);

        if (!userStudy.getRole().equals(StudyRole.MEMBER)) {
            throw new StudyMemberRequiredException();
        }

        study.decreaseMemberCount();
        studyRepository.save(study);
        userStudyRepository.delete(userStudy);
    }

    /**
     * 스터디 멤버를 추방한다.
     * 해당 스터디의 리더만 수행할 수 있다.
     *
     * @param studyId   스터디 ID
     * @param memberId  추방 멤버 ID
     * @param leaderId  리더 ID
     */
    @Transactional
    public void removeStudyMember(Long studyId, Long memberId, Long leaderId) {
        Study study = studyRepository.findById(studyId)
                .orElseThrow(StudyNotFoundException::new);

        UserStudy userStudy = userStudyRepository.findByStudyIdAndUserId(studyId, leaderId)
                .orElseThrow(UserStudyNotFoundException::new);

        validateStudyLeader(userStudy);

        study.decreaseMemberCount();

        studyRepository.save(study);
        userStudyRepository.deleteByStudyIdAndUserId(studyId, memberId);
    }

    /**
     * 스터디 리더를 위임한다.
     * 해당 스터디의 리더만 수행할 수 있다.
     *
     * @param studyId   스터디 ID
     * @param memberId  멤버 ID
     * @param leaderId  리더 ID
     */
    @Transactional
    public void modifyStudyLeader(Long studyId, Long memberId, Long leaderId) {
        UserStudy leaderStudy = userStudyRepository.findByStudyIdAndUserId(studyId, leaderId)
                .orElseThrow(UserStudyNotFoundException::new);

        validateStudyLeader(leaderStudy);

        UserStudy memberStudy = userStudyRepository.findByStudyIdAndUserId(studyId, memberId)
                .orElseThrow(UserStudyNotFoundException::new);

        leaderStudy.modifyRole(StudyRole.MEMBER);
        memberStudy.modifyRole(StudyRole.LEADER);

        userStudyRepository.save(leaderStudy);
        userStudyRepository.save(memberStudy);
    }

    /**
     * 스터디 그룹원을 조회한다.
     * 조회를 요청한 유저가 스터디에 속한 경우, 가장 먼저 반환한다.
     *
     * @param studyId   스터디ID
     * @param userId    유저ID
     * @return          스터디 그룹원 조회 DTO
     */
    public List<GetStudyMemberResponse> findStudyMember(Long studyId, Long userId) {
        List<Object[]> rows = userRepository.findAllByStudyIdOrderByCreatedAtAsc(studyId);

        List<GetStudyMemberResponse> responses = rows.stream()
                .map(row -> {
                    User user = (User) row[0];
                    StudyRole role = (StudyRole) row[1];
                    return GetStudyMemberResponse.of(user, role);
                })
                .collect(Collectors.toList());

        responses.stream()
                .filter(r -> Objects.equals(r.getUserId(), userId))
                .findFirst()
                .ifPresent(target -> {
                    responses.remove(target);
                    responses.add(0, target);
                });

        return responses;
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
            int achievement = DateTimeUtils.calculateAchievement(todayStudyTime, goalTime);
            return GetMyStudyInfoResponse.of(
                    DateTimeUtils.timeConvert(goalTime),
                    DateTimeUtils.timeConvert(todayStudyTime),
                    achievement,
                    studyDtoList
            );
        }

        return GetMyStudyInfoResponse.from(studyDtoList);

    }

    public GetStudySearchResponse findStudySearch(
            String tag,
            String filter,
            boolean joinable,
            boolean challenge,
            int page,
            int size,
            Long userId)
    {
        StudyTag studyTag = null;
        if (tag != null) {
            studyTag = StudyTag.fromDescription(tag);
        }

        PageRequest pageRequest = PageRequest.of(Math.max(page - 1, 0), size, Sort.by("name"));
        Slice<Study> studyList = studyRepository.findStudiesByConditions(studyTag, filter, joinable, challenge, pageRequest);

        List<StudySearchDto> studySearchDtos = studyList.stream()
                .map(study -> {
                    List<User> userList = userRepository.findAllByStudyId(study.getId());
                    Optional<User> user = userList.stream().max(Comparator.comparing(User::getLastActivatedAt));
                    String lastActivatedAt = null;
                    if (user.isPresent()) {
                        lastActivatedAt = DateTimeUtils.formatTimeAgo(user.get().getLastActivatedAt());
                    }

                    User leader = userRepository.findByStudyIdAndRole(study.getId(), StudyRole.LEADER)
                            .orElseThrow(StudyLeaderNotFoundException::new);

                    String studyLeaderImageUrl = leader.getProfileImageUrl();
                    String challengeGoalTime = DateTimeUtils.timeConvert(study.getGoalTime());
                    boolean isNewlyCreated = validateNewlyCreated(study.getCreatedAt());
                    boolean hasPassword = study.getPassword() != null;
                    return StudySearchDto.of(study, studyLeaderImageUrl, isNewlyCreated, lastActivatedAt, challengeGoalTime, hasPassword);
                })
                .toList();

        return GetStudySearchResponse.of(studyList.hasNext(), studySearchDtos);
    }

    /**
     * 스터디 리더를 검증한다.
     *
     * @param userStudy 유저 스터디 테이블
     */
    private void validateStudyLeader(UserStudy userStudy) {
        if (!userStudy.getRole().equals(StudyRole.LEADER)) {
            throw new StudyLeaderRequiredException();
        }
    }

    /**
     * 스터디에 해당 유저의 존재 여부를 검증한다.
     *
     * @param studyId   스터디 ID
     * @param userId    유저 ID
     */
    private void validateStudyMember(Long studyId, Long userId) {
        if (!userStudyRepository.existsByStudyIdAndUserId(studyId, userId)) {
            throw new StudyAccessDeniedException();
        }
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
        return (int) ((double) completedMemberCount / studyMemberCount);
    }

    private boolean validateNewlyCreated(LocalDateTime createdAt) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime current = now.minusDays(7);

        return createdAt.isAfter(current) && createdAt.isBefore(now);
    }
}
