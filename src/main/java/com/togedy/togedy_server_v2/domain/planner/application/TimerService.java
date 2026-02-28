package com.togedy.togedy_server_v2.domain.planner.application;

import com.togedy.togedy_server_v2.domain.planner.dao.StudySubjectRepository;
import com.togedy.togedy_server_v2.domain.planner.dao.StudyTimeRepository;
import com.togedy.togedy_server_v2.domain.planner.dto.GetRunningTimerResponse;
import com.togedy.togedy_server_v2.domain.planner.dto.GetTimerTotalResponse;
import com.togedy.togedy_server_v2.domain.planner.dto.PostTimerStartRequest;
import com.togedy.togedy_server_v2.domain.planner.dto.PostTimerStartResponse;
import com.togedy.togedy_server_v2.domain.planner.dto.PostTimerStopRequest;
import com.togedy.togedy_server_v2.domain.planner.dto.PostTimerStopResponse;
import com.togedy.togedy_server_v2.domain.planner.dto.SubjectStudyTimeItemResponse;
import com.togedy.togedy_server_v2.domain.planner.entity.StudySubject;
import com.togedy.togedy_server_v2.domain.planner.entity.StudyTime;
import com.togedy.togedy_server_v2.domain.planner.exception.InvalidStudySubjectException;
import com.togedy.togedy_server_v2.domain.planner.exception.StudySubjectNotFoundException;
import com.togedy.togedy_server_v2.domain.planner.exception.StudySubjectNotOwnedException;
import com.togedy.togedy_server_v2.domain.planner.exception.TimerAlreadyRunningException;
import com.togedy.togedy_server_v2.domain.planner.exception.TimerAlreadyStoppedException;
import com.togedy.togedy_server_v2.domain.planner.exception.TimerNotFoundException;
import com.togedy.togedy_server_v2.domain.planner.exception.TimerNotOwnedException;
import com.togedy.togedy_server_v2.global.util.TimeUtil;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TimerService {

    private final StudySubjectRepository studySubjectRepository;
    private final StudyTimeRepository studyTimeRepository;

    @Transactional
    public PostTimerStartResponse startTimer(PostTimerStartRequest request, Long userId) {
        validateStartRequest(request);

        if (studyTimeRepository.findByUserIdAndEndTimeIsNull(userId).isPresent()) {
            throw new TimerAlreadyRunningException();
        }

        StudySubject studySubject = studySubjectRepository.findActiveById(request.getStudySubjectId())
                .orElseThrow(StudySubjectNotFoundException::new);

        if (!studySubject.getUserId().equals(userId)) {
            throw new StudySubjectNotOwnedException();
        }

        LocalDateTime startTime = LocalDateTime.now();

        StudyTime studyTime = StudyTime.builder()
                .userId(userId)
                .studySubjectId(studySubject.getId())
                .startTime(startTime)
                .endTime(null)
                .build();

        Long timerId = studyTimeRepository.save(studyTime).getId();
        return PostTimerStartResponse.of(timerId, startTime);
    }

    @Transactional
    public PostTimerStopResponse stopTimer(PostTimerStopRequest request, Long userId) {
        validateStopRequest(request);

        StudyTime studyTime = studyTimeRepository.findById(request.getTimerId())
                .orElseThrow(TimerNotFoundException::new);

        if (!studyTime.getUserId().equals(userId)) {
            throw new TimerNotOwnedException();
        }
        if (studyTime.getEndTime() != null) {
            throw new TimerAlreadyStoppedException();
        }

        LocalDateTime endTime = LocalDateTime.now();
        studyTime.stop(endTime);
        return PostTimerStopResponse.of(studyTime.getId(), studyTime.getStartTime(), endTime);
    }

    @Transactional(readOnly = true)
    public GetRunningTimerResponse findRunningTimer(Long userId) {
        return studyTimeRepository.findByUserIdAndEndTimeIsNull(userId)
                .map(studyTime -> GetRunningTimerResponse.of(
                        studyTime.getId(),
                        studyTime.getStudySubjectId(),
                        studyTime.getStartTime()
                ))
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<SubjectStudyTimeItemResponse> findTodaySubjectStudyTimes(Long userId) {
        List<StudySubject> studySubjects = studySubjectRepository.findAllByUserId(userId);
        if (studySubjects.isEmpty()) {
            return List.of();
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dayStart = TimeUtil.startOfStudyDay(now);
        LocalDateTime dayEnd = TimeUtil.endOfStudyDay(now);

        List<StudyTime> studyTimes = studyTimeRepository.findDailyStudyTimesByUserId(userId, dayStart, dayEnd);
        Map<Long, Long> studyTimeBySubjectId = studyTimes.stream()
                .collect(Collectors.groupingBy(
                        StudyTime::getStudySubjectId,
                        Collectors.summingLong(studyTime ->
                                TimeUtil.calculateStudySeconds(studyTime.getStartTime(), studyTime.getEndTime()))
                ));

        List<SubjectStudyTimeItemResponse> response = studySubjects.stream()
                .map(subject -> SubjectStudyTimeItemResponse.of(
                        subject.getId(),
                        subject.getName(),
                        studyTimeBySubjectId.getOrDefault(subject.getId(), 0L)
                ))
                .toList();

        return response;
    }

    @Transactional(readOnly = true)
    public GetTimerTotalResponse findTodayTotalStudyTime(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dayStart = TimeUtil.startOfStudyDay(now);
        LocalDateTime dayEnd = TimeUtil.endOfStudyDay(now);

        long totalStudyTime = studyTimeRepository.findDailyStudyTimesByUserId(userId, dayStart, dayEnd).stream()
                .mapToLong(studyTime -> TimeUtil.calculateStudySeconds(studyTime.getStartTime(), studyTime.getEndTime()))
                .sum();

        return GetTimerTotalResponse.of(totalStudyTime);
    }

    private void validateStartRequest(PostTimerStartRequest request) {
        if (request == null || request.getStudySubjectId() == null || request.getStudySubjectId() <= 0) {
            throw new InvalidStudySubjectException();
        }
    }

    private void validateStopRequest(PostTimerStopRequest request) {
        if (request == null || request.getTimerId() == null || request.getTimerId() <= 0) {
            throw new TimerNotFoundException();
        }
    }
}
