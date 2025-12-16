package com.togedy.togedy_server_v2.domain.study.application;

import com.togedy.togedy_server_v2.domain.planner.dao.DailyStudySummaryRepository;
import com.togedy.togedy_server_v2.domain.planner.dao.PlanRepository;
import com.togedy.togedy_server_v2.domain.planner.dao.StudyCategoryRepository;
import com.togedy.togedy_server_v2.domain.planner.entity.DailyStudySummary;
import com.togedy.togedy_server_v2.domain.study.dao.StudyRepository;
import com.togedy.togedy_server_v2.domain.study.dao.UserStudyRepository;
import com.togedy.togedy_server_v2.domain.study.dto.DailyStudyTimeDto;
import com.togedy.togedy_server_v2.domain.study.dto.GetStudyAttendanceResponse;
import com.togedy.togedy_server_v2.domain.study.dto.GetStudyMemberManagementResponse;
import com.togedy.togedy_server_v2.domain.study.dto.GetStudyMemberResponse;
import com.togedy.togedy_server_v2.domain.study.dto.GetStudyResponse;
import com.togedy.togedy_server_v2.domain.study.dto.PatchStudyInfoRequest;
import com.togedy.togedy_server_v2.domain.study.dto.PatchStudyMemberLimitRequest;
import com.togedy.togedy_server_v2.domain.study.dto.PostStudyMemberRequest;
import com.togedy.togedy_server_v2.domain.study.entity.Study;
import com.togedy.togedy_server_v2.domain.study.entity.UserStudy;
import com.togedy.togedy_server_v2.domain.study.enums.StudyRole;
import com.togedy.togedy_server_v2.domain.study.exception.StudyAccessDeniedException;
import com.togedy.togedy_server_v2.domain.study.exception.StudyLeaderNotFoundException;
import com.togedy.togedy_server_v2.domain.study.exception.StudyNotFoundException;
import com.togedy.togedy_server_v2.domain.study.exception.UserStudyNotFoundException;
import com.togedy.togedy_server_v2.domain.user.dao.UserRepository;
import com.togedy.togedy_server_v2.domain.user.entity.User;
import com.togedy.togedy_server_v2.global.service.S3Service;
import com.togedy.togedy_server_v2.global.util.TimeUtil;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudyInternalService {

    private final StudyRepository studyRepository;
    private final UserRepository userRepository;
    private final StudyCategoryRepository studyCategoryRepository;
    private final PlanRepository planRepository;
    private final S3Service s3Service;
    private final UserStudyRepository userStudyRepository;
    private final DailyStudySummaryRepository dailyStudySummaryRepository;

    /**
     * 스터디 정보를 조회한다. 해당 스터디에 존재하는 유저만 수행할 수 있다. 조회를 요청한 유저가 해당 스터디의 리더인 경우 비밀번호를 함께 반환한다.
     *
     * @param studyId 스터디 ID
     * @param userId  유저 ID
     * @return 해당 스터디 정보 DTO
     */
    public GetStudyResponse findStudyInfo(Long studyId, Long userId) {
        boolean isJoined = userStudyRepository.existsByStudyIdAndUserId(studyId, userId);

        Study study = studyRepository.findById(studyId)
                .orElseThrow(StudyNotFoundException::new);

        User leader = userRepository.findByStudyIdAndRole(study.getId(), StudyRole.LEADER)
                .orElseThrow(StudyLeaderNotFoundException::new);

        Integer completedCount = study.isChallengeStudy()
                ? countCompletedMembers(study)
                : null;

        boolean isStudyLeader = leader.getId()
                .equals(userId);

        if (isStudyLeader) {
            return GetStudyResponse.ofLeader(isJoined, study, leader, completedCount);
        }

        return GetStudyResponse.ofMember(isJoined, study, leader, completedCount);
    }

    /**
     * 스터디를 제거한다. 해당 스터디의 리더만 수행할 수 있다.
     *
     * @param studyId  스터디 ID
     * @param leaderId 리더 ID
     */
    @Transactional
    public void removeStudy(Long studyId, Long leaderId) {
        UserStudy userStudy = userStudyRepository.findByStudyIdAndUserId(studyId, leaderId)
                .orElseThrow(UserStudyNotFoundException::new);

        userStudy.validateStudyLeader();

        Study study = studyRepository.findById(studyId)
                .orElseThrow(StudyNotFoundException::new);

        s3Service.deleteFile(study.getImageUrl());

        userStudyRepository.deleteAllByStudyId(studyId);
        studyRepository.delete(study);
    }

    /**
     * 스터디 정보를 변경한다. 해당 스터디의 리더만 수행할 수 있다.
     *
     * @param request  스터디 정보 변경 DTO
     * @param studyId  스터디 ID
     * @param leaderId 리더 ID
     */
    @Transactional
    public void modifyStudyInfo(PatchStudyInfoRequest request, Long studyId, Long leaderId) {
        UserStudy userStudy = userStudyRepository.findByStudyIdAndUserId(studyId, leaderId)
                .orElseThrow(UserStudyNotFoundException::new);

        userStudy.validateStudyLeader();

        Study study = studyRepository.findById(studyId)
                .orElseThrow(StudyNotFoundException::new);

        String studyImageUrl = updateStudyImage(request, study);

        study.updateInfo(request, studyImageUrl);
        studyRepository.save(study);
    }

    /**
     * 스터디 최대 인원을 변경한다. 기존에 설정한 최대 인원보다 더 많은 인원으로만 설정할 수 있다. 해당 스터디의 리더만 수행할 수 있다.
     *
     * @param request  스터디 멤버 변경 DTO
     * @param studyId  스터디 ID
     * @param leaderId 리더 ID
     */
    @Transactional
    public void modifyStudyMemberLimit(PatchStudyMemberLimitRequest request, Long studyId, Long leaderId) {
        UserStudy userStudy = userStudyRepository.findByStudyIdAndUserId(studyId, leaderId)
                .orElseThrow(UserStudyNotFoundException::new);

        userStudy.validateStudyLeader();

        Study study = studyRepository.findById(studyId)
                .orElseThrow(StudyNotFoundException::new);

        study.updateMemberLimit(request.getStudyMemberLimit());
        studyRepository.save(study);
    }

    /**
     * 스터디에 멤버를 추가한다. 스터디 인원이 최대인 경우 추가할 수 없다.
     *
     * @param request 스터디 입장 DTO
     * @param studyId 스터디 ID
     * @param userId  유저 ID
     */
    @Transactional
    public void registerStudyMember(PostStudyMemberRequest request, Long studyId, Long userId) {
        Study study = studyRepository.findById(studyId)
                .orElseThrow(StudyNotFoundException::new);

        study.validatePassword(request.getStudyPassword());
        study.increaseMemberCount();
        studyRepository.save(study);

        UserStudy userStudy = UserStudy.builder()
                .userId(userId)
                .studyId(studyId)
                .role(StudyRole.MEMBER)
                .build();

        userStudyRepository.save(userStudy);
    }

    /**
     * 스터디에서 퇴장한다. 해당 스터디의 리더는 수행할 수 없다.
     *
     * @param studyId  스터디 ID
     * @param memberId 멤버 ID
     */
    @Transactional
    public void removeMyStudyMembership(Long studyId, Long memberId) {
        UserStudy userStudy = userStudyRepository.findByStudyIdAndUserId(studyId, memberId)
                .orElseThrow(UserStudyNotFoundException::new);

        Study study = studyRepository.findById(studyId)
                .orElseThrow(StudyNotFoundException::new);

        userStudy.validateStudyMember();

        study.decreaseMemberCount();
        studyRepository.save(study);
        userStudyRepository.delete(userStudy);
    }

    /**
     * 스터디 멤버를 추방한다. 해당 스터디의 리더만 수행할 수 있다.
     *
     * @param studyId  스터디 ID
     * @param memberId 추방 멤버 ID
     * @param leaderId 리더 ID
     */
    @Transactional
    public void removeStudyMember(Long studyId, Long memberId, Long leaderId) {
        Study study = studyRepository.findById(studyId)
                .orElseThrow(StudyNotFoundException::new);

        UserStudy userStudy = userStudyRepository.findByStudyIdAndUserId(studyId, leaderId)
                .orElseThrow(UserStudyNotFoundException::new);

        userStudy.validateStudyLeader();

        study.decreaseMemberCount();

        studyRepository.save(study);
        userStudyRepository.deleteByStudyIdAndUserId(studyId, memberId);
    }

    /**
     * 스터디 리더를 위임한다. 해당 스터디의 리더만 수행할 수 있다.
     *
     * @param studyId  스터디 ID
     * @param memberId 멤버 ID
     * @param leaderId 리더 ID
     */
    @Transactional
    public void modifyStudyLeader(Long studyId, Long memberId, Long leaderId) {
        UserStudy leaderStudy = userStudyRepository.findByStudyIdAndUserId(studyId, leaderId)
                .orElseThrow(UserStudyNotFoundException::new);

        UserStudy memberStudy = userStudyRepository.findByStudyIdAndUserId(studyId, memberId)
                .orElseThrow(UserStudyNotFoundException::new);

        leaderStudy.delegateLeader(memberStudy);

        userStudyRepository.save(leaderStudy);
        userStudyRepository.save(memberStudy);
    }

    /**
     * 스터디 그룹원을 조회한다. 조회를 요청한 유저가 스터디에 속한 경우, 가장 먼저 반환한다.
     *
     * @param studyId 스터디ID
     * @param userId  유저ID
     * @return 스터디 그룹원 조회 DTO
     */
    public List<GetStudyMemberResponse> findStudyMember(Long studyId, Long userId) {
        LocalDateTime start = TimeUtil.startOfToday();
        LocalDateTime end = TimeUtil.startOfTomorrow();

        List<Object[]> membersWithRoles = userRepository.findAllByStudyIdOrderByCreatedAtAsc(studyId);
        List<Long> memberIds = membersWithRoles.stream()
                .map(memberWithRole -> ((User) memberWithRole[0]).getId())
                .toList();

        Map<Long, DailyStudySummary> dailyStudySummaryMap = loadDailyStudySummaryMap(
                memberIds, start, end);

        List<GetStudyMemberResponse> responses = membersWithRoles.stream()
                .map(memberWithRole -> mapToMemberResponse(memberWithRole, dailyStudySummaryMap))
                .collect(Collectors.toList());

        moveCurrentUserToTop(userId, responses, GetStudyMemberResponse::getUserId);
        return responses;
    }

    public List<GetStudyMemberManagementResponse> findStudyMemberManagement(Long studyId, Long userId) {
        if (!userStudyRepository.existsByStudyIdAndUserId(studyId, userId)) {
            throw new StudyAccessDeniedException();
        }

        List<GetStudyMemberManagementResponse> responses = userStudyRepository.findStudyMembersByStudyId(
                studyId);

        moveCurrentUserToTop(userId, responses, GetStudyMemberManagementResponse::getUserId);
        return responses;
    }

    public List<GetStudyAttendanceResponse> findStudyAttendance(
            LocalDate startDate,
            LocalDate endDate,
            Long studyId
    ) {
        List<User> users = userRepository.findAllByStudyId(studyId);
        List<Long> userIds = users.stream()
                .map(User::getId)
                .toList();

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();

        List<DailyStudyTimeDto> dailyStudyTimes = dailyStudySummaryRepository.findDailyStudyTimeByUserIdsAndPeriod(
                userIds,
                startDateTime,
                endDateTime
        );

        Map<Long, Map<LocalDate, Long>> studyTimeMap = new HashMap<>();
        Map<Long, Long> totalStudyTimeMap = new HashMap<>();

        accumulateStudyTimes(dailyStudyTimes, studyTimeMap, totalStudyTimeMap);

        return createStudyAttendanceResponses(startDate, endDate, users, studyTimeMap, totalStudyTimeMap);
    }

    private void accumulateStudyTimes(
            List<DailyStudyTimeDto> dailyStudyTimes,
            Map<Long, Map<LocalDate, Long>> studyTimeMap,
            Map<Long, Long> totalStudyTimeMap
    ) {
        for (DailyStudyTimeDto dailyStudyTime : dailyStudyTimes) {
            studyTimeMap.computeIfAbsent(dailyStudyTime.getUserId(), k -> new HashMap<>())
                    .put(dailyStudyTime.getDate(), dailyStudyTime.getStudyTime());

            totalStudyTimeMap.merge(dailyStudyTime.getUserId(), dailyStudyTime.getStudyTime(), Long::sum);
        }
    }

    private List<GetStudyAttendanceResponse> createStudyAttendanceResponses(
            LocalDate startDate,
            LocalDate endDate,
            List<User> users,
            Map<Long, Map<LocalDate, Long>> studyTimeMap,
            Map<Long, Long> totalStudyTimeMap
    ) {
        return users.stream()
                .map(user -> {
                    Map<LocalDate, Long> userStudyTimeMap = studyTimeMap.getOrDefault(user.getId(), Map.of());
                    List<String> studyTimes = buildStudyAttendanceTimes(startDate, endDate, userStudyTimeMap);

                    return GetStudyAttendanceResponse.of(user, studyTimes);
                })
                .sorted(Comparator.comparingLong(
                        (GetStudyAttendanceResponse response) ->
                                totalStudyTimeMap.getOrDefault(response.getUserId(), 0L)
                ).reversed())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private List<String> buildStudyAttendanceTimes(
            LocalDate startDate,
            LocalDate endDate,
            Map<LocalDate, Long> userStudyTimeMap
    ) {
        List<String> studyTimeList = new ArrayList<>();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            Long studyTime = userStudyTimeMap.get(date);

            studyTimeList.add(
                    studyTime != null && studyTime > 0
                            ? TimeUtil.toTimeFormat(studyTime)
                            : null
            );
        }

        return studyTimeList;
    }


    private <T> void moveCurrentUserToTop(
            Long userId,
            List<T> responses,
            Function<T, Long> extractUserId
    ) {
        responses.stream()
                .filter(response -> Objects.equals(extractUserId.apply(response), userId))
                .findFirst()
                .ifPresent(currentUser -> {
                    responses.remove(currentUser);
                    responses.add(0, currentUser);
                });
    }

    private int countCompletedMembers(Study study) {
        LocalDateTime startOfDay = TimeUtil.startOfToday();
        LocalDateTime endOfDay = TimeUtil.startOfTomorrow();

        List<User> members = userRepository.findAllByStudyId(study.getId());

        List<Long> memberIds = members.stream()
                .map(User::getId)
                .toList();

        List<DailyStudySummary> todaySummaries =
                dailyStudySummaryRepository.findAllByUserIdsAndCreatedAt(
                        memberIds,
                        startOfDay,
                        endOfDay
                );

        return (int) todaySummaries.stream()
                .filter(study::isAchieved)
                .count();
    }

    private String updateStudyImage(PatchStudyInfoRequest request, Study study) {
        if (request.getStudyImage() != null) {
            String studyImageUrl = s3Service.uploadFile(request.getStudyImage());
            String oldUrl = study.changeImageUrl(studyImageUrl);
            s3Service.deleteFile(oldUrl);
            return studyImageUrl;
        }
        return null;
    }

    private GetStudyMemberResponse mapToMemberResponse(
            Object[] memberWithRole,
            Map<Long, DailyStudySummary> dailyStudySummaryMap
    ) {
        User member = (User) memberWithRole[0];
        StudyRole role = (StudyRole) memberWithRole[1];

        DailyStudySummary todaySummary = dailyStudySummaryMap.get(member.getId());

        if (todaySummary != null) {
            return GetStudyMemberResponse.of(member, todaySummary, role);
        }
        return GetStudyMemberResponse.of(member, role);
    }

    private Map<Long, DailyStudySummary> loadDailyStudySummaryMap(
            List<Long> memberIds,
            LocalDateTime startOfDay,
            LocalDateTime endOfDay
    ) {
        return dailyStudySummaryRepository
                .findAllByUserIdsAndCreatedAt(memberIds, startOfDay, endOfDay)
                .stream()
                .collect(Collectors.toMap(
                        DailyStudySummary::getUserId,
                        dailyStudySummary -> dailyStudySummary
                ));
    }
}
