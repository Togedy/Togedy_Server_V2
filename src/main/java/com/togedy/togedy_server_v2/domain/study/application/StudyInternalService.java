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
import com.togedy.togedy_server_v2.domain.study.dto.StudyMemberRoleDto;
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
     * 스터디 단건 정보를 조회한다.
     * <p>
     * 요청한 사용자의 스터디 참여 여부를 함께 반환하며, 요청 사용자가 해당 스터디의 리더인 경우에 한해 스터디 비밀번호를 포함한 정보를 반환한다.
     * </p>
     * <p>
     * 챌린지 스터디인 경우 완료한 멤버 수(completedCount)를 함께 조회하며, 일반 스터디의 경우 해당 값은 {@code null}이다.
     * </p>
     *
     * @param studyId 조회할 스터디 ID
     * @param userId  정보를 요청한 사용자 ID
     * @return 스터디 정보 및 참여 여부를 포함한 DTO
     * @throws StudyNotFoundException       주어진 {@code studyId}에 해당하는 스터디가 존재하지 않는 경우
     * @throws StudyLeaderNotFoundException 스터디는 존재하지만 리더 정보를 조회할 수 없는 경우
     */
    public GetStudyResponse findStudyInfo(Long studyId, Long userId) {
        boolean isJoined = userStudyRepository.existsByStudyIdAndUserId(studyId, userId);

        Study study = studyRepository.findById(studyId)
                .orElseThrow(StudyNotFoundException::new);

        User leader = userRepository.findByStudyIdAndRole(study.getId(), StudyRole.LEADER)
                .orElseThrow(StudyLeaderNotFoundException::new);

        Integer completedCount = study.isChallengeStudy()
                ? countCompletedChallengeMembers(study)
                : null;

        boolean isStudyLeader = leader.getId()
                .equals(userId);

        if (isStudyLeader) {
            return GetStudyResponse.ofLeader(isJoined, study, leader, completedCount);
        }

        return GetStudyResponse.ofMember(isJoined, study, leader, completedCount);
    }

    /**
     * 스터디를 삭제한다.
     * <p>
     * 해당 스터디의 리더만 수행할 수 있으며, 리더 여부는 {@code UserStudy} 정보를 통해 검증한다.
     * </p>
     * <p>
     * 스터디 삭제 시 다음 작업이 함께 수행된다.
     * <ul>
     *     <li>스터디 이미지(S3) 삭제</li>
     *     <li>해당 스터디에 속한 모든 유저-스터디 연관 관계 삭제</li>
     *     <li>스터디 엔티티 삭제</li>
     * </ul>
     * </p>
     *
     * @param studyId  삭제할 스터디 ID
     * @param leaderId 삭제를 요청한 리더 사용자 ID
     * @throws UserStudyNotFoundException 요청한 사용자가 해당 스터디에 속해 있지 않은 경우
     * @throws StudyNotFoundException     삭제 대상 스터디가 존재하지 않는 경우
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
     * 스터디의 기본 정보를 수정한다.
     * <p>
     * 해당 스터디의 리더만 수행할 수 있으며, 리더 여부는 {@code UserStudy} 정보를 통해 검증한다.
     * </p>
     * <p>
     * 요청 DTO에 이미지 변경 정보가 포함된 경우, 기존 이미지를 갱신하고 새로운 이미지 URL을 스터디 정보에 반영한다.
     * </p>
     *
     * @param request  변경할 스터디 정보 DTO
     * @param studyId  수정 대상 스터디 ID
     * @param leaderId 수정을 요청한 리더 사용자 ID
     * @throws UserStudyNotFoundException 요청한 사용자가 해당 스터디의 리더가 아닌 경우
     * @throws StudyNotFoundException     수정 대상 스터디가 존재하지 않는 경우
     */
    @Transactional
    public void modifyStudyInfo(PatchStudyInfoRequest request, Long studyId, Long leaderId) {
        UserStudy userStudy = userStudyRepository.findByStudyIdAndUserId(studyId, leaderId)
                .orElseThrow(UserStudyNotFoundException::new);

        userStudy.validateStudyLeader();

        Study study = studyRepository.findById(studyId)
                .orElseThrow(StudyNotFoundException::new);

        String studyImageUrl = replaceStudyImage(request, study);

        study.updateInfo(
                request.getStudyName(),
                request.getStudyDescription(),
                request.getStudyTag(),
                request.getStudyPassword(),
                studyImageUrl
        );
        studyRepository.save(study);
    }

    /**
     * 스터디의 최대 인원 수를 변경한다.
     * <p>
     * 최대 인원은 기존에 설정된 값보다 큰 값으로만 변경할 수 있으며, 해당 스터디의 리더만 수행할 수 있다.
     * </p>
     * <p>
     * 리더 여부는 {@code UserStudy} 정보를 통해 검증한다.
     * </p>
     *
     * @param request  변경할 최대 인원 정보를 담은 DTO
     * @param studyId  대상 스터디 ID
     * @param leaderId 변경을 요청한 리더 사용자 ID
     * @throws UserStudyNotFoundException 요청한 사용자가 해당 스터디의 리더가 아닌 경우
     * @throws StudyNotFoundException     대상 스터디가 존재하지 않는 경우
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
     * 스터디에 새로운 멤버를 등록한다.
     * <p>
     * 스터디 비밀번호를 검증한 후 참여를 허용하며, 현재 스터디 인원이 최대 인원에 도달한 경우 참여할 수 없다.
     * </p>
     * <p>
     * 멤버 등록 시 다음 작업이 하나의 트랜잭션으로 처리된다.
     * <ul>
     *     <li>스터디 멤버 수 증가</li>
     *     <li>유저-스터디 연관 정보 생성</li>
     * </ul>
     * </p>
     *
     * @param request 스터디 입장 요청 DTO (비밀번호 포함)
     * @param studyId 참여할 스터디 ID
     * @param userId  참여를 요청한 사용자 ID
     * @throws StudyNotFoundException 참여 대상 스터디가 존재하지 않는 경우
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
     * 사용자가 스터디에서 자발적으로 퇴장한다.
     * <p>
     * 해당 스터디의 리더는 퇴장할 수 없으며, 일반 멤버만 수행할 수 있다.
     * </p>
     * <p>
     * 퇴장 시 다음 작업이 하나의 트랜잭션으로 처리된다.
     * <ul>
     *     <li>스터디 멤버 수 감소</li>
     *     <li>유저-스터디 연관 정보 삭제</li>
     * </ul>
     * </p>
     *
     * @param studyId  퇴장할 스터디 ID
     * @param memberId 퇴장을 요청한 멤버 사용자 ID
     * @throws UserStudyNotFoundException 요청한 사용자가 해당 스터디의 멤버가 아닌 경우
     * @throws StudyNotFoundException     대상 스터디가 존재하지 않는 경우
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
     * 스터디에서 특정 멤버를 강제로 추방한다.
     * <p>
     * 해당 스터디의 리더만 수행할 수 있으며, 리더 여부는 {@code UserStudy} 정보를 통해 검증한다.
     * </p>
     * <p>
     * 추방 시 다음 작업이 하나의 트랜잭션으로 처리된다.
     * <ul>
     *     <li>스터디 멤버 수 감소</li>
     *     <li>추방 대상 멤버의 유저-스터디 연관 정보 삭제</li>
     * </ul>
     * </p>
     *
     * @param studyId  대상 스터디 ID
     * @param memberId 추방할 멤버 사용자 ID
     * @param leaderId 추방을 요청한 리더 사용자 ID
     * @throws StudyNotFoundException     대상 스터디가 존재하지 않는 경우
     * @throws UserStudyNotFoundException 요청한 사용자가 해당 스터디의 리더가 아닌 경우
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
     * 스터디 리더 권한을 다른 멤버에게 위임한다.
     * <p>
     * 해당 스터디의 현재 리더만 수행할 수 있으며, 리더와 위임 대상 멤버는 모두 해당 스터디에 속해 있어야 한다.
     * </p>
     * <p>
     * 리더 위임 시 다음 작업이 하나의 트랜잭션으로 처리된다.
     * <ul>
     *     <li>기존 리더의 역할을 MEMBER로 변경</li>
     *     <li>위임 대상 멤버의 역할을 LEADER로 변경</li>
     * </ul>
     * </p>
     *
     * @param studyId  대상 스터디 ID
     * @param memberId 리더 권한을 위임받을 멤버 사용자 ID
     * @param leaderId 리더 위임을 요청한 현재 리더 사용자 ID
     * @throws UserStudyNotFoundException 리더 또는 위임 대상 멤버가 해당 스터디에 속해 있지 않은 경우
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
     * 스터디에 속한 모든 멤버 목록을 조회한다.
     * <p>
     * 각 멤버의 역할 및 오늘 기준 공부 정보를 포함하여 반환한다.
     * </p>
     * <p>
     * 조회를 요청한 사용자가 스터디에 속해 있는 경우, 해당 사용자를 결과 목록의 가장 첫 번째로 정렬한다.
     * </p>
     *
     * @param studyId 조회 대상 스터디 ID
     * @param userId  조회를 요청한 사용자 ID
     * @return 스터디 멤버 목록 DTO
     */
    public List<GetStudyMemberResponse> findStudyMember(Long studyId, Long userId) {
        LocalDate today = LocalDate.now();

        List<StudyMemberRoleDto> membersWithRoles = userRepository.findAllByStudyIdOrderByCreatedAtAsc(studyId);
        List<Long> memberIds = membersWithRoles.stream()
                .map(studyMemberRoleDto -> studyMemberRoleDto.getUser().getId())
                .toList();

        Map<Long, DailyStudySummary> dailyStudySummaryMap = findDailyStudySummaryMapByUserIds(memberIds, today);

        List<GetStudyMemberResponse> responses = membersWithRoles.stream()
                .map(studyMemberRoleDto -> buildMemberResponse(studyMemberRoleDto, dailyStudySummaryMap))
                .collect(Collectors.toList());

        moveCurrentUserToTop(userId, responses, GetStudyMemberResponse::getUserId);
        return responses;
    }

    /**
     * 스터디 멤버 관리 화면에 필요한 멤버 정보를 조회한다.
     * <p>
     * 조회를 요청한 사용자가 해당 스터디에 속해 있지 않은 경우 접근이 제한된다.
     * </p>
     * <p>
     * 조회 결과에서는 요청한 사용자를 목록의 가장 첫 번째로 정렬한다.
     * </p>
     *
     * @param studyId 조회 대상 스터디 ID
     * @param userId  조회를 요청한 사용자 ID
     * @return 스터디 멤버 관리용 DTO 목록
     * @throws StudyAccessDeniedException 요청한 사용자가 해당 스터디에 속해 있지 않은 경우
     */
    public List<GetStudyMemberManagementResponse> findStudyMemberManagement(Long studyId, Long userId) {
        if (!userStudyRepository.existsByStudyIdAndUserId(studyId, userId)) {
            throw new StudyAccessDeniedException();
        }

        List<GetStudyMemberManagementResponse> responses = userStudyRepository.findStudyMembersByStudyId(
                studyId);

        moveCurrentUserToTop(userId, responses, GetStudyMemberManagementResponse::getUserId);
        return responses;
    }

    /**
     * 스터디 멤버들의 출석 여부 및 기간별 학습 시간을 조회한다.
     * <p>
     * 지정한 기간 동안 각 멤버의 일별 학습 시간을 집계하며, 전체 학습 시간과 함께 반환한다.
     * </p>
     * <p>
     * 조회 기간은 {@code startDate}부터 {@code endDate}까지이며, 종료 날짜를 포함하여 집계한다.
     * </p>
     *
     * @param startDate 조회 시작 날짜 (포함)
     * @param endDate   조회 종료 날짜 (포함)
     * @param studyId   조회 대상 스터디 ID
     * @return 스터디 출석 및 학습 시간 DTO 목록
     */
    public List<GetStudyAttendanceResponse> findStudyAttendance(
            LocalDate startDate,
            LocalDate endDate,
            Long studyId
    ) {
        List<User> users = userRepository.findAllByStudyId(studyId);
        List<Long> userIds = users.stream()
                .map(User::getId)
                .toList();

        List<DailyStudyTimeDto> dailyStudyTimes = dailyStudySummaryRepository.findDailyStudyTimeByUserIdsAndPeriod(
                userIds,
                startDate,
                endDate
        );

        Map<Long, Map<LocalDate, Long>> studyTimeMap = new HashMap<>();
        Map<Long, Long> totalStudyTimeMap = new HashMap<>();

        accumulateStudyTimes(dailyStudyTimes, studyTimeMap, totalStudyTimeMap);

        return buildStudyAttendanceResponses(startDate, endDate, users, studyTimeMap, totalStudyTimeMap);
    }

    /**
     * 사용자별 일일 공부 시간과 총 공부 시간을 누적 집계한다.
     * <p>
     * {@code dailyStudyTimes}에 포함된 데이터를 기반으로 사용자 ID 기준의 날짜별 공부 시간 맵과 사용자별 총 공부 시간 맵을 갱신한다.
     * </p>
     *
     * @param dailyStudyTimes   일별 공부 시간 조회 결과 목록
     * @param studyTimeMap      사용자별 날짜별 공부 시간 맵 (누적 대상)
     * @param totalStudyTimeMap 사용자별 총 공부 시간 맵 (누적 대상)
     */
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

    /**
     * 스터디 출석 및 공부 시간 조회 응답 DTO 목록을 생성한다.
     * <p>
     * 각 사용자에 대해 조회 기간 동안의 일별 공부 시간을 생성하고, 사용자별 총 공부 시간을 기준으로 내림차순 정렬한다.
     * </p>
     *
     * @param startDate         조회 시작 날짜 (포함)
     * @param endDate           조회 종료 날짜 (포함)
     * @param users             스터디에 속한 사용자 목록
     * @param studyTimeMap      사용자별 날짜별 공부 시간 맵
     * @param totalStudyTimeMap 사용자별 총 공부 시간 맵
     * @return 스터디 출석 및 공부 시간 DTO 목록
     */
    private List<GetStudyAttendanceResponse> buildStudyAttendanceResponses(
            LocalDate startDate,
            LocalDate endDate,
            List<User> users,
            Map<Long, Map<LocalDate, Long>> studyTimeMap,
            Map<Long, Long> totalStudyTimeMap
    ) {
        return users.stream()
                .map(user -> {
                    Map<LocalDate, Long> userStudyTimeMap = studyTimeMap.getOrDefault(user.getId(), Map.of());
                    List<String> studyTimes = buildDailyStudyAttendanceTimes(startDate, endDate, userStudyTimeMap);

                    return GetStudyAttendanceResponse.of(user, studyTimes);
                })
                .sorted(Comparator.comparingLong(
                        (GetStudyAttendanceResponse response) ->
                                totalStudyTimeMap.getOrDefault(response.getUserId(), 0L)
                ).reversed())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * 조회 기간 동안의 일별 공부 시간 목록을 생성한다.
     * <p>
     * 날짜별 공부 시간이 존재하고 0보다 큰 경우 포맷된 문자열을 반환하며, 기록이 없거나 0인 경우 {@code null}을 추가한다.
     * </p>
     *
     * @param startDate        조회 시작 날짜 (포함)
     * @param endDate          조회 종료 날짜 (포함)
     * @param userStudyTimeMap 사용자별 날짜별 공부 시간 맵
     * @return 날짜 순서대로 정렬된 공부 시간 문자열 목록
     */
    private List<String> buildDailyStudyAttendanceTimes(
            LocalDate startDate,
            LocalDate endDate,
            Map<LocalDate, Long> userStudyTimeMap
    ) {
        List<String> studyTimeList = new ArrayList<>();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            Long studyTime = userStudyTimeMap.get(date);

            studyTimeList.add(
                    studyTime != null && studyTime > 0
                            ? TimeUtil.formatSecondsToHms(studyTime)
                            : null
            );
        }

        return studyTimeList;
    }

    /**
     * 응답 목록에서 특정 사용자를 가장 앞에 위치시키도록 재정렬한다.
     * <p>
     * {@code extractUserId}를 통해 각 요소의 사용자 ID를 추출하여 비교하며, 대상 사용자가 존재하는 경우 해당 요소를 제거한 뒤 목록의 첫 번째 위치로 이동시킨다.
     * </p>
     *
     * @param userId        가장 앞에 위치시킬 사용자 ID
     * @param responses     재정렬 대상 목록 (in-place 변경)
     * @param extractUserId 요소에서 사용자 ID를 추출하는 함수
     * @param <T>           응답 DTO 타입
     */
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

    /**
     * 오늘 기준 스터디 챌린지를 달성한 멤버 수를 집계한다.
     * <p>
     * 스터디에 속한 모든 멤버의 오늘 일일 학습 기록을 조회한 뒤, 스터디의 챌린지 달성 조건을 만족하는 멤버 수를 반환한다.
     * </p>
     *
     * @param study 챌린지 스터디 엔티티
     * @return 챌린지 달성 멤버 수
     */
    private int countCompletedChallengeMembers(Study study) {
        LocalDate today = LocalDate.now();

        List<User> members = userRepository.findAllByStudyId(study.getId());

        List<Long> memberIds = members.stream()
                .map(User::getId)
                .toList();

        List<DailyStudySummary> todaySummaries =
                dailyStudySummaryRepository.findAllByUserIdsAndDate(memberIds, today);

        return (int) todaySummaries.stream()
                .filter(study::isAchieved)
                .count();
    }

    /**
     * 스터디 이미지를 변경하고 새로운 이미지 URL을 반환한다.
     * <p>
     * 요청에 이미지 파일이 포함된 경우, 새 이미지를 업로드한 뒤 기존 이미지를 삭제하고 변경된 이미지 URL을 반환한다.
     * </p>
     * <p>
     * 이미지 변경 요청이 없는 경우 {@code null}을 반환한다.
     * </p>
     *
     * @param request 이미지 변경 요청 DTO
     * @param study   대상 스터디 엔티티
     * @return 변경된 이미지 URL, 변경이 없는 경우 {@code null}
     */
    private String replaceStudyImage(PatchStudyInfoRequest request, Study study) {
        if (request.getStudyImage() != null) {
            String studyImageUrl = s3Service.uploadFile(request.getStudyImage());
            String oldUrl = study.changeImageUrl(studyImageUrl);
            s3Service.deleteFile(oldUrl);
            return studyImageUrl;
        }
        return null;
    }

    /**
     * 스터디 멤버 정보를 조회 응답 DTO로 변환한다.
     * <p>
     * 오늘의 학습 요약 정보가 존재하는 경우 이를 포함하여 DTO를 생성하며, 존재하지 않는 경우 기본 멤버 정보만 포함한다.
     * </p>
     *
     * @param memberWithRole       사용자 엔티티와 역할 정보가 포함된 조회 결과
     * @param dailyStudySummaryMap 사용자별 오늘 학습 요약 정보 맵
     * @return 스터디 멤버 조회 응답 DTO
     */
    private GetStudyMemberResponse buildMemberResponse(
            StudyMemberRoleDto studyMemberRoleDto,
            Map<Long, DailyStudySummary> dailyStudySummaryMap
    ) {
        DailyStudySummary todaySummary = dailyStudySummaryMap.get(studyMemberRoleDto.getUser().getId());

        if (todaySummary != null) {
            return GetStudyMemberResponse.of(studyMemberRoleDto.getUser(), todaySummary,
                    studyMemberRoleDto.getStudyRole());
        }
        return GetStudyMemberResponse.of(studyMemberRoleDto.getUser(), studyMemberRoleDto.getStudyRole());
    }

    /**
     * 지정한 기간 내 사용자별 일일 학습 요약 정보를 맵으로 조회한다.
     *
     * @param memberIds 조회 대상 사용자 ID 목록
     * @param date      조회 날짜
     * @return 사용자 ID를 키로 하는 일일 학습 요약 정보 맵
     */
    private Map<Long, DailyStudySummary> findDailyStudySummaryMapByUserIds(
            List<Long> memberIds,
            LocalDate date
    ) {
        return dailyStudySummaryRepository
                .findAllByUserIdsAndDate(memberIds, date)
                .stream()
                .collect(Collectors.toMap(
                        DailyStudySummary::getUserId,
                        dailyStudySummary -> dailyStudySummary
                ));
    }
}
