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
import com.togedy.togedy_server_v2.domain.user.dto.MyPageStudyDto;
import com.togedy.togedy_server_v2.domain.user.entity.AuthProvider;
import com.togedy.togedy_server_v2.domain.user.entity.User;
import com.togedy.togedy_server_v2.domain.user.exception.user.DuplicateEmailException;
import com.togedy.togedy_server_v2.domain.user.exception.user.DuplicateNicknameException;
import com.togedy.togedy_server_v2.domain.user.exception.user.UserNotFoundException;
import com.togedy.togedy_server_v2.global.util.TimeUtil;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final StudyRepository studyRepository;
    private final UserStudyRepository userStudyRepository;
    private final AuthProviderRepository authProviderRepository;
    private final DailyStudySummaryRepository dailyStudySummaryRepository;

    private final static int FIND_STUDY_COUNT = 2;

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

        authProviderRepository.save(
                AuthProvider.local(user, request.getEmail())
        );

        return user.getId();
    }

    @Transactional(readOnly = true)
    public User loadUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }

    public GetMyPageResponse findMyPage(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Long totalStudyTime = dailyStudySummaryRepository.findTotalStudyTimeByUserId(userId).orElse(0L);

        Pageable pageable = PageRequest.of(0, FIND_STUDY_COUNT);

        List<Study> studies = studyRepository.findRecentStudiesByUserId(userId, pageable);

        List<Long> studyIds = studies.stream()
                .map(Study::getId)
                .toList();

        List<UserStudy> userStudies = userStudyRepository.findAllByStudyIds(studyIds);

        Map<Long, List<UserStudy>> userStudyMap = userStudies.stream()
                .collect(Collectors.groupingBy(UserStudy::getStudyId));

        List<MyPageStudyDto> studyDtos = studies.stream()
                .map(study -> buildMyPageStudyDto(study, userStudyMap))
                .toList();

        return GetMyPageResponse.from(user, TimeUtil.formatSecondsToHms(totalStudyTime), studyDtos);
    }

    private MyPageStudyDto buildMyPageStudyDto(Study study, Map<Long, List<UserStudy>> userStudyMap) {
        if (study.isChallengeStudy()) {
            int completedMemberCount = 0;

            List<UserStudy> userStudies = userStudyMap.get(study.getId());

            List<Long> userIds = userStudies.stream()
                    .map(UserStudy::getUserId)
                    .toList();

            List<DailyStudySummary> dailyStudySummaries = dailyStudySummaryRepository.findAllByUserIdsAndDate(
                    userIds,
                    LocalDate.now()
            );

            Map<Long, Long> studySummaryMap = dailyStudySummaries.stream()
                    .collect(Collectors.toMap(
                            DailyStudySummary::getUserId,
                            DailyStudySummary::getStudyTime
                    ));

            for (Long userId : userIds) {
                Long studyTime = studySummaryMap.getOrDefault(userId, 0L);

                if (studyTime >= study.getGoalTime()) {
                    completedMemberCount++;
                }
            }

            boolean isCompleted = study.getMemberCount() == completedMemberCount;

            return MyPageStudyDto.from(study, isCompleted, completedMemberCount);
        }

        return MyPageStudyDto.from(study);
    }
}
